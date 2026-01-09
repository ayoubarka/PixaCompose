# Using PixaCompose Library in Your KMP Project

## Solution 1: Publish to Maven Local (Recommended for Development)

### Step 1: Publish the library to your local Maven repository

```bash
cd /Users/ayouboubarka/StudioProjects/PixaCompose
./gradlew publishToMavenLocal
```

This will publish the library to `~/.m2/repository/`

### Step 2: Add mavenLocal() to your app project

In your KMP project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenLocal() // Add this line FIRST
        google()
        mavenCentral()
    }
}
```

### Step 3: Add the dependency

In your app's `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.pixamob.pixacompose:pixacompose:0.0.1")
        }
    }
}
```

### Step 4: Sync and build

```bash
./gradlew clean build
```

---

## Solution 2: Use as a Composite Build (Alternative)

If you want to include the library directly without publishing:

### In your app project's `settings.gradle.kts`:

```kotlin
includeBuild("/Users/ayouboubarka/StudioProjects/PixaCompose") {
    dependencySubstitution {
        substitute(module("com.pixamob.pixacompose:pixacompose"))
            .using(project(":library"))
    }
}
```

Then use the dependency normally in your `build.gradle.kts`.

---

## Solution 3: Publish to GitHub Packages (For Team Sharing)

If you want to share with your team, you can publish to GitHub Packages.

### Step 1: Add GitHub Packages repository to library

In `library/build.gradle.kts`, add:

```kotlin
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ayoubarka/PixaCompose")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

### Step 2: Publish

```bash
./gradlew publish -Pgpr.user=YOUR_GITHUB_USERNAME -Pgpr.key=YOUR_GITHUB_TOKEN
```

### Step 3: In your app, add the repository

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/ayoubarka/PixaCompose")
        credentials {
            username = YOUR_GITHUB_USERNAME
            password = YOUR_GITHUB_TOKEN
        }
    }
    google()
    mavenCentral()
}
```

---

## Quick Fix (Right Now)

Run these commands:

```bash
# Go to library directory
cd /Users/ayouboubarka/StudioProjects/PixaCompose

# Publish to local Maven
./gradlew publishToMavenLocal

# Verify it was published
ls -la ~/.m2/repository/com/pixamob/pixacompose/pixacompose/0.0.1/
```

Then in your app project, add `mavenLocal()` as the FIRST repository in `settings.gradle.kts`.

