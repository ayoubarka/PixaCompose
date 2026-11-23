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
include(":pixacompose-media") // optional: remote images and other media, on Coil
include(":pixacompose-charts") // optional: charts, on Vico
include(":showcase")    // shared showcase UI + desktop entry point
include(":androidApp")  // thin Android wrapper around :showcase
