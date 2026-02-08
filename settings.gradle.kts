pluginManagement {

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {

    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }

    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Tennis Scoreboard"
include(":wearos")
include(":androidos")
include(":shared")
include(":tennis-score-manager")