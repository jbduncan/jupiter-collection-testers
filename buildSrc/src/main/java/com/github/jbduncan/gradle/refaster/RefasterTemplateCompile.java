package com.github.jbduncan.gradle.refaster;

import static com.github.jbduncan.gradle.refaster.Constants.ERROR_PRONE_CORE_MAVEN_COORDINATES_FORMAT;
import static com.github.jbduncan.gradle.refaster.Constants.REFASTER_MAVEN_COORDINATES_FORMAT;
import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

public class RefasterTemplateCompile extends DefaultTask {
  private String errorProneVersion;
  private Iterable<File> refasterTemplates;

  private File compiledRefasterTemplatesDir;
  private File compiledRefasterClassesDir;

  @Input
  public String getErrorProneVersion() {
    return errorProneVersion;
  }

  public void setErrorProneVersion(String errorProneVersion) {
    this.errorProneVersion = errorProneVersion;
  }

  @InputFiles
  @SkipWhenEmpty
  public Iterable<File> getRefasterTemplates() {
    return refasterTemplates;
  }

  public void setRefasterTemplates(Iterable<File> refasterTemplates) {
    this.refasterTemplates = refasterTemplates;
  }

  @OutputDirectory
  public File getCompiledRefasterTemplatesDir() {
    return compiledRefasterTemplatesDir;
  }

  public void setCompiledRefasterTemplatesDir(File compiledRefasterTemplatesDir) {
    this.compiledRefasterTemplatesDir = compiledRefasterTemplatesDir;
  }

  @OutputDirectory
  public File getCompiledRefasterClassesDir() {
    return compiledRefasterClassesDir;
  }

  public void setCompiledRefasterClassesDir(File compiledRefasterClassesDir) {
    this.compiledRefasterClassesDir = compiledRefasterClassesDir;
  }

  @TaskAction
  public void compile(IncrementalTaskInputs inputs) {
    // TODO: Remove this Java-8-only restriction
    int javaVersion = Integer.parseInt(JavaVersion.current().getMajorVersion());
    if (javaVersion != 8) {
      throw new GradleException(
          "Task of type " + getClass() + " requires Java 8. "
              + "Currently executing with Java "
              + javaVersion
              + ".");
    }

    checkSpecified(errorProneVersion, "errorProneVersion");
    checkSpecified(refasterTemplates, "refasterTemplates");
    checkSpecified(compiledRefasterTemplatesDir, "compiledRefasterTemplatesDir");
    checkSpecified(compiledRefasterClassesDir, "compiledRefasterClassesDir");

    // TODO: Remove this restriction when able to compile on Java 9+
    if (!errorProneVersion.equals("2.1.0")) {
      throw new GradleException(
          "Currently only error-prone 2.1.0 is supported as a base for Refaster");
    }

    if (!inputs.isIncremental()) {
      deleteAllCompiledRefasterFiles();
    }
    Set<File> refasterTemplates = outOfDateRefasterTemplates(inputs);

    // TODO: When the restriction on the error-prone version to use is lifted, consider using the
    // gradle-errorprone-plugin's errorprone configuration instead, e.g.
    // "project.getConfigurations().getByName(ErrorProneBasePlugin.CONFIGURATION_NAME)..."
    Configuration refasterBootstrapClasspathConfiguration =
        getProject()
            .getConfigurations()
            .detachedConfiguration(
                Stream.of(
                        String.format(ERROR_PRONE_CORE_MAVEN_COORDINATES_FORMAT, errorProneVersion),
                        String.format(REFASTER_MAVEN_COORDINATES_FORMAT, errorProneVersion))
                    .map(getProject().getDependencies()::create)
                    .toArray(Dependency[]::new));
    Set<File> bootstrapClasspath =
        refasterBootstrapClasspathConfiguration.getResolvedConfiguration().getFiles();
    String bootstrapClasspathAsString =
        bootstrapClasspath.stream().map(Object::toString).collect(joining(File.pathSeparator));

    for (File refasterTemplate : refasterTemplates) {
      File compiledRefasterTemplatePath =
          Utils.toCompiledRefasterTemplateFile(
              refasterTemplate, getProject().getRootDir(), compiledRefasterTemplatesDir);
      getProject().mkdir(compiledRefasterTemplatePath.getParent());

      // TODO: When we depend on Java 9+, look into `ToolProvider` as a way to hopefully call
      // java.exe programmatically without spawning a new JVM process. (See:
      // http://in.relation.to/2017/12/06/06-calling-jdk-tools-programmatically-on-java-9/)
      getProject()
          .javaexec(
              j -> {
                j.setMain("");
                j.setArgs(
                    Arrays.asList(
                        String.format("-Xbootclasspath/p:%s", bootstrapClasspathAsString),
                        "com.google.errorprone.refaster.RefasterRuleCompiler",
                        refasterTemplate,
                        "--out",
                        compiledRefasterTemplatePath,
                        "-d",
                        // TODO: Consider using getTemporaryDir()
                        compiledRefasterClassesDir));
              });
    }
  }

  private static <T> void checkSpecified(T value, String valueAsString) {
    if (value == null) {
      throw new GradleException(String.format("You must specify '%s'", valueAsString));
    }
  }

  private void deleteAllCompiledRefasterFiles() {
    // TODO: If Guava is ever imported, consider using com.google.common.io.Files#fileTraverser
    // instead of Files#find
    try (Stream<Path> compiledRefasterFiles =
        Files.find(
            compiledRefasterTemplatesDir.toPath(),
            Integer.MAX_VALUE,
            // TODO: If Guava is ever imported and if MoreFiles is ever promoted from @Beta,
            // consider using MoreFiles.getFileExtension(path).equals("refaster") here
            (path, attr) -> FilenameUtils.isExtension(path.toString(), "refaster"))) {
      Iterable<Path> asIterable = compiledRefasterFiles::iterator;
      for (Path file : asIterable) {
        Files.deleteIfExists(file);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Set<File> outOfDateRefasterTemplates(IncrementalTaskInputs inputs) {
    Set<File> result = new LinkedHashSet<>();
    if (!inputs.isIncremental()) {
      getRefasterTemplates().forEach(result::add);
    } else {
      inputs.outOfDate(inputFileDetails -> result.add(inputFileDetails.getFile()));
      inputs.removed(
          inputFileDetails -> removeAssociatedCompiledRefasterTemplate(inputFileDetails.getFile()));
    }
    return result;
  }

  private void removeAssociatedCompiledRefasterTemplate(File refasterTemplate) {
    File compiledRefasterTemplateFile =
        Utils.toCompiledRefasterTemplateFile(
            refasterTemplate, getProject().getRootDir(), compiledRefasterTemplatesDir);
    try {
      Files.deleteIfExists(compiledRefasterTemplateFile.toPath());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
