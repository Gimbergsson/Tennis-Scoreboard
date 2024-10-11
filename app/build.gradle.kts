plugins {
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    id("kotlin-android")
    id("kotlin-kapt")

    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "se.dennisgimbergsson.tennisscoreboard"
    compileSdk = 34

    defaultConfig {
        applicationId = "se.dennisgimbergsson.tennisscoreboard"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.preference)

    // Enable for navigation between compose views/screens.
    //implementation "androidx.wear.compose:compose-navigation:1.1.2"
    implementation(libs.androidx.wear)
    implementation(libs.androidx.wear.input)
    implementation(libs.androidx.wear.tooling.preview)

    implementation(libs.jetbrain.corutines.android)
    implementation(libs.jetbrain.corutines.guava)

    implementation(libs.gson)
}