plugins {
    `java-library`
    eclipse
    idea
    pmd

    id("com.diffplug.gradle.spotless") version("3.14.0")
    id("com.github.ben-manes.versions") version("0.20.0")
    // TODO: Consider swapping out for
    // https://github.com/tbroyer/gradle-errorprone-javacplugin-plugin
    id("net.ltgt.errorprone") // No version needed, as already imported in buildSrc/build.gradle
}

// Configuration for Java - START
group = "com.github.jbduncan"
// TODO: Start at version 0.0.1? Consider following semver.
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
}

tasks.withType<Test>().configureEach {
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
// Configuration for Java - END

// Configuration for PMD - START
val pmdVersion: String by project

pmd {
    toolVersion = pmdVersion
    ruleSets = emptyList()
    ruleSetConfig = resources.text.fromFile(file("$rootDir/config/pmd/ruleset.xml"), "UTF-8")
}

tasks.withType(Pmd::class.java)
        .configureEach {
            if (name.contains("refaster", ignoreCase = true)) {
                enabled = false
            }
        }
// Configuration for PMD - END

// Configuration for error-prone - START (https://github.com/tbroyer/gradle-errorprone-plugin)
val errorProneVersion: String by project

dependencies {
    errorprone("com.google.errorprone:error_prone_core:$errorProneVersion")
    compileOnly("com.google.errorprone:error_prone_annotations:$errorProneVersion")
}

val commonErrorProneOptions =
    listOf(
        // TODO: Update these on every new version of error-prone
        // Experimental ERROR checks
        "-Xep:ClassName",
        "-Xep:ComparisonContractViolated",
        "-Xep:DepAnn",
        "-Xep:DivZero",
        "-Xep:EmptyIf",
        "-Xep:FuzzyEqualsShouldNotBeUsedInEqualsMethod",
        "-Xep:InjectInvalidTargetingOnScopingAnnotation",
        "-Xep:InsecureCryptoUsage",
        "-Xep:IterablePathParameter",
        "-Xep:LongLiteralLowerCaseSuffix",
        "-Xep:NumericEquality",
        "-Xep:ParameterPackage",
        // Experimental WARNING checks
        "-Xep:AssertFalse",
        "-Xep:ConstructorInvokesOverridable",
        "-Xep:ConstructorLeaksThis",
        "-Xep:EmptyTopLevelDeclaration",
        "-Xep:FunctionalInterfaceClash",
        "-Xep:InconsistentOverloads",
        "-Xep:MissingDefault",
        "-Xep:NonCanonicalStaticMemberImport",
        "-Xep:PrimitiveArrayPassedToVarargsMethod",
        "-Xep:RedundantThrows",
        "-Xep:StaticQualifiedUsingExpression",
        "-Xep:StringEquality",
        "-Xep:TestExceptionChecker",
        "-Xep:UnnecessaryDefaultInEnumSwitch",
        // Experimental SUGGESTION checks
        "-Xep:ConstantField",
        "-Xep:FieldCanBeFinal",
        "-Xep:LambdaFunctionalInterface",
        "-Xep:MixedArrayDimensions",
        "-Xep:MultiVariableDeclaration",
        "-Xep:MultipleTopLevelClasses",
        "-Xep:MultipleUnaryOperatorsInMethodCall",
        "-Xep:PackageLocation",
        "-Xep:ParameterComment",
        "-Xep:PrivateConstructorForUtilityClass",
        "-Xep:SwitchDefault",
        "-Xep:ThrowsUncheckedException",
        "-Xep:TypeParameterNaming",
        "-Xep:UngroupedOverloads",
        "-Xep:UnnecessarySetDefault",
        "-Xep:UnnecessaryStaticImport",
        "-Xep:WildcardImport")

tasks.withType(JavaCompile::class.java).named("compileJava").configure {
    options.compilerArgs.addAll(commonErrorProneOptions)
}
tasks.withType(JavaCompile::class.java).named("compileTestJava").configure {
    options.compilerArgs.addAll(
            commonErrorProneOptions.plus(listOf(
                    // Produces false positives against JUnit Platform @Nested tests
                    "-Xep:ClassCanBeStatic:OFF")))
}

// Configuration for error-prone - END

// Configuration for Refaster - START (http://errorprone.info/docs/refaster)
apply {
    plugin("com.github.jbduncan.gradle.refaster")
}
// Configuration for Refaster - END

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
        trimTrailingWhitespace()
        endWithNewline()
    }
    format("misc") {
        target(fileTree(rootDir) {
            include("**/*.gradle",
                    "**/*.gitignore",
                    "README.md",
                    "CONTRIBUTING.md",
                    "config/**/*.xml",
                    "src/**/*.xml")
        })
        trimTrailingWhitespace()
        endWithNewline()
    }
    encoding("UTF-8")
}
// Configuration for Spotless - END

// If both Spotless and Refaster are requested, make sure Spotless runs after Refaster so that the
// code remains formatted as expected.
afterEvaluate {
    tasks.named("spotlessApply").configure { mustRunAfter("refasterApply") }
}
