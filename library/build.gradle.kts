import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "com.pixamob.pixacompose"
version = libs.versions.appVersionName.get()

kotlin {
    androidLibrary {
        namespace = "com.pixamob.pixacompose"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava() // enable java compilation support
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(
                    JvmTarget.JVM_11
                )
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // Disable linuxX64 target as it's not supported by Compose Multiplatform
    @Suppress("OPT_IN_USAGE")
    applyDefaultHierarchyTemplate {
        common {
            group("mobile") {
                group("ios") {
                    withIos()
                }
                withAndroidTarget()
            }
        }
    }

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose Multiplatform dependencies
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Material3 Adaptive Components
            implementation(libs.material3.adaptive)
            implementation(libs.material3.adaptive.layout)
            implementation(libs.material3.adaptive.navigation)

            // Kotlinx libraries
            implementation(libs.kotlinx.datetime)

            // UI Components
            implementation(libs.cmp.datetime.picker)
            implementation(libs.cmp.constraintlayout)
            implementation(libs.cmp.shimmer)
            implementation(libs.coil.compose)
            implementation(libs.coil.network)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "pixacompose", version.toString())

    pom {
        name = "PixaCompose"
        description = "A comprehensive UI component library for Compose Multiplatform mobile applications using Material 3 design principles."
        inceptionYear = "2026"
        url = "https://github.com/ayoubarka/PixaCompose/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "repo"
            }
        }
        developers {
            developer {
                id = "ayoubarka"
                name = "Ayoub Arka"
                url = "https://github.com/ayoubarka/"
            }
        }
        scm {
            url = "https://github.com/ayoubarka/PixaCompose/"
            connection = "scm:git:git://github.com/ayoubarka/PixaCompose.git"
            developerConnection = "scm:git:ssh://git@github.com/ayoubarka/PixaCompose.git"
        }
    }
}
