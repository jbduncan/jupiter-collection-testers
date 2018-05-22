plugins {
    `java-library`
    eclipse
    idea
    pmd

    id("com.diffplug.gradle.spotless") version("3.10.0")
    id("com.github.ben-manes.versions") version("0.17.0")
    // TODO: Consider swapping out for
    // https://github.com/tbroyer/gradle-errorprone-javacplugin-plugin
    id("net.ltgt.errorprone") // No version needed, as already imported in buildSrc/build.gradle

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

tasks.withType<Pmd> {
    if (name.contains("refaster", ignoreCase = true)) {
        enabled = false
    }
}

// Configuration for error-prone: https://github.com/tbroyer/gradle-errorprone-plugin
val errorProneVersion: String by project

dependencies {
    errorprone("com.google.errorprone:error_prone_core:$errorProneVersion")
    compileOnly("com.google.errorprone:error_prone_annotations:$errorProneVersion")
}

val compileJava by tasks.getting(JavaCompile::class)

val compileTestJava by tasks.getting(JavaCompile::class) {
    // error-prone options
    options.compilerArgs.addAll(
            // Produces false positives against JUnit Platform @Nested tests
            listOf("-Xep:ClassCanBeStatic:OFF"))
}

// Configuration for Refaster: http://errorprone.info/docs/refaster
apply {
    plugin("com.github.jbduncan.gradle.refaster")
}

// Configuration for Spotless: https://github.com/diffplug/spotless
val googleJavaFormatVersion: String by project
val ktlintVersion: String by project

spotless {
    java {
        googleJavaFormat(googleJavaFormatVersion)
        licenseHeaderFile(file("$rootDir/src/spotless/apache-license-2.0.java"))
        // TODO: Consider adding an "authorship" custom step that checks for @author tags on Java
        // source files and fails if any are present. Alternatively, do it as a custom Checkstyle
        // check as in
        // https://github.com/danielb987/EmojicodeEditor/blob/master/checkstyle/emojicode_checks.xml
    }
    kotlinGradle {
        ktlint(ktlintVersion)
        endWithNewline()
    }
    format("misc") {
        target(fileTree(rootDir, {
            include("**/*.gradle",
                    "**/*.gitignore",
                    "README.md",
                    "CONTRIBUTING.md",
                    "config/**/*.xml",
                    "src/**/*.xml")
        }))
        trimTrailingWhitespace()
        endWithNewline()
    }
    encoding("UTF-8")
}

afterEvaluate {
    tasks["spotlessApply"].mustRunAfter("refasterApply")
}
