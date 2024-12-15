plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")

    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "se.dennisgimbergsson.tennisscoreboard"
    compileSdk = 35

    defaultConfig {
        applicationId = "se.dennisgimbergsson.tennisscoreboard"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.compiler)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.play.services.wearable)
    implementation(libs.play.services.tasks)

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.preference)

    implementation(libs.androidx.wear)
    implementation(libs.androidx.wear.input)
    implementation(libs.androidx.wear.tooling.preview)

    implementation(libs.kotlinx.corutines.core)
    implementation(libs.kotlinx.corutines.play.services)
    implementation(libs.jetbrain.corutines.android)
    implementation(libs.jetbrain.corutines.guava)

    implementation(libs.gson)

    testImplementation(libs.androidx.arch.core)
    testImplementation(libs.kotlinx.corutines.test)

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

    androidTestImplementation(libs.androidx.core.ktx)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.rules)

    // Espresso
    androidTestImplementation(libs.androidx.espresso.core)

    // Test rules and transitive dependencies:
    androidTestImplementation(libs.androidx.compose.ui.unit)
    // Needed for createComposeRule(), but not for createAndroidComposeRule<YourActivity>():
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}