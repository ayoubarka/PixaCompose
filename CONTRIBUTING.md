# Contributing to PixaCompose

Thank you for your interest in contributing to PixaCompose! This document provides guidelines and instructions for contributing to the project.

## 🎯 Project Vision

PixaCompose is a production-ready UI component library for Compose Multiplatform mobile applications, built with:
- Mobile-first design principles
- Material 3 design system
- Clean, maintainable code
- Comprehensive documentation
- Excellent developer experience

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Component Standards](#component-standards)
- [Pull Request Process](#pull-request-process)
- [Coding Conventions](#coding-conventions)
- [Testing Requirements](#testing-requirements)

## 📜 Code of Conduct

### Our Standards

- Be respectful and inclusive
- Welcome newcomers and help them learn
- Focus on what is best for the community
- Show empathy towards other community members

### Unacceptable Behavior

- Harassment, discrimination, or offensive comments
- Personal or political attacks
- Public or private harassment
- Publishing others' private information
- Unprofessional conduct

## 🚀 Getting Started

### Prerequisites

- JDK 11 or higher
- Android Studio Hedgehog or later (for Android development)
- Xcode 14+ (for iOS development on macOS)
- Git

### Fork and Clone

1. Fork the repository on GitHub
2. Clone your fork:
```bash
git clone https://github.com/YOUR_USERNAME/PixaCompose.git
cd PixaCompose
```

3. Add upstream remote:
```bash
git remote add upstream https://github.com/pixamob/pixacompose.git
```

## 💻 Development Setup

### Build the Project

```bash
./gradlew build
```

### Run Tests

```bash
# Android tests
./gradlew :library:testDebugUnitTest

# iOS tests
./gradlew :library:iosSimulatorArm64Test

# All tests
./gradlew test
```

### Project Structure

```
library/src/commonMain/kotlin/com/pixamob/pixacompose/
├── theme/              # Theme system (Color, Typography, Dimen, etc.)
└── elements/           # UI components
    ├── actions/        # Interactive components (Button, FAB, etc.)
    ├── inputs/         # Form inputs (TextField, Checkbox, etc.)
    ├── feedback/       # User feedback (Toast, Alert, etc.)
    ├── display/        # Content display (Card, Avatar, etc.)
    ├── navigation/     # Navigation components
    ├── overlay/        # Modal/overlay components
    └── layout/         # Layout components
```

## 🎨 Component Standards

### Component File Structure

Every component MUST follow this structure:

```kotlin
package com.pixamob.pixacompose.elements.[category]

// ============================================================================
// CONFIGURATION
// ============================================================================

enum class ComponentVariant { /* variants */ }
enum class ComponentSize { /* sizes */ }
data class ComponentColors( /* color properties */ )
data class ComponentStateColors( /* state-based colors */ )

// ============================================================================
// THEME PROVIDER
// ============================================================================

@Composable
private fun getComponentTheme(
    variant: ComponentVariant,
    colors: ColorPalette
): ComponentStateColors { /* theme mapping */ }

// ============================================================================
// BASE COMPONENT (Internal)
// ============================================================================

@Composable
private fun BaseComponent( /* core implementation */ ) { }

// ============================================================================
// PUBLIC API
// ============================================================================

@Composable
fun Component( /* public interface */ ) { }

// ============================================================================
// CONVENIENCE VARIANTS (Optional)
// ============================================================================

@Composable
fun ComponentVariantOne( /* convenience functions */ ) = Component(...)

/**
 * USAGE EXAMPLES:
 * 
 * [Include clear usage examples]
 */
```

### Core Principles

1. **Build from Primitives**: Use `Box`, `Row`, `Column`, `Canvas` - NOT Material 3 wrappers
2. **Single File Components**: All logic in one file with clear sections
3. **Theme-Aware**: Use `AppTheme.colors`, `AppTheme.typography`, etc.
4. **Mobile-First**: Minimum 44dp touch targets
5. **Accessibility**: Proper semantic roles and content descriptions

### Naming Conventions

- **Components**: PascalCase (`Button`, `TextField`)
- **Variants**: PascalCase enums (`ButtonVariant.Flat`)
- **Sizes**: PascalCase enums (`ButtonSize.Medium`)
- **Functions**: camelCase (`onClick`, `getButtonTheme`)
- **Files**: Match component name (`Button.kt`)

### Documentation

Every component must include:

- **KDoc comments** for public functions
- **Parameter descriptions** for complex parameters
- **Usage examples** at the end of the file
- **State management** documentation
- **Accessibility notes** where applicable

Example:

```kotlin
/**
 * A primary action button with multiple variants, sizes, and states.
 *
 * @param text The button label text
 * @param onClick Callback invoked when button is clicked
 * @param modifier Modifier to be applied to the button
 * @param variant Visual style variant (Flat, Outlined, Ghost)
 * @param enabled Whether the button is interactive
 * @param loading Whether to show loading indicator
 * @param size Button size (Mini to Huge)
 * @param shape Button shape (Default, Pill, Circle)
 * @param leadingIcon Optional icon displayed before text
 * @param trailingIcon Optional icon displayed after text
 */
@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    // ... other parameters
) { }
```

## 🔄 Pull Request Process

### Before Submitting

1. **Update from upstream**:
```bash
git fetch upstream
git rebase upstream/main
```

2. **Run all tests**:
```bash
./gradlew test
```

3. **Check code formatting** (if configured):
```bash
./gradlew spotlessCheck
```

4. **Build successfully**:
```bash
./gradlew build
```

### Creating a Pull Request

1. **Create a feature branch**:
```bash
git checkout -b feature/component-name
# or
git checkout -b fix/issue-description
```

2. **Make your changes** following coding conventions

3. **Commit with clear messages**:
```bash
git commit -m "feat: add Button component with variants"
# or
git commit -m "fix: correct TextField padding in small size"
```

**Commit message format**:
- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation only
- `style:` Code style/formatting
- `refactor:` Code refactoring
- `test:` Adding tests
- `chore:` Maintenance tasks

4. **Push to your fork**:
```bash
git push origin feature/component-name
```

5. **Open Pull Request** on GitHub with:
   - Clear title and description
   - Reference to related issues
   - Screenshots/GIFs for UI changes
   - Checklist of completed items

### PR Checklist

- [ ] Code follows project structure and conventions
- [ ] All tests pass
- [ ] New tests added for new functionality
- [ ] Documentation updated (if applicable)
- [ ] Usage examples included
- [ ] No console warnings or errors
- [ ] Tested on both Android and iOS (if applicable)
- [ ] Accessibility considerations addressed
- [ ] Dark theme tested (if UI component)

## 🔍 Code Review Process

1. **Automated checks** must pass (CI/CD)
2. **At least one maintainer** must review
3. **Address feedback** by pushing new commits
4. **Maintainer will merge** when approved

## 🧪 Testing Requirements

### Unit Tests

Every component must have unit tests covering:

- Component rendering
- Variant rendering
- State changes
- User interactions
- Edge cases

Example:

```kotlin
class ButtonTest {
    @Test
    fun button_rendersWithText() {
        composeTestRule.setContent {
            Button(text = "Click Me", onClick = {})
        }
        composeTestRule.onNodeWithText("Click Me").assertExists()
    }
    
    @Test
    fun button_disabled_doesNotTriggerClick() {
        var clicked = false
        composeTestRule.setContent {
            Button(
                text = "Click Me",
                onClick = { clicked = true },
                enabled = false
            )
        }
        composeTestRule.onNodeWithText("Click Me").performClick()
        assertFalse(clicked)
    }
}
```

### Visual Tests

For UI components, include:

- Screenshots for each variant
- Dark/light theme comparison
- Different size variations
- State variations (default, disabled, etc.)

## 📝 Documentation Requirements

### Component Documentation

Each component needs:

1. **README section** in main README.md (if major component)
2. **Usage examples** in component file comments
3. **API documentation** via KDoc
4. **Implementation notes** in IMPLEMENTATION_GUIDE.md

### Example Documentation

```kotlin
/**
 * USAGE EXAMPLES:
 *
 * Basic usage:
 * ```
 * Button(
 *     text = "Submit",
 *     onClick = { /* handle click */ }
 * )
 * ```
 *
 * With icon:
 * ```
 * Button(
 *     text = "Save",
 *     leadingIcon = painterResource(Res.drawable.ic_save),
 *     onClick = { /* handle click */ }
 * )
 * ```
 *
 * Loading state:
 * ```
 * var isLoading by remember { mutableStateOf(false) }
 * Button(
 *     text = "Submit",
 *     loading = isLoading,
 *     onClick = {
 *         isLoading = true
 *         // perform async operation
 *     }
 * )
 * ```
 */
```

## 🎨 Design Guidelines

### Variants

Provide 2-4 practical variants:
- **Primary**: Most common use case (default)
- **Secondary**: Alternative styling
- **Tertiary**: Less emphasis
- **(Optional) Special**: Unique use cases

### Sizes

Provide 4-6 sizes:
- **Small**: Compact spaces
- **Medium**: Default (usually 44dp for touch targets)
- **Large**: Prominent elements
- **(Optional) Mini/Huge**: Edge cases

### States

Handle these states:
- **Default**: Normal interactive state
- **Hover**: Desktop hover state (future)
- **Focused**: Keyboard focus
- **Pressed**: Active press state
- **Disabled**: Non-interactive state
- **Loading**: Async operation state (if applicable)
- **Error**: Error state (for inputs)

## 🐛 Reporting Issues

### Bug Reports

Include:

1. **Description**: Clear description of the bug
2. **Steps to Reproduce**: Detailed steps
3. **Expected Behavior**: What should happen
4. **Actual Behavior**: What actually happens
5. **Environment**:
   - OS (Android/iOS version)
   - Library version
   - Kotlin version
6. **Code Sample**: Minimal reproduction code
7. **Screenshots**: If applicable

### Feature Requests

Include:

1. **Use Case**: Why this feature is needed
2. **Proposed Solution**: Your idea for implementation
3. **Alternatives**: Other solutions considered
4. **Additional Context**: Any other relevant information

## 📞 Getting Help

- **Issues**: [GitHub Issues](https://github.com/pixamob/pixacompose/issues)
- **Discussions**: [GitHub Discussions](https://github.com/pixamob/pixacompose/discussions)
- **Documentation**: [Implementation Guide](IMPLEMENTATION_GUIDE.md)

## 🙏 Recognition

Contributors are recognized in:
- CONTRIBUTORS.md file
- Release notes
- Project README (major contributions)

## 📄 License

By contributing, you agree that your contributions will be licensed under the Apache License 2.0, the same license as the project.

---

Thank you for contributing to PixaCompose! 🎉

