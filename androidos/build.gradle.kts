import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

val apikeyPropertiesFile = rootProject.file("apikey.properties")
val apikeyProperties = Properties()
apikeyProperties.load(FileInputStream(apikeyPropertiesFile))

android {
    namespace = "se.dennisgimbergsson.tennisscoreboard"
    compileSdk = 35

    defaultConfig {
        applicationId = "se.dennisgimbergsson.tennisscoreboard"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "APPWRITE_PROJECT_ID", "\"${apikeyProperties["APPWRITE_PROJECT_ID"]}\"")
        buildConfigField("String", "APPWRITE_API_KEY", "\"${apikeyProperties["APPWRITE_API_KEY"]}\"")
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
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("releaseConfig")
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    packaging {
        resources {
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
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.compiler)

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.work)
    implementation(libs.androidx.preference)

    implementation(libs.play.services.wearable)
    implementation(libs.gson)
    implementation(libs.appwrite.kotlin)

    androidTestImplementation(libs.androidx.core.ktx)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.espresso.core)
}

tasks.register("printVersionName") {
    val versionName = android.defaultConfig.versionName?.replace(".", "_")
    val versionCode = android.defaultConfig.versionCode
    doLast {
        println("$versionName#$versionCode")
    }
}