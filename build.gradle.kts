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
    }
    kotlinGradle {
        ktlint(ktlintVersion)
        endWithNewline()
    }
    format("misc") {
        target("**/*.gradle", "**/*.gitignore", "**/*.properties")
        trimTrailingWhitespace()
        endWithNewline()
    }
    encoding("UTF-8")
}
