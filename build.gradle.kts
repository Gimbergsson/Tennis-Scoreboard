import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.android.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.hilt.gradle.plugin)

        // NOTE: Do not place your application dependencies here;
        // they belong in the individual module build.gradle files
    }
}

plugins {
    alias(libs.plugins.compose.compiler) apply false
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
        delete(rootProject.buildDir)
    }
}