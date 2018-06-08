package com.github.jbduncan.gradle.refaster;

import static com.github.jbduncan.gradle.refaster.Constants.DEFAULT_ERROR_PRONE_VERSION;
import static com.github.jbduncan.gradle.refaster.Constants.DEFAULT_REFASTER_SOURCE_SET_DIR;
import static com.github.jbduncan.gradle.refaster.Constants.ERROR_PRONE_CORE_MAVEN_COORDINATES_FORMAT;
import static com.github.jbduncan.gradle.refaster.Constants.REFASTER_MAVEN_COORDINATES_FORMAT;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import net.ltgt.gradle.errorprone.ErrorProneToolChain;
import org.apache.commons.io.FilenameUtils;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.compile.JavaCompile;

// TODO: Consider allowing the user to pass in a graph that describes a partial ordering in which
// the Refaster templates must be executed.
public class RefasterPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    // TODO: Remove this Java-8-only restriction
    int javaVersion = Integer.parseInt(JavaVersion.current().getMajorVersion());
    if (javaVersion != 8) {
      throw new GradleException(
          "Plugin of type " + getClass() + " requires Java 8. "
              + "Currently executing with Java "
              + javaVersion
              + ".");
    }

    // TODO: Consider wrapping this method call chain in project.afterEvaluate to wait until after
    // net.ltgt.errorprone has potentially been applied, so that we can ensure that the
    // refaster(Check|Apply) JavaCompile-based sub-tasks use custom error-prone toolchains (as they
    // currently do) rather than the default toolchain that net.ltgt.errorprone would automatically
    // apply.
    // Alternatively, migrate away from sub-tasks implementing JavaCompile and use project.javaexec
    // instead.
    project
        .getPluginManager()
        .withPlugin("net.ltgt.errorprone-base", appliedPlugin -> applyInternal(project));
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
                          files.srcDir(DEFAULT_REFASTER_SOURCE_SET_DIR);
                        }));

    // TODO: Allow any error-prone version 2.1.0+ to be used
    String errorProneVersion = DEFAULT_ERROR_PRONE_VERSION;

    // Set up the Refaster annotations dependency for the Refaster templates
    project
        .getDependencies()
        .add(
            refasterSourceSet.getImplementationConfigurationName(),
            String.format(REFASTER_MAVEN_COORDINATES_FORMAT, errorProneVersion));

    SourceDirectorySet refasterTemplates = refasterSourceSet.getAllJava();

    File refasterBuildDir = project.file(String.format("%s/refaster", project.getBuildDir()));

    RefasterTemplateCompile compileRefasterTemplatesTask =
        project
            .getTasks()
            .create(
                "compileRefasterTemplates",
                RefasterTemplateCompile.class,
                t -> {
                  t.setErrorProneVersion(errorProneVersion);
                  t.setRefasterTemplates(refasterTemplates);
                  t.setCompiledRefasterTemplatesDir(
                      project.file(String.format("%s/templates", refasterBuildDir)));
                  t.setCompiledRefasterClassesDir(
                      project.file(String.format("%s/classes", refasterBuildDir)));
                });

    // TODO: Consider redirecting all logging messages by the refaster(Check|Apply) sub-tasks to a
    // log file in the buildDir, as in https://stackoverflow.com/a/27679230/2252930

    Set<JavaCompile> allJavaCompileTasksExceptRefaster =
        new LinkedHashSet<>(
            project
                .getTasks()
                .withType(JavaCompile.class)
                .matching(t -> !t.getName().equals(refasterSourceSet.getCompileJavaTaskName())));

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

    // TODO: When the restriction on the error-prone version to use is lifted, consider using the
    // gradle-errorprone-plugin's errorprone configuration instead, e.g.
    // "project.getConfigurations().getByName(ErrorProneBasePlugin.CONFIGURATION_NAME)..."
    Configuration errorproneCoreConfiguration =
        project
            .getConfigurations()
            .detachedConfiguration(
                project
                    .getDependencies()
                    .create(
                        String.format(
                            ERROR_PRONE_CORE_MAVEN_COORDINATES_FORMAT,
                            compileRefasterTemplatesTask.getErrorProneVersion())));

    for (File refasterTemplate : refasterTemplates) {
      for (JavaCompile underlyingJavaCompileTask : allJavaCompileTasksExceptRefaster) {
        File compiledRefasterTemplateFile =
            Utils.toCompiledRefasterTemplateFile(
                refasterTemplate,
                project.getRootDir(),
                compileRefasterTemplatesTask.getCompiledRefasterTemplatesDir());
        String refasterTemplateName = FilenameUtils.removeExtension(refasterTemplate.getName());

        JavaCompile refasterCheckSubTask =
            project
                .getTasks()
                .create(
                    String.format(
                        "refaster%sCheckWith%s",
                        capitalise(underlyingJavaCompileTask.getName()),
                        refasterTemplateName),
                    JavaCompile.class,
                    j -> {
                      j.setClasspath(underlyingJavaCompileTask.getClasspath());
                      j.setSource(underlyingJavaCompileTask.getSource());
                      j.setDestinationDir(
                          project.file(
                              String.format(
                                  "%s/check/%s/%s/classes",
                                  refasterBuildDir,
                                  refasterTemplateName,
                                  underlyingJavaCompileTask.getName())));

                      j.setToolChain(new ErrorProneToolChain(errorproneCoreConfiguration));

                      String nullDir = Os.isFamily(Os.FAMILY_WINDOWS) ? "nul" : "/dev/null";
                      j.getOptions()
                          .setCompilerArgs(
                              Arrays.asList(
                                  "-Werror",
                                  "-XepPatchChecks:refaster:" + compiledRefasterTemplateFile,
                                  "-XepPatchLocation:" + nullDir));

                      // TODO: See if there is a way of declaring this input without using an
                      // internal API.
                      j.getInputs().file(compiledRefasterTemplateFile);
                    });
        refasterCheckSubTask.dependsOn(compileRefasterTemplatesTask);
        underlyingJavaCompileTask.mustRunAfter(refasterCheckSubTask);
        refasterCheckTask.dependsOn(refasterCheckSubTask);

        JavaCompile refasterApplySubTask =
            project
                .getTasks()
                .create(
                    String.format(
                        "refaster%sApplyWith%s",
                        capitalise(underlyingJavaCompileTask.getName()),
                        refasterTemplateName),
                    JavaCompile.class,
                    j -> {
                      j.setClasspath(underlyingJavaCompileTask.getClasspath());
                      j.setSource(underlyingJavaCompileTask.getSource());
                      j.setDestinationDir(
                          project.file(
                              String.format(
                                  "%s/apply/%s/%s/classes",
                                  refasterBuildDir,
                                  refasterTemplateName,
                                  underlyingJavaCompileTask.getName())));

                      j.setToolChain(new ErrorProneToolChain(errorproneCoreConfiguration));

                      j.getOptions()
                          .setCompilerArgs(
                              Arrays.asList(
                                  "-Xlint:none",
                                  "-XepPatchChecks:refaster:" + compiledRefasterTemplateFile,
                                  "-XepPatchLocation:IN_PLACE"));

                      // TODO: This is a hack to disable UP-TO-DATE checking, as for some reason
                      // it doesn't work properly for all refasterApply sub-tasks. Remove this hack
                      // if the problem is fixed.
                      j.getOutputs().upToDateWhen(x -> false);
                    });
        refasterApplySubTask.dependsOn(compileRefasterTemplatesTask);
        underlyingJavaCompileTask.mustRunAfter(refasterApplySubTask);
        refasterApplyTask.dependsOn(refasterApplySubTask);
      }
    }
  }

  private static String capitalise(String value) {
    if (value.isEmpty()) {
      return "";
    }
    StringBuilder builder = new StringBuilder();
    builder.appendCodePoint(Character.toUpperCase(value.codePointAt(0)));
    value.codePoints().skip(1).forEachOrdered(builder::appendCodePoint);
    return builder.toString();
  }
}
