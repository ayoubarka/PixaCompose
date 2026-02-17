import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.vanniktechMavenPublish)
}

group = "com.pixamob"
version = libs.versions.appVersionName.get()

kotlin {

    androidLibrary{
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        namespace = "com.pixamob.pixacompose.library"
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
        
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                }
            }
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "library"
            isStatic = true
        }
    }

/**
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

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "pixacompose"
            isStatic = true
            freeCompilerArgs += listOf(
                "-Xallocator=custom",
                "-Xruntime-logs=gc=info"
            )
        }
    }

*/

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.components.uiToolingPreview)

            // Material3 Adaptive Components
            implementation(libs.bundles.material3.adaptive.suite)

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
