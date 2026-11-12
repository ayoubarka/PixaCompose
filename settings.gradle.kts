pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PixaCompose"

include(":pixacompose") // the published library
include(":showcase")    // shared showcase UI + desktop entry point
include(":androidApp")  // thin Android wrapper around :showcase
