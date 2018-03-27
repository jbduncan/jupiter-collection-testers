plugins {
    java
    eclipse
    idea

    id("com.github.ben-manes.versions") version("0.17.0")
    id("com.diffplug.gradle.spotless") version("3.10.0")
    // TODO: Consider swapping out for
    // https://github.com/tbroyer/gradle-errorprone-javacplugin-plugin
    id("net.ltgt.errorprone") version ("0.0.13")
    // TODO: Add other static analysis and formal verification tools
}

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

val compileTestJava by tasks.getting(JavaCompile::class) {
    // error-prone options
    options.compilerArgs.addAll(
            // Produces false positives against JUnit Platform @Nested tests
            listOf("-Xep:ClassCanBeStatic:OFF"))
}

repositories {
    jcenter()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val junit5Version by project.properties
val guavaVersion by project.properties
val truthVersion by project.properties
val errorProneVersion by project.properties

dependencies {
    implementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")

    testImplementation("com.google.guava:guava:$guavaVersion")
    testImplementation("com.google.truth:truth:$truthVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit5Version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit5Version")

    errorprone("com.google.errorprone:error_prone_core:$errorProneVersion")
    compileOnly("com.google.errorprone:error_prone_annotations:$errorProneVersion")
}

val googleJavaFormatVersion by project.properties
val ktlintVersion by project.properties

spotless {
    java {
        googleJavaFormat("$googleJavaFormatVersion")
    }
    kotlinGradle {
        ktlint("$ktlintVersion")
        endWithNewline()
    }
    format("misc") {
        target("**/*.gradle", "**/*.gitignore", "**/*.properties")
        trimTrailingWhitespace()
        endWithNewline()
    }
    encoding("UTF-8")
}
