# PixaCompose

[![Maven Central](https://img.shields.io/maven-central/v/com.pixamob/pixacompose)](https://central.sonatype.com/artifact/com.pixamob/pixacompose)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.7.1-blue)](https://www.jetbrains.com/lp/compose-multiplatform/)

A comprehensive UI component library for Compose Multiplatform mobile applications (Android & iOS) using Material 3 design principles.

## 🚀 Features

- **🎨 Rich Component Library**: TextField, TextArea, SearchBar, Buttons, Cards, and more
- **📱 Mobile-First**: Optimized for Android and iOS platforms
- **🎭 Material 3 Design**: Following the latest Material Design guidelines
- **🌈 Complete Theming System**: Fully customizable colors, typography, and dimensions
- **♿ Accessibility**: WCAG-compliant components with proper semantics
- **🔧 Highly Configurable**: Multiple variants and sizes for each component
- **⚡ Performance Optimized**: Smooth animations and efficient rendering
- **📦 Type-Safe**: Leverage Kotlin's type system for safer code

## 📦 Installation

### Gradle (Kotlin DSL)

Add the dependency to your `build.gradle.kts`:

```kotlin
commonMain.dependencies {
    implementation("com.pixamob:pixacompose:1.0.0")
}
```

### Gradle (Groovy)

Add the dependency to your `build.gradle`:

```groovy
commonMain {
    dependencies {
        implementation 'com.pixamob:pixacompose:1.0.0'
    }
}
```

### Version Catalog

Add to your `libs.versions.toml`:

```toml
[versions]
pixacompose = "1.0.0"

[libraries]
pixacompose = { module = "com.pixamob:pixacompose", version.ref = "pixacompose" }
```

Then in your `build.gradle.kts`:

```kotlin
commonMain.dependencies {
    implementation(libs.pixacompose)
}
```

## 🎯 Quick Start

### 1. Set Up Theme

```kotlin
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        // Your app content
        MyScreen()
    }
}
```

### 2. Use Components

#### TextField

```kotlin
import com.pixamob.pixacompose.components.inputs.OutlinedTextField

var text by remember { mutableStateOf("") }
OutlinedTextField(
    value = text,
    onValueChange = { text = it },
    label = "Email",
    placeholder = "Enter your email",
    helperText = "We'll never share your email"
)
```

#### TextArea

```kotlin
import com.pixamob.pixacompose.components.inputs.OutlinedTextArea

var description by remember { mutableStateOf("") }
OutlinedTextArea(
    value = description,
    onValueChange = { description = it },
    label = "Description",
    placeholder = "Enter description...",
    maxLength = 500,
    showCharacterCount = true,
    minLines = 4,
    maxLines = 8
)
```

#### SearchBar

```kotlin
import com.pixamob.pixacompose.components.inputs.ElevatedSearchBar
import com.pixamob.pixacompose.components.inputs.SearchSuggestion

var query by remember { mutableStateOf("") }
val suggestions = remember {
    listOf(
        SearchSuggestion("React Native", isRecent = true),
        SearchSuggestion("React Hooks"),
        SearchSuggestion("React Router")
    )
}

ElevatedSearchBar(
    value = query,
    onValueChange = { query = it },
    placeholder = "Search...",
    suggestions = suggestions,
    showSuggestions = query.length >= 2,
    onSearch = { performSearch(query) }
)
```

## 📚 Components

### Input Components
- **TextField**: Single-line text input with variants (Filled, Outlined, Ghost)
- **TextArea**: Multi-line text input for longer content
- **SearchBar**: Dynamic search with suggestions and filtering

### Button Components
- **Button**: Primary, Secondary, Tertiary, and more variants
- **IconButton**: Button with icon support
- **FloatingActionButton**: Material 3 FAB

### Display Components
- **Card**: Elevated, Outlined, and Filled variants
- **Badge**: Small status indicators
- **Chip**: Compact elements for actions or information

### Navigation Components
- **NavigationBar**: Bottom navigation
- **TabRow**: Horizontal tabs
- **Drawer**: Navigation drawer

### Feedback Components
- **Dialog**: Modal dialogs
- **Snackbar**: Brief messages
- **ProgressIndicator**: Linear and circular progress

## 🎨 Theming

PixaCompose provides a comprehensive theming system:

### Colors

```kotlin
AppTheme.colors.brandContentDefault
AppTheme.colors.baseSurfaceDefault
AppTheme.colors.errorContentDefault
// ... and many more
```

### Typography

```kotlin
AppTheme.typography.bodyLarge
AppTheme.typography.headlineBold
AppTheme.typography.labelMedium
// ... complete typography scale
```

### Dimensions

```kotlin
ComponentSize.ButtonMedium
Spacing.Large
IconSize.Medium
BorderWidth.Thin
CornerRadius.Medium
Elevation.Medium
// ... comprehensive sizing system
```

## 🔧 Configuration

### Component Sizes

All components support three standard sizes:

```kotlin
// Small - Compact layouts
OutlinedTextField(
    value = text,
    onValueChange = { text = it },
    size = TextFieldSize.Small
)

// Medium - Default, touch-friendly
OutlinedTextField(
    value = text,
    onValueChange = { text = it },
    size = TextFieldSize.Medium
)

// Large - Maximum comfort
OutlinedTextField(
    value = text,
    onValueChange = { text = it },
    size = TextFieldSize.Large
)
```

### Component Variants

Most components offer multiple visual styles:

```kotlin
// Filled variant
FilledTextField(value = text, onValueChange = { text = it })

// Outlined variant (default)
OutlinedTextField(value = text, onValueChange = { text = it })

// Ghost variant (minimal)
GhostTextField(value = text, onValueChange = { text = it })
```

## 📖 Documentation

For comprehensive documentation, visit:
- [Quick Reference Guide](QUICK_REFERENCE_TEXTAREA_SEARCHBAR.md)
- [Implementation Details](TEXTAREA_SEARCHBAR_IMPLEMENTATION.md)
- [Usage Guide](USAGE_GUIDE.md)
- [API Documentation](https://pixamob.github.io/PixaCompose/)

## 🤝 Contributing

Contributions are welcome! Please read our [Contributing Guide](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## 📄 License

```
Copyright 2025 Ayoub Arka

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 🙏 Acknowledgments

- Built with [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- Follows [Material 3 Design](https://m3.material.io/) guidelines
- Inspired by the Compose ecosystem

## 📞 Contact

- **Author**: Ayoub Arka
- **Email**: ayoub@pixamob.com
- **GitHub**: [@ayoubarka](https://github.com/ayoubarka)
- **Issues**: [GitHub Issues](https://github.com/ayoubarka/PixaCompose/issues)

---

## 🔐 Publishing to Maven Central (For Maintainers)

### Prerequisites

1. **Sonatype Account**: Create an account at [Sonatype Central Portal](https://central.sonatype.com/)
2. **Verified Namespace**: Verify ownership of `com.pixamob` namespace
3. **GPG Key**: Generate and publish a GPG key for signing artifacts

### Local Publishing Setup

Create or update `~/.gradle/gradle.properties`:

```properties
# Sonatype Credentials
mavenCentralUsername=YOUR_SONATYPE_USERNAME
mavenCentralPassword=YOUR_SONATYPE_PASSWORD

# GPG Signing
signing.keyId=YOUR_GPG_KEY_ID_LAST_8_CHARS
signing.password=YOUR_GPG_PASSPHRASE
signing.secretKeyRingFile=/Users/YOUR_USERNAME/.gnupg/secring.gpg
```

### Generate GPG Key

```bash
# Generate new key
gpg --full-generate-key

# List keys to get the key ID
gpg --list-secret-keys --keyid-format=long

# Export public key to keyserver
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID

# Export private key (for CI)
gpg --export-secret-keys YOUR_KEY_ID | base64 > private-key.txt
```

### Local Publishing

```bash
# Build and test
./gradlew :library:build :library:allTests

# Publish to Maven Central
./gradlew :library:publishToMavenCentral --no-configuration-cache

# Or publish all variants
./gradlew :library:publishAllPublicationsToMavenCentral
```

### GitHub Actions Setup

Configure these secrets in your GitHub repository (Settings → Secrets and variables → Actions):

1. **SONATYPE_USERNAME**: Your Sonatype username
2. **SONATYPE_PASSWORD**: Your Sonatype password
3. **GPG_PRIVATE_KEY**: Base64-encoded private key (content of private-key.txt)
4. **GPG_PASSPHRASE**: Your GPG key passphrase

### Automated Publishing via GitHub Actions

1. **Create a tag**:
   ```bash
   git tag -a v1.0.0 -m "Release version 1.0.0"
   git push origin v1.0.0
   ```

2. **GitHub Action automatically**:
   - Builds all targets (Android, iOS)
   - Runs tests
   - Signs artifacts with GPG
   - Publishes to Maven Central
   - Creates GitHub Release with artifacts

3. **Verify publication**:
   - Check [Maven Central](https://central.sonatype.com/artifact/com.pixamob/pixacompose)
   - Allow 10-30 minutes for sync to Maven Central Search

### Manual Release Process

If you prefer manual control:

```bash
# 1. Update version in gradle.properties
# 2. Commit and tag
git add .
git commit -m "Release v1.0.0"
git tag -a v1.0.0 -m "Release version 1.0.0"

# 3. Build and publish
./gradlew :library:publishToMavenCentral --no-configuration-cache

# 4. Push changes
git push origin main
git push origin v1.0.0
```

### Troubleshooting

**Signing Issues**:
```bash
# Verify GPG is working
gpg --list-secret-keys

# Test signing
echo "test" | gpg --clearsign
```

**Publication Issues**:
```bash
# Check publication repository
./gradlew :library:publishToMavenLocal

# Verify in ~/.m2/repository/com/pixamob/pixacompose/
```

**CI Issues**:
- Ensure secrets are properly base64 encoded
- Check GitHub Actions logs for detailed errors
- Verify GPG key hasn't expired

### Version Management

Update version in `gradle/libs.versions.toml`:

```toml
[versions]
appVersionName = "1.0.0"
```

Or in `gradle.properties` if using that approach.

### Release Checklist

- [ ] Update version number
- [ ] Update CHANGELOG.md
- [ ] Run all tests locally
- [ ] Update documentation
- [ ] Commit all changes
- [ ] Create and push tag
- [ ] Verify GitHub Action completes successfully
- [ ] Check Maven Central for new version (wait 10-30 minutes)
- [ ] Update README with new version
- [ ] Announce release

---

Made with ❤️ by [Ayoub Arka](https://github.com/ayoubarka)

