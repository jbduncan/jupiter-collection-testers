plugins {
    java
    eclipse
    idea

    id("org.gradle.kotlin.kotlin-dsl") version("0.16.0")
    id("com.github.ben-manes.versions") version("0.17.0")
    id("com.diffplug.gradle.spotless") version("3.10.0")
    id("net.ltgt.errorprone") version("0.0.13")
    // TODO: Add other static analysis and formal verification tools
}

group = "com.github.jbduncan"
version = "1.0-SNAPSHOT"

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

repositories {
    jcenter()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter-api:${ext["junit5Version"]}")

    testImplementation("com.google.guava:guava:${ext["guavaVersion"]}")
    testImplementation("com.google.truth:truth:${ext["truthVersion"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${ext["junit5Version"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${ext["junit5Version"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${ext["junit5Version"]}")

    errorprone("com.google.errorprone:error_prone_core:${ext["errorProneVersion"]}")
    compileOnly("com.google.errorprone:error_prone_annotations:${ext["errorProneVersion"]}")
}

spotless {
    java {
        googleJavaFormat("${ext["googleJavaFormatVersion"]}")
    }
    kotlinGradle {
        ktlint("${ext["ktlintVersion"]}")
    }
    format("misc") {
        target("**/*.gradle", "**/*.gitignore", "**/*.properties")
        trimTrailingWhitespace()
        endWithNewline()
    }
    encoding("UTF-8")
}
