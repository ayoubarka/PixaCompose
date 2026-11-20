import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.vanniktechMavenPublish)
}

group = "com.pixamob"
version = libs.versions.pixacompose.get()

kotlin {
    explicitApi()

    android {
        namespace = "com.pixamob.pixacompose.charts"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilations.all {
            compileTaskProvider.configure {
                compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
            }
        }
    }

    jvm()

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":pixacompose"))
            // Vico's core artifact: Compose foundation only, no Material.
            implementation(libs.vico.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = group.toString(),
        artifactId = "pixacompose-charts",
        version = version.toString(),
    )

    pom {
        name.set("PixaCompose Charts")
        description.set("Line, bar, scatter, combo, candlestick and pie charts for PixaCompose, drawn with Vico.")
        inceptionYear.set("2026")
        url.set("https://github.com/ayoubarka/PixaCompose")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("ayoubarka")
                name.set("Ayoub Oubarka")
                url.set("https://github.com/ayoubarka")
            }
        }
        scm {
            url.set("https://github.com/ayoubarka/PixaCompose")
            connection.set("scm:git:git://github.com/ayoubarka/PixaCompose.git")
            developerConnection.set("scm:git:ssh://git@github.com/ayoubarka/PixaCompose.git")
        }
    }

    publishToMavenCentral()
    signAllPublications()
}
