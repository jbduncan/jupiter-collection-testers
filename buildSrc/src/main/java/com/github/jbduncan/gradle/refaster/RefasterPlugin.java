package com.github.jbduncan.gradle.refaster;

import static com.github.jbduncan.gradle.refaster.Constants.REFASTER_MAVEN_COORDINATES_FORMAT;

import java.io.File;
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
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.compile.JavaCompile;

// TODO: Consider allowing the user to pass in a graph describing a partial ordering in which the
// Refaster templates should be executed.
// TODO: Consider migrating to Kotlin.
// TODO: Consider using Task Configuration Avoidance:
// (https://docs.gradle.org/4.9/userguide/task_configuration_avoidance.html)
public class RefasterPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    // TODO: Remove this Java-8-only restriction (see
    // https://github.com/google/error-prone/commit/0ede1de0a59a6c782a242ca85981b8b71c92f944 and
    // https://github.com/google/error-prone/issues/535#issuecomment-414165838 for guidance).
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
        project.getConfigurations().create(refasterConfigurationName);
    project.getDependencies().add(refasterConfigurationName, refasterMavenCoords);

    File compiledRefasterTemplatesDir =
        project.file(String.format("%s/templates", refasterBuildDir));

    // Compile all Refaster templates
    List<JavaCompile> baseJavaCompileTasks =
        Arrays.asList(
            project.getTasks().withType(JavaCompile.class).getByName("compileJava"),
            project.getTasks().withType(JavaCompile.class).getByName("compileTestJava"));

    Task refasterCheckTask =
        project
            .getTasks()
            .create(
                "refasterCheck",
                task -> {
                  task.setGroup(JavaBasePlugin.VERIFICATION_GROUP);
                  task.setDescription("Checks that Java source code satisfies Refaster refactorings.");
                });
    project.getTasks().getByPath("check").dependsOn(refasterCheckTask);
    Task refasterApplyTask =
        project
            .getTasks()
            .create(
                "refasterApply",
                task -> {
                  task.setGroup(JavaBasePlugin.VERIFICATION_GROUP);
                  task.setDescription("Applies Refaster refactorings to source code in-place.");
                });

    for (File refasterTemplateFile : refasterTemplates) {
      String refasterTemplateName = FilenameUtils.removeExtension(refasterTemplateFile.getName());

      File compiledRefasterTemplateFile =
          Utils.toCompiledRefasterTemplateFile(
              refasterTemplateFile, project.getRootDir(), compiledRefasterTemplatesDir);

      Task compileRefasterTemplateSubTask =
          createCompileRefasterTemplateTask(
              project.getTasks(),
              refasterConfiguration,
              refasterTemplateFile,
              refasterTemplateName,
              compiledRefasterTemplateFile);

      // Use compiled Refaster templates to perform refactorings
      for (JavaCompile baseJavaCompileTask : baseJavaCompileTasks) {
        String javaCompileTaskNameCapitalised = Utils.capitalise(baseJavaCompileTask.getName());
        FileCollection classpath = baseJavaCompileTask.getClasspath();
        FileTree source = baseJavaCompileTask.getSource();

        // TODO: Find a way of suppressing the noisy standard-out logging produced by Refaster.
        // TODO: Refactor common parts of `refasterCheckSubTask` and
        // `refasterApplySubTask` into its own method.
        JavaCompile refasterCheckSubTask =
            project
                .getTasks()
                .create(
                    String.format(
                        "refasterCheckFor%sWith%s",
                        javaCompileTaskNameCapitalised, refasterTemplateName),
                    JavaCompile.class,
                    task -> {
                      task.setClasspath(classpath);
                      task.setSource(source);
                      setDestinationDirToTaskTemporaryDir(task);

                      task.setToolChain(new ErrorProneToolChain(refasterConfiguration));

                      String nullDir = Os.isFamily(Os.FAMILY_WINDOWS) ? "nul" : "/dev/null";
                      task.getOptions()
                          .setCompilerArgs(
                              Arrays.asList(
                                  "-Werror",
                                  "-XDcompilePolicy=byfile",
                                  "-XepPatchChecks:refaster:" + compiledRefasterTemplateFile,
                                  "-XepPatchLocation:" + nullDir));

                      task.getInputs().file(compiledRefasterTemplateFile);
                    });
        refasterCheckSubTask.dependsOn(compileRefasterTemplateSubTask);
        baseJavaCompileTask.mustRunAfter(refasterCheckSubTask);
        refasterCheckTask.dependsOn(refasterCheckSubTask);

        JavaCompile refasterApplySubTask =
            project
                .getTasks()
                .create(
                    String.format(
                        "refasterApplyFor%sWith%s",
                        javaCompileTaskNameCapitalised, refasterTemplateName),
                    JavaCompile.class,
                    j -> {
                      j.setClasspath(classpath);
                      j.setSource(source);
                      setDestinationDirToTaskTemporaryDir(j);

                      j.setToolChain(new ErrorProneToolChain(refasterConfiguration));

                      j.getOptions()
                          .setCompilerArgs(
                              Arrays.asList(
                                  "-Xlint:none",
                                  "-XDcompilePolicy=byfile",
                                  "-XepPatchChecks:refaster:" + compiledRefasterTemplateFile,
                                  "-XepPatchLocation:IN_PLACE"));

                      // This is a hack to disable UP-TO-DATE checking, as for some reason
                      // it doesn't seem to work properly for refasterApply.
                      j.getOutputs().upToDateWhen(x -> false);
                    });
        refasterApplySubTask.dependsOn(compileRefasterTemplateSubTask);
        baseJavaCompileTask.mustRunAfter(refasterApplySubTask);
        refasterApplyTask.dependsOn(refasterApplySubTask);
      }
    }
  }

  private Task createCompileRefasterTemplateTask(
      TaskContainer projectTasks,
      Configuration refasterConfiguration,
      File refasterTemplateFile,
      String refasterTemplateName,
      File compiledRefasterTemplateFile) {

    return projectTasks
        .create(
            String.format("compileRefasterTemplateNamed%s", refasterTemplateName),
            JavaCompile.class,
            task -> {
              task.setSource(refasterTemplateFile);
              task.setToolChain(new ErrorProneToolChain(refasterConfiguration));
              task.setClasspath(
                  task.getClasspath() == null
                      ? refasterConfiguration
                      : task.getClasspath().plus(refasterConfiguration));
              task.getOptions()
                  .setAnnotationProcessorPath(
                      task.getOptions().getAnnotationProcessorPath() == null
                          ? refasterConfiguration
                          : task.getOptions()
                              .getAnnotationProcessorPath()
                              .plus(refasterConfiguration));
              setDestinationDirToTaskTemporaryDir(task);

              task.getOptions()
                  .getCompilerArgs()
                  .add("-Xplugin:RefasterRuleCompiler --out " + compiledRefasterTemplateFile);

              task.getInputs().file(refasterTemplateFile);
              task.getOutputs().file(compiledRefasterTemplateFile);
            });
  }

  private static void setDestinationDirToTaskTemporaryDir(JavaCompile task) {
    // It seems that javac has trouble writing files to the operating system's null directory (at
    // least on Windows 7), so write to the next best place.
    task.setDestinationDir(task.getTemporaryDir());
  }
}
