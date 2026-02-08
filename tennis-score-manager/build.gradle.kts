import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    jacoco
}

android {
    namespace = "se.dennisgimbergsson.tennisscoring"
    compileSdk = 36

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    testOptions {
        animationsDisabled = true
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Gson for serializer and deserializer
    implementation(libs.gson)

    /**
     * Unit test dependencies
     */
    testImplementation(testFixtures(project(":shared")))

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

    // Mockk
    testImplementation(libs.mockk)
}

tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed",)
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
    }
}