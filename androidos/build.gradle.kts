import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)

    kotlin("plugin.serialization") version "2.0.21"
    id("androidx.navigation.safeargs.kotlin")
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

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

android {
    namespace = "se.dennisgimbergsson.tennisscoreboard"
    compileSdk = 36

    defaultConfig {
        applicationId = "se.dennisgimbergsson.tennisscoreboard"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "${buildVersionName()} ($versionCode)"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "APPWRITE_PROJECT_ID", apikeyProperties.getProperty("APPWRITE_PROJECT_ID"))
        buildConfigField("String", "APPWRITE_API_KEY", apikeyProperties.getProperty("APPWRITE_API_KEY"))
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
        buildConfig = true
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.android.desugar)

    implementation(project(":shared"))
    implementation(project(":tennis-score-manager"))

    // Dependency injection with Hilt.
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)

    // Local SQLite database with Room.
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.animation)

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.work)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.preference)

    implementation(libs.play.services.wearable)
    implementation(libs.gson)
    implementation(libs.appwrite.kotlin)

    androidTestImplementation(libs.androidx.core.ktx)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.espresso.core)
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
    } catch (exception: Exception) {
        println("Failed to get git branch: $exception")
    }
    return versionName
}