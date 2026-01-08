# PixaCompose Components

> Production-ready UI components for Compose Multiplatform

---

## 📦 Component Categories

```
components/
├── actions/          # Interactive elements
├── inputs/           # Form & data entry
├── feedback/         # User feedback
├── display/          # Content display
└── navigation/       # Navigation elements
```

---

## ✅ Implemented Components

### Actions Category
- ✅ **Button** - Multi-variant button with 7 styles, 6 sizes, 3 shapes, icons, loading state

---

## 📋 Planned Components

### Actions Category
- ⏳ IconButton - Compact icon-only button
- ⏳ FAB (Floating Action Button) - Prominent floating button
- ⏳ Tab - Horizontal/vertical tab component
- ⏳ Chip - Compact element for tags, filters

### Inputs Category
- ⏳ TextField - Single-line text input
- ⏳ TextArea - Multi-line text input
- ⏳ Checkbox - Multi-selection control
- ⏳ RadioButton - Single selection control
- ⏳ Switch - Binary toggle
- ⏳ Slider - Range selector
- ⏳ SearchBar - Specialized search input

### Feedback Category
- ⏳ Toast - Temporary notification
- ⏳ Snackbar - Temporary message with action
- ⏳ ProgressIndicator - Loading indicator (circular/linear)
- ⏳ Skeleton - Shimmer loading placeholder
- ⏳ Badge - Status indicator
- ⏳ Alert - Important inline message

### Display Category
- ⏳ Card - Container for grouped content
- ⏳ Avatar - User profile picture/placeholder
- ⏳ Image - Async image with loading/error states
- ⏳ EmptyState - Empty state placeholder

### Navigation Category
- ⏳ BottomNavBar - Bottom navigation bar
- ⏳ TopAppBar - Top app bar with title and actions
- ⏳ Drawer - Side navigation drawer

---

## 🎯 Component Standards

Every component in this library follows these standards:

### ✅ Single File Architecture
- All code in one file
- Clear section markers
- Easy to find everything

### ✅ Built from Primitives
- Uses `Box`, `Row`, `Column`, `Text`, `Icon`, `Canvas`
- NOT Material 3 component wrappers
- Full control over styling

### ✅ Theme Integration
- Consumes `AppTheme.colors`
- Consumes `AppTheme.typography`
- Uses dimension constants from theme
- Respects light/dark mode

### ✅ Customization First
- Multiple variants for different use cases
- Size options (Mini to Huge)
- Shape options where applicable
- Custom colors support
- Custom styling support

### ✅ State Management
- Enabled/Disabled states
- Loading states (where applicable)
- Focused states (where applicable)
- Error states (where applicable)
- Smooth animated transitions (150ms)

### ✅ Accessibility
- Proper semantic roles
- Content descriptions
- Touch target sizes (44dp minimum)
- Keyboard navigation support
- Screen reader friendly

### ✅ Documentation
- Comprehensive KDoc
- Parameter documentation
- @sample code blocks
- Usage examples

---

## 📖 Usage Example

```kotlin
import com.pixamob.pixacompose.components.actions.Button
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.ButtonSize

@Composable
fun MyScreen() {
    Button(
        text = "Click Me",
        variant = ButtonVariant.Solid,
        size = ButtonSize.Medium,
        onClick = { /* action */ }
    )
}
```

---

## 🎨 Theming

All components automatically use your theme:

```kotlin
@Composable
fun App() {
    AppTheme(
        lightColorPalette = myLightColors,
        darkColorPalette = myDarkColors,
        fontFamily = myFontFamily
    ) {
        // All components use your theme
        MyContent()
    }
}
```

---

## 📚 Documentation

- **Implementation Guide**: See `COMPONENT_REFERENCE.md` in project root
- **Button Usage**: See `BUTTON_USAGE.md` in project root
- **Theme Customization**: See `THEME_CUSTOMIZATION.md` in project root
- **Quick Reference**: See `QUICK_REFERENCE.md` in project root

---

## 🔧 Contributing

### Adding a New Component

1. **Read the Guide**: `COMPONENT_REFERENCE.md`
2. **Copy Template**: Use Button.kt as reference
3. **Follow Structure**:
   - Configuration section
   - Size configurations
   - Theme provider
   - Base component
   - Public API
   - Convenience variants
4. **Document**: Add KDoc and examples
5. **Test**: Verify all variants, sizes, states

### Component Checklist

- [ ] Single file in correct category folder
- [ ] Follows section structure
- [ ] Uses theme values (no hardcoded colors)
- [ ] Supports variants (3-5 options)
- [ ] Supports sizes (Mini to Huge)
- [ ] Handles states (enabled, disabled, etc.)
- [ ] Smooth animations (150ms transitions)
- [ ] Accessibility support
- [ ] Comprehensive documentation
- [ ] Usage examples provided

---

## 🎯 Design Principles

1. **Consistency** - All components follow same patterns
2. **Flexibility** - Support customization without complexity
3. **Performance** - Efficient rendering and animations
4. **Accessibility** - Usable by everyone
5. **Theme-Aware** - Automatic light/dark mode support
6. **Type-Safe** - Compile-time checks
7. **Well-Documented** - Clear usage and examples

---

## 💡 Tips

- **Start Simple**: Get basic version working first
- **Use Theme**: Never hardcode colors or dimensions
- **Animate Transitions**: Use `animateColorAsState`
- **Test Thoroughly**: All variants, sizes, states
- **Document Well**: Future you will thank you
- **Follow Patterns**: Consistency is key

---

## 🚀 Getting Started

1. **See Button.kt** for reference implementation
2. **Read COMPONENT_REFERENCE.md** for patterns
3. **Check BUTTON_USAGE.md** for usage examples
4. **Build and test** your components

---

**Happy Component Building! 🎨**

