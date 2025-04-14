plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    jacoco
}

android {
    namespace = "se.dennisgimbergsson.shared"
    compileSdk = 35

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            testCoverage {
                enableUnitTestCoverage = true
            }
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    testOptions {
        animationsDisabled = true
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.wear)
    implementation(libs.androidx.wear.tooling.preview)

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.work)

    implementation(libs.play.services.wearable)
    implementation(libs.play.services.tasks)

    implementation(libs.material)

    implementation(libs.gson)

    /**
     * Unit test dependencies
     */
    // Mockk
    testImplementation(libs.mockk)

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
     * UI Test dependencies
     */
    // Mockk
    androidTestImplementation(libs.mockk)

    // AndroidX Test
    androidTestImplementation(libs.androidx.core.ktx)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.rules)
}

// Filter out unnecessary files from code coverage
val fileFilter = setOf(
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*",

    // Hilt
    "**/databinding/*",
    "**/Dagger*",
    "**/Hilt*",
    "**/DataBinding*",
    "**/DataBinder*",
    "**/*_*",
    "**/*Module.kt",
    "**/*AppModule.java",
    "**/_se_dennisgimbergsson_shared_di_AppModule.java",
    "**/di/**",
    "dagger.hilt.internal/*",
    "/transformDebugClassesWithAsm/dirs/hilt_aggregated_deps/*",
)

tasks.register("jacocoTestReport", JacocoReport::class) {
    dependsOn("testDebugUnitTest", "createDebugUnitTestCoverageReport")

    val debugTree = fileTree("${layout.buildDirectory}/intermediates/classes/debug")
        .apply { exclude(fileFilter) }

    println(debugTree.files.toString())

    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree("${layout.buildDirectory}").apply {
        include(
            "jacoco/testDebugUnitTest.exec",
            "outputs/code-coverage/connected/*coverage.ec",
            "**Module*.*"
        )
    })
}