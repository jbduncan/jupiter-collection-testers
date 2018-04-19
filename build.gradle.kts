import net.ltgt.gradle.errorprone.ErrorProneToolChain
import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    `java-library`
    eclipse
    idea
    pmd

    id("com.diffplug.gradle.spotless") version("3.10.0")
    id("com.github.ben-manes.versions") version("0.17.0")
    // TODO: Consider swapping out for
    // https://github.com/tbroyer/gradle-errorprone-javacplugin-plugin
    id("net.ltgt.errorprone") version("0.0.13")

    // TODO: Add other static analysis and formal verification tools
}

// Configuration for Java
group = "com.github.jbduncan"
// TODO: Start at version 0.0.1? Consider following semver.
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    jcenter()
}

val junit5Version: String by project
val guavaVersion: String by project
val truthVersion: String by project

dependencies {
    implementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")

    testImplementation("com.google.guava:guava:$guavaVersion")
    testImplementation("com.google.truth:truth:$truthVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit5Version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
}

// Configuration for PMD
val pmdVersion: String by project

pmd {
    toolVersion = pmdVersion
    ruleSets = emptyList()
    ruleSetConfig = resources.text.fromFile(file("$rootDir/config/pmd/ruleset.xml"), "UTF-8")
}

// Configuration for error-prone: https://github.com/tbroyer/gradle-errorprone-plugin
val errorProneVersion: String by project

dependencies {
    errorprone("com.google.errorprone:error_prone_core:$errorProneVersion")
    compileOnly("com.google.errorprone:error_prone_annotations:$errorProneVersion")
}

val compileTestJava by tasks.getting(JavaCompile::class) {
    // error-prone options
    options.compilerArgs.addAll(
            // Produces false positives against JUnit Platform @Nested tests
            listOf("-Xep:ClassCanBeStatic:OFF"))
}

// Configuration for Refaster: http://errorprone.info/docs/refaster

// TODO: Consider extracting this all into a plugin, and in doing so make
// "compileRefasterTemplate" have UP-TO-DATE checking and possibly even be incremental.

java {
    sourceSets {
        "refaster" {
            java {
                srcDir("src/refaster/java")
            }
        }
    }
}

val refasterCompile by configurations.getting

val refasterErrorProneVersion: String by project

dependencies {
    refasterCompile("com.google.guava:guava:$guavaVersion")
    refasterCompile("com.google.errorprone:error_prone_core:$refasterErrorProneVersion")
    refasterCompile("com.google.errorprone:error_prone_refaster:$refasterErrorProneVersion")
}

tasks {
    val refasterBuildDir = "$buildDir/refaster"
    val compiledRefasterTemplatePath = file("$refasterBuildDir/template/Refaster.refaster")

    val refasterJarPath =
            refasterCompile.files.single { it.getName().startsWith("error_prone_refaster") }
    val errorproneJavacJarPath =
            refasterCompile.files.single { it.name.matches("javac-.+.jar".toRegex()) }

    val refasterTemplatePath =
            java.sourceSets["refaster"].java.srcDirs.single()
                    .walkTopDown().single { it.isFile && it.toString().endsWith(".java") }
    val compiledRefasterClassesDir = "$refasterBuildDir/refaster/classes"

    "compileRefasterTemplate"(JavaExec::class) {
        doFirst {
            // Require Java 8
            val javaVersion = Integer.parseInt(JavaVersion.current().majorVersion)
            if (javaVersion != 8) {
                throw GradleException(
                        "The task 'compileRefasterTemplate' requires Java 8. " +
                                "Currently executing with Java " + javaVersion + ".")
            }

            mkdir(compiledRefasterTemplatePath.parent)
        }
        main = ""
        args(
                // TODO: Adjust these arguments so that they work for Java 9+ as well. See:
                // http://errorprone.info/docs/installation -> "Java 9" for more information.
                listOf(
                        "-Xbootclasspath/p:" +
                                "$refasterJarPath${File.pathSeparator}$errorproneJavacJarPath",
                        "com.google.errorprone.refaster.RefasterRuleCompiler",
                        refasterTemplatePath,
                        "--out",
                        compiledRefasterTemplatePath,
                        "-d",
                        compiledRefasterClassesDir))
    }

    val javaCompileTasks: List<JavaCompile> =
            tasks.withType<JavaCompile>()
                    .filter { it.name in listOf("compileJava", "compileTestJava") }
                    .toList()
    val refasterClasspath: FileCollection =
            javaCompileTasks
                    .asSequence()
                    .map { it.classpath }
                    .reduce { acc, classpath -> acc + classpath }
    val refasterSource: FileTree =
            javaCompileTasks
                    .asSequence()
                    .map { it.source }
                    .reduce { acc, source -> acc + source }
    val nullDir = if (Os.isFamily(Os.FAMILY_WINDOWS)) "nul" else "/dev/null"

    // FIXME: "refasterApply" and "refasterCheck" do not re-run if the contents of
    // `refasterTemplatePath` changes. Fix that.

    // TODO: Consider redirecting all logging messages to a log file in the buildDir, as in
    // https://stackoverflow.com/a/27679230/2252930

    "refasterCheck"(JavaCompile::class) {
        dependsOn("compileRefasterTemplate")

        group = "verification"
        toolChain = ErrorProneToolChain.create(project)

        classpath = refasterClasspath
        source = refasterSource

        options.compilerArgs =
                listOf(
                        "-Werror",
                        "-XepPatchChecks:refaster:$compiledRefasterTemplatePath",
                        "-XepPatchLocation:$nullDir")
        destinationDir = file("$refasterBuildDir/check/classes")
    }
    tasks["check"].dependsOn("refasterCheck")

    "refasterApply"(JavaCompile::class) {
        dependsOn("compileRefasterTemplate")
        group = "verification"
        toolChain = ErrorProneToolChain.create(project)

        classpath = refasterClasspath
        source = refasterSource

        options.compilerArgs =
                listOf(
                        "-Xlint:none",
                        "-XepPatchChecks:refaster:$compiledRefasterTemplatePath",
                        "-XepPatchLocation:IN_PLACE")
        destinationDir = file("$refasterBuildDir/apply/classes")
    }
}

// Configuration for Spotless: https://github.com/diffplug/spotless

// This explicitly-named "dependency configuration" is declared and populated with the Maven
// co-ordinates of various code formatters in the "dependencies" block below.
// This is done so that when `./gradlew dependencyUpdates` is executed, gradle-versions-plugin can
// then find the latest versions of all the formatters used by Spotless.
val spotless by configurations.creating

val googleJavaFormatVersion: String by project
val ktlintVersion: String by project

dependencies {
    spotless("com.google.googlejavaformat:google-java-format:$googleJavaFormatVersion")
    spotless("com.github.shyiko:ktlint:$ktlintVersion")
}

spotless {
    java {
        googleJavaFormat(googleJavaFormatVersion)
        // TODO: Consider adding an "authorship" custom step that checks for @author tags on Java
        // source files and fails if any are present.
    }
    kotlinGradle {
        ktlint(ktlintVersion)
        endWithNewline()
    }
    format("misc") {
        target(
                "**/*.gradle",
                "**/*.gitignore",
                "**/*.properties",
                "config/**/*.xml",
                "src/**/*.xml")
        trimTrailingWhitespace()
        endWithNewline()
    }
    encoding("UTF-8")
}

afterEvaluate {
    tasks["spotlessApply"].mustRunAfter("refasterApply")
}
