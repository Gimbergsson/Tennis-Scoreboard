import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

val apikeyPropertiesFile = rootProject.file("apikey.properties")
val apikeyProperties = Properties()
apikeyProperties.load(FileInputStream(apikeyPropertiesFile))

val gitBranchName = providers.exec {
    commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
}.standardOutput.asText.get()

android {
    namespace = "se.dennisgimbergsson.tennisscoreboard"
    compileSdk = 35

    defaultConfig {
        applicationId = "se.dennisgimbergsson.tennisscoreboard"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "${buildVersionName()} ($versionCode)"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("releaseConfig") {
            keyAlias = keystoreProperties["KEYSTORE_KEY_ALIAS"] as String?
            keyPassword = keystoreProperties["KEYSTORE_KEY_PASSWORD"] as String?
            storeFile = file(keystoreProperties["KEYSTORE_FILE_PATH"] as String)
            storePassword = keystoreProperties["KEYSTORE_PASSWORD"] as String
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("releaseConfig")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    packaging {
        resources {
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE-notice.md")
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        languageVersion = "2.0"
        jvmTarget = "17"
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(project(":shared"))

    // Dependency injection with Hilt.
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.preference)

    implementation(libs.androidx.wear)
    implementation(libs.androidx.wear.compose.material)
    implementation(libs.androidx.wear.compose.material.core)
    implementation(libs.androidx.wear.compose.foundation)
    implementation(libs.androidx.wear.input)
    implementation(libs.androidx.wear.tooling.preview)

    implementation(libs.play.services.wearable)
    implementation(libs.play.services.tasks)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.guava)

    implementation(libs.gson)

    /**
     * Unit test dependencies
     */
    testImplementation(libs.androidx.arch.core)
    testImplementation(libs.kotlinx.coroutines.test)

    // Junit4
    testImplementation(libs.junit)

    // JUnit5
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.vintage.engine)

    // Mockito
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)

    // Mockk
    testImplementation(libs.mockk)

    /**
     * UI test dependencies
     */
    androidTestImplementation(libs.androidx.core.ktx)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.rules)

    // Mockk
    androidTestImplementation(libs.mockk)

    // Espresso
    androidTestImplementation(libs.androidx.espresso.core)

    // Test rules and transitive dependencies:
    androidTestImplementation(libs.androidx.compose.ui.unit)
}

tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        showExceptions = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showCauses = true
        showStackTraces = true
        showStandardStreams = true
    }
}

tasks.register("printBuildName") {
    val versionName = android.defaultConfig.versionName?.replace(".", "_")
        ?.replace(" ", "-#")
        ?.replace("(", "")
        ?.replace(")", "")
    doLast {
        println("$versionName")
    }
}

private fun buildVersionName(): String {
    var versionName = "undefined-version-name"
    try {
        // Gets the full branch name from Git.
        val branchName = gitBranchName.trim()

        // Only take the identifier part from the full branch name
        // example from "release/1-0-0" to "1-0-0".
        val branchIdentifierName = gitBranchName.trim()
            .split("/")
            .last()

        // If it's a release build then replace the strokes with dots.
        versionName = if (branchName.startsWith("release")) {
            branchIdentifierName.replace("-", ".")
        } else {
            branchIdentifierName
        }
    } catch (ignored: Exception) {
        println("Failed to get git branch")
    }
    return versionName
}