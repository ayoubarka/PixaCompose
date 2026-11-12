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
include(":pixacompose-image") // optional: remote images, on Coil
include(":pixacompose-charts") // optional: line and bar charts, on Vico
include(":pixacompose-datetime") // optional: calendar and date picker
include(":showcase")    // shared showcase UI + desktop entry point
include(":androidApp")  // thin Android wrapper around :showcase
