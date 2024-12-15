import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }
    }
    dependencies {
        classpath(libs.android.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.hilt.gradle.plugin)

        classpath(libs.androidx.compose.compiler)
        classpath(libs.androidx.compose.runtime)

        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.11.2.0")

        // NOTE: Do not place your application dependencies here;
        // they belong in the individual module build.gradle files
    }
}

plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

allprojects {
    tasks {
        withType<KotlinCompile> {
            compilerOptions {
                jvmTarget = JvmTarget.JVM_17
            }
        }
    }
}

tasks {
    register("clean", Delete::class) {
        delete(rootProject.layout.buildDirectory)
    }
}