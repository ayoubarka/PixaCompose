import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import groovyjarjarantlr.build.ANTLR.compiler
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "com.pixamob"
version = libs.versions.appVersionName.get()

kotlin {
    @Suppress("UnstableApiUsage")
    androidLibrary {
        namespace = "com.pixamob"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava() // enable java compilation support

        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                }
            }
        }
    }

    // Temporarily disabled due to kmp-date-time-picker JS incompatibility
    // js {
    //     browser()
    //     binaries.executable()
    //     compiler = KotlinJsCompilerType.IR.toString()
    // }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "pixacompose"
            isStatic = true
        }
    }


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
    }
}

mavenPublishing {
    // Configure POM metadata
    coordinates(
        groupId = group.toString(),
        artifactId = "pixacompose",
        version = version.toString()
    )

    pom {
        name.set("PixaCompose")
        description.set("A comprehensive UI component library for Compose Multiplatform mobile applications (Android & iOS) using Material 3 design principles. Features include TextField, TextArea, SearchBar, Buttons, Cards, and a complete theming system.")
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
                email.set("oubarka.ayoub@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/ayoubarka/PixaCompose")
            connection.set("scm:git:git://github.com/ayoubarka/PixaCompose.git")
            developerConnection.set("scm:git:ssh://git@github.com/ayoubarka/PixaCompose.git")
        }

        issueManagement {
            system.set("GitHub Issues")
            url.set("https://github.com/ayoubarka/PixaCompose/issues")
        }
    }

    // Publish to Sonatype Central Portal
    publishToMavenCentral()

    // Sign all publications
    signAllPublications()

}
