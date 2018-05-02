package com.github.jbduncan.gradle.refaster;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.ltgt.gradle.errorprone.ErrorProneToolChain;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.compile.JavaCompile;

public class RefasterPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    project.getPlugins().apply(JavaPlugin.class);

    project.configure(
        Collections.singleton(project), proj -> proj.apply(p -> p.plugin("net.ltgt.errorprone")));

    SourceSet refasterSourceSet =
        project.getConvention()
            .getPlugin(JavaPluginConvention.class)
            .getSourceSets()
            .create(
                "refaster",
                sourceSet -> sourceSet.java(
                    files -> {
                      files.srcDir("src/refaster/java");
                    }));

    String refasterBuildDir = String.format("%s/refaster", project.getBuildDir());
    File compiledRefasterTemplatePath =
        project.file(String.format("%s/template/Refaster.refaster", refasterBuildDir));

    String guavaVersion =
        Objects.requireNonNull(project.property("guavaVersion")).toString();
    String refasterErrorProneVersion =
        Objects.requireNonNull(project.property("refasterErrorProneVersion"))
            .toString();

    Configuration refasterCompile =
        project.getConfigurations()
            // TODO: Figure out why we need to retrieve an existing "refasterCompile" configuration
            // here instead of creating a new one.
            .getByName("refasterCompile")
            .setVisible(false)
            .setDescription("The Refaster artifacts needed by this plugin.");
    DependencyHandler dependencies = project.getDependencies();
    dependencies.add(
        refasterCompile.getName(),
        "com.google.guava:guava:" + guavaVersion);
    dependencies.add(
        refasterCompile.getName(),
        "com.google.errorprone:error_prone_core:" + refasterErrorProneVersion);
    Dependency errorproneRefaster =
        dependencies.add(
            refasterCompile.getName(),
            "com.google.errorprone:error_prone_refaster:" + refasterErrorProneVersion);

    Set<File> resolvedRefasterFiles =
        refasterCompile.getResolvedConfiguration().getFiles();
    File refasterJarPath =
        single(
            resolvedRefasterFiles
                .stream()
                .filter(file -> file.getName().startsWith(errorproneRefaster.getName())));
    Pattern errorproneJavacJarPattern = Pattern.compile("javac-.+.jar");
    File errorproneJavacJarPath =
        single(
            resolvedRefasterFiles
                .stream()
                .filter(
                    file -> errorproneJavacJarPattern.matcher(file.getName()).matches()));

    Path refasterSrcDir = single(refasterSourceSet.getJava().getSrcDirs()).toPath();
    Path refasterTemplatePath;
    try (Stream<Path> stream =
        Files.find(
            refasterSrcDir,
            Integer.MAX_VALUE,
            (path, attr) ->
                Files.isRegularFile(path, NOFOLLOW_LINKS)
                    && path.toString().endsWith(".java"))) {
      refasterTemplatePath = single(stream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    String compiledRefasterClassesDir = String.format("%s/refaster/classes", refasterBuildDir);

    // TODO: Make "compileRefasterTemplate" have UP-TO-DATE checking and maybe even be incremental.
    JavaExec compileRefasterTemplateTask =
        project.getTasks().create(
            "compileRefasterTemplate",
            JavaExec.class,
            task -> {
              task.doFirst(
                  unused -> {
                    int javaVersion = Integer
                        .parseInt(JavaVersion.current().getMajorVersion());
                    if (javaVersion != 8) {
                      throw new GradleException(
                          "The task 'compileRefasterTemplate' requires Java 8. "
                              + "Currently executing with Java "
                              + javaVersion
                              + ".");
                    }
                    project.mkdir(compiledRefasterTemplatePath.getParent());
                  });

              task.setMain("");

              // TODO: Adjust these arguments so that they work for Java 9+ as well. See:
              // http://errorprone.info/docs/installation -> "Java 9" for more information.
              task.args(
                  String.format(
                      "-Xbootclasspath/p:%s",
                      String.join(
                          File.pathSeparator,
                          refasterJarPath.toString(),
                          errorproneJavacJarPath.toString())),
                  "com.google.errorprone.refaster.RefasterRuleCompiler",
                  refasterTemplatePath,
                  "--out",
                  compiledRefasterTemplatePath,
                  "-d",
                  compiledRefasterClassesDir);
            });

    // FIXME: "refasterApply" and "refasterCheck" do not re-run if the output of
    // "compileRefasterTemplate" changes. Find a way of fixing it.

    // TODO: Consider redirecting all logging messages by the refaster(Check|Apply) sub-tasks to a
    // log file in the buildDir, as in https://stackoverflow.com/a/27679230/2252930

    JavaCompile refasterCheckTask =
        project.getTasks().create(
            "refasterCheck",
            JavaCompile.class,
            j -> j.setGroup(JavaBasePlugin.VERIFICATION_GROUP));
    project.getTasks().getByPath("check").dependsOn(refasterCheckTask);
    JavaCompile refasterApplyTask =
        project.getTasks().create(
            "refasterApply",
            JavaCompile.class,
            j -> j.setGroup(JavaBasePlugin.VERIFICATION_GROUP));

    for (String underlyingJavaCompileTaskName : Arrays.asList("compileJava", "compileTestJava")) {
      JavaCompile underlyingJavaCompileTask =
          project.getTasks().withType(JavaCompile.class).getByName(underlyingJavaCompileTaskName);

      JavaCompile refasterCheckSubTask =
          project.getTasks().create(
              "refasterCheckFor" + capitalise(underlyingJavaCompileTaskName),
              JavaCompile.class,
              j -> {
                j.dependsOn(compileRefasterTemplateTask);

                j.setToolChain(ErrorProneToolChain.create(project));

                j.setClasspath(underlyingJavaCompileTask.getClasspath());
                j.setSource(underlyingJavaCompileTask.getSource());

                String nullDir = Os.isFamily(Os.FAMILY_WINDOWS) ? "nul" : "/dev/null";
                j.getOptions()
                    .setCompilerArgs(
                        Arrays.asList(
                            "-Werror",
                            "-XepPatchChecks:refaster:" + compiledRefasterTemplatePath,
                            "-XepPatchLocation:" + nullDir));
                j.setDestinationDir(
                    project.file(
                        String.format(
                            "%s/check/%s/classes",
                            refasterBuildDir, underlyingJavaCompileTaskName)));

                // TODO: This is a hack to forcefully disable UP-TO-DATE checking whilst we're
                // waiting for "compileRefasterTemplate" to itself adopt UP-TO-DATE checking. Remove
                // it when "compileRefasterTemplate" is fixed.
                j.getOutputs().upToDateWhen(unused -> false);
              });
      refasterCheckTask.dependsOn(refasterCheckSubTask);

      JavaCompile refasterApplySubTask =
          project.getTasks().create(
              "refasterApplyFor" + capitalise(underlyingJavaCompileTaskName),
              JavaCompile.class,
              j -> {
                j.dependsOn(compileRefasterTemplateTask);

                j.setToolChain(ErrorProneToolChain.create(project));

                j.setClasspath(underlyingJavaCompileTask.getClasspath());
                j.setSource(underlyingJavaCompileTask.getSource());

                j.getOptions()
                    .setCompilerArgs(
                        Arrays.asList(
                            "-Xlint:none",
                            "-XepPatchChecks:refaster:" + compiledRefasterTemplatePath,
                            "-XepPatchLocation:IN_PLACE"));
                j.setDestinationDir(
                    project.file(
                        String.format(
                            "%s/apply/%s/classes",
                            refasterBuildDir, underlyingJavaCompileTaskName)));

                // TODO: This is a hack to forcefully disable UP-TO-DATE checking whilst we're
                // waiting for "compileRefasterTemplate" to itself adopt UP-TO-DATE checking. Remove
                // it when "compileRefasterTemplate" is fixed.
                j.getOutputs().upToDateWhen(unused -> false);
              });
      refasterApplyTask.dependsOn(refasterApplySubTask);
    }
  }

  private static <T> T single(Stream<T> stream) {
    return singleImpl(stream.iterator(), "stream");
  }

  private static <T> T single(Iterable<T> iterable) {
    return singleImpl(iterable.iterator(), "iterable");
  }

  private static <T> T singleImpl(Iterator<T> iterator, String name) {
    if (!iterator.hasNext()) {
      throw new NoSuchElementException(name + " is empty");
    }
    T result = iterator.next();
    if (iterator.hasNext()) {
      throw new IllegalArgumentException(name + " has more than one element");
    }
    return result;
  }

  private static String capitalise(String value) {
    StringBuilder builder = new StringBuilder();
    builder.appendCodePoint(Character.toUpperCase(value.codePointAt(0)));
    value.codePoints().skip(1).forEachOrdered(builder::appendCodePoint);
    return builder.toString();
  }
}
