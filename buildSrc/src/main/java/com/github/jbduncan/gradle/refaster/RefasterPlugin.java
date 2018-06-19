package com.github.jbduncan.gradle.refaster;

import static com.github.jbduncan.gradle.refaster.Constants.REFASTER_MAVEN_COORDINATES_FORMAT;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.ltgt.gradle.errorprone.ErrorProneBasePlugin;
import net.ltgt.gradle.errorprone.ErrorProneToolChain;
import org.apache.commons.io.FilenameUtils;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.compile.JavaCompile;

// TODO: Consider allowing the user to pass in a graph describing a partial ordering in which the
// Refaster templates should be executed.
// TODO: Consider migrating to Kotlin.
// TODO: Consider using Task Configuration Avoidance
// (https://docs.gradle.org/4.9-rc-1/userguide/task_configuration_avoidance.html)
public class RefasterPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    // TODO: Remove this Java-8-only restriction
    int javaVersion = Integer.parseInt(JavaVersion.current().getMajorVersion());
    if (javaVersion != 8) {
      throw new GradleException(
          String.format(
              "Plugin of type %s requires Java 8. Currently executing with Java %s.",
              getClass(), javaVersion));
    }

    project
        .getPluginManager()
        .withPlugin("net.ltgt.errorprone", appliedPlugin -> applyInternal(project));
  }

  private void applyInternal(Project project) {
    // Set up the source set for the Refaster templates
    SourceSet refasterSourceSet =
        project
            .getConvention()
            .getPlugin(JavaPluginConvention.class)
            .getSourceSets()
            .create(
                "refaster",
                sourceSet ->
                    sourceSet.java(
                        files -> {
                          files.srcDir("src/refaster/java");
                        }));

    // Retrieve the on-filesytem location of the user-defined Refaster templates
    SourceDirectorySet refasterTemplates = refasterSourceSet.getAllJava();

    for (File refasterTemplateFile : refasterTemplates) {
      if (refasterTemplateFile.toString().contains(" ")) {
        throw new GradleException(
            "Refaster cannot compile the given template because it has space(s) in its path: "
                + refasterTemplateFile);
      }
    }

    // Discover the error-prone version used by gradle-errorprone-plugin
    // TODO: Document that this plugin only supports error-prone and Refaster versions 2.1.2+
    String errorProneVersion =
        project
            .getConfigurations()
            .getByName(ErrorProneBasePlugin.CONFIGURATION_NAME)
            .getResolvedConfiguration()
            .getFirstLevelModuleDependencies(
                element -> element.getName().equals("error_prone_core"))
            .iterator()
            .next()
            .getModuleVersion();
    String refasterMavenCoords =
        String.format(REFASTER_MAVEN_COORDINATES_FORMAT, errorProneVersion);

    // Set up the Refaster annotations dependency for the Refaster templates
    project
        .getDependencies()
        .add(refasterSourceSet.getImplementationConfigurationName(), refasterMavenCoords);

    File refasterBuildDir = project.file(String.format("%s/refaster", project.getBuildDir()));

    String refasterConfigurationName = "refaster";
    Configuration refasterConfiguration =
        project.getConfigurations().maybeCreate(refasterConfigurationName);
    project.getDependencies().add(refasterConfigurationName, refasterMavenCoords);

    File compiledRefasterTemplatesDir =
        project.file(String.format("%s/templates", refasterBuildDir));

    // Compile all Refaster templates
    List<JavaCompile> allJavaCompileTasksExceptRefaster = new ArrayList<>();
    for (JavaCompile javaCompile : project.getTasks().withType(JavaCompile.class)) {
      if (!javaCompile.getName().contains("Refaster")) {
        allJavaCompileTasksExceptRefaster.add(javaCompile);
      }
    }

    Task refasterCheckTask =
        project
            .getTasks()
            .create(
                "refasterCheck",
                j -> {
                  j.setGroup(JavaBasePlugin.VERIFICATION_GROUP);
                  j.setDescription("Checks that Java source code satisfies Refaster refactorings.");
                });
    project.getTasks().getByPath("check").dependsOn(refasterCheckTask);
    Task refasterApplyTask =
        project
            .getTasks()
            .create(
                "refasterApply",
                j -> {
                  j.setGroup(JavaBasePlugin.VERIFICATION_GROUP);
                  j.setDescription("Applies Refaster refactorings to source code in-place.");
                });

    for (File refasterTemplateFile : refasterTemplates) {
      String refasterTemplateName = FilenameUtils.removeExtension(refasterTemplateFile.getName());

      File compiledRefasterTemplateFile =
          Utils.toCompiledRefasterTemplateFile(
              refasterTemplateFile, project.getRootDir(), compiledRefasterTemplatesDir);

      Task compileRefasterTemplateSubTask =
          createCompileRefasterTemplateTask(
              project,
              refasterConfiguration,
              refasterTemplateFile,
              refasterTemplateName,
              compiledRefasterTemplateFile);

      // Use compiled Refaster templates to perform refactorings

      // TODO: Consider redirecting all logging messages by the refaster(Check|Apply) sub-tasks to a
      // log file in the buildDir, as in https://stackoverflow.com/a/27679230/2252930

      for (JavaCompile underlyingJavaCompileTask : allJavaCompileTasksExceptRefaster) {
        String javaCompileTaskNameCapitalised =
            Utils.capitalise(underlyingJavaCompileTask.getName());
        FileCollection classpath = underlyingJavaCompileTask.getClasspath();
        FileTree source = underlyingJavaCompileTask.getSource();

        JavaCompile refasterCheckSubTask =
            project
                .getTasks()
                .create(
                    String.format(
                        "refasterCheckFor%sWith%s",
                        javaCompileTaskNameCapitalised, refasterTemplateName),
                    JavaCompile.class,
                    j -> {
                      // TODO: See if it's possible to forgo including the classpath and in turn
                      // see if it's possible to combine compileJava's and compileTestJava's sources
                      // together in one refaster check sub-task (or to combine all Java files a-la
                      // Spotless).
                      // (https://github.com/google/error-prone/blob/master/examples/maven/refaster-based-cleanup/pom.xml
                      // might be able to help here.)
                      j.setClasspath(classpath);
                      j.setSource(source);
                      Utils.setDestinationDirToTaskTemporaryDir(j);

                      j.setToolChain(new ErrorProneToolChain(refasterConfiguration));

                      String nullDir = Os.isFamily(Os.FAMILY_WINDOWS) ? "nul" : "/dev/null";
                      j.getOptions()
                          .setCompilerArgs(
                              Arrays.asList(
                                  "-Werror",
                                  "-XepPatchChecks:refaster:" + compiledRefasterTemplateFile,
                                  "-XepPatchLocation:" + nullDir));

                      j.getInputs().file(compiledRefasterTemplateFile);
                    });
        refasterCheckSubTask.dependsOn(compileRefasterTemplateSubTask);
        underlyingJavaCompileTask.mustRunAfter(refasterCheckSubTask);
        refasterCheckTask.dependsOn(refasterCheckSubTask);

        JavaCompile refasterApplySubTask =
            project
                .getTasks()
                .create(
                    String.format(
                        "refaster%sApplyWith%s",
                        javaCompileTaskNameCapitalised, refasterTemplateName),
                    JavaCompile.class,
                    j -> {
                      // TODO: See if it's possible to forgo including the classpath and in turn
                      // see if it's possible to combine compileJava's and compileTestJava's sources
                      // together in one refaster check sub-task (or to combine all Java files a-la
                      // Spotless).
                      // (https://github.com/google/error-prone/blob/master/examples/maven/refaster-based-cleanup/pom.xml
                      // might be able to help here.)
                      j.setClasspath(classpath);
                      j.setSource(source);
                      Utils.setDestinationDirToTaskTemporaryDir(j);

                      j.setToolChain(new ErrorProneToolChain(refasterConfiguration));

                      j.getOptions()
                          .setCompilerArgs(
                              Arrays.asList(
                                  "-Xlint:none",
                                  "-XepPatchChecks:refaster:" + compiledRefasterTemplateFile,
                                  "-XepPatchLocation:IN_PLACE"));

                      // TODO: This is a hack to disable UP-TO-DATE checking, as for some reason
                      // it doesn't seem to work properly for refasterApply. Remove this hack if the
                      // problem is fixed.
                      j.getOutputs().upToDateWhen(x -> false);
                    });
        refasterApplySubTask.dependsOn(compileRefasterTemplateSubTask);
        underlyingJavaCompileTask.mustRunAfter(refasterApplySubTask);
        refasterApplyTask.dependsOn(refasterApplySubTask);
      }
    }
  }

  private Task createCompileRefasterTemplateTask(
      Project project,
      Configuration refasterConfiguration,
      File refasterTemplateFile,
      String refasterTemplateName,
      File compiledRefasterTemplateFile) {

    JavaCompile compileRefasterTemplateSubTask =
        project
            .getTasks()
            .maybeCreate(
                String.format("compileRefasterTemplateNamed%s", refasterTemplateName),
                JavaCompile.class);
    compileRefasterTemplateSubTask.setSource(refasterTemplateFile);
    compileRefasterTemplateSubTask.setToolChain(new ErrorProneToolChain(refasterConfiguration));
    compileRefasterTemplateSubTask.setClasspath(
        compileRefasterTemplateSubTask.getClasspath() == null
            ? refasterConfiguration
            : compileRefasterTemplateSubTask.getClasspath().plus(refasterConfiguration));
    compileRefasterTemplateSubTask
        .getOptions()
        .setAnnotationProcessorPath(
            compileRefasterTemplateSubTask.getOptions().getAnnotationProcessorPath() == null
                ? refasterConfiguration
                : compileRefasterTemplateSubTask
                    .getOptions()
                    .getAnnotationProcessorPath()
                    .plus(refasterConfiguration));
    Utils.setDestinationDirToTaskTemporaryDir(compileRefasterTemplateSubTask);

    compileRefasterTemplateSubTask
        .getOptions()
        .getCompilerArgs()
        .add("-Xplugin:RefasterRuleCompiler --out " + compiledRefasterTemplateFile);

    compileRefasterTemplateSubTask.getInputs().file(refasterTemplateFile);
    compileRefasterTemplateSubTask.getOutputs().file(compiledRefasterTemplateFile);

    return compileRefasterTemplateSubTask;
  }
}
