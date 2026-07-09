package com.pixamob.pixacompose.demo

import androidx.compose.runtime.Composable

enum class ComponentCategory(val displayName: String, val icon: String) {
    Actions("Actions", "Actions"),
    Inputs("Inputs", "Inputs"),
    Display("Display", "Display"),
    Feedback("Feedback", "Feedback"),
    Overlay("Overlay", "Overlay"),
    Navigation("Navigation", "Navigation")
}

sealed class ComponentEntry(
    val name: String,
    val category: ComponentCategory,
    val description: String
) {
    abstract val showcase: @Composable () -> Unit
    // ── Actions ──────────────────────────────────────────
    data object Button : ComponentEntry(
        name = "Button", category = ComponentCategory.Actions,
        description = "Versatile button with variants, sizes, shapes, loading & skeleton states"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.ButtonShowcase() } }

    data object Chip : ComponentEntry(
        name = "Chip", category = ComponentCategory.Actions,
        description = "Compact chips with variants, selection, dismiss, and icon support"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.ChipShowcase() } }

    data object Accordion : ComponentEntry(
        name = "Accordion", category = ComponentCategory.Actions,
        description = "Collapsible panels with single/multi expand, icons, and custom content"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.AccordionShowcase() } }

    data object Tabs : ComponentEntry(
        name = "Tabs", category = ComponentCategory.Actions,
        description = "Tab components: fixed, scrollable, segmented, vertical, with badges"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.TabsShowcase() } }

    // ── Inputs ───────────────────────────────────────────
    data object TextField : ComponentEntry(
        name = "TextField", category = ComponentCategory.Inputs,
        description = "Input fields: filled, outlined, ghost, email, password, search variants"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.TextFieldShowcase() } }

    data object TextArea : ComponentEntry(
        name = "TextArea", category = ComponentCategory.Inputs,
        description = "Multi-line text areas with character count, variants, and sizes"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.TextAreaShowcase() } }

    data object Checkbox : ComponentEntry(
        name = "Checkbox", category = ComponentCategory.Inputs,
        description = "Checkboxes with labels, tri-state, variants, and label positions"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.CheckboxShowcase() } }

    data object RadioButton : ComponentEntry(
        name = "RadioButton", category = ComponentCategory.Inputs,
        description = "Radio buttons with groups, horizontal/vertical layout, variants"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.RadioButtonShowcase() } }

    data object Switch : ComponentEntry(
        name = "Switch", category = ComponentCategory.Inputs,
        description = "Toggles with filled, outlined, minimal variants and label support"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.SwitchShowcase() } }

    data object Slider : ComponentEntry(
        name = "Slider", category = ComponentCategory.Inputs,
        description = "Sliders with variants, sizes, steps, value display, and gradients"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.SliderShowcase() } }

    data object SearchBar : ComponentEntry(
        name = "SearchBar", category = ComponentCategory.Inputs,
        description = "Search bars with suggestions, voice/search actions, multiple variants"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.SearchBarShowcase() } }

    data object Dropdown : ComponentEntry(
        name = "Dropdown", category = ComponentCategory.Inputs,
        description = "Dropdown selectors with items, labels, icons, and variants"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.DropdownShowcase() } }

    data object DatePicker : ComponentEntry(
        name = "DatePicker", category = ComponentCategory.Inputs,
        description = "Date pickers with calendar, wheel, multi-select, and range modes"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.DatePickerShowcase() } }

    data object TimePicker : ComponentEntry(
        name = "TimePicker", category = ComponentCategory.Inputs,
        description = "Time pickers with wheel, clock modes, intervals, and ranges"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.TimePickerShowcase() } }

    data object ColorPicker : ComponentEntry(
        name = "ColorPicker", category = ComponentCategory.Inputs,
        description = "Color pickers with palette, hex input, history, and mode switching"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.ColorPickerShowcase() } }

    // ── Display ──────────────────────────────────────────
    data object Card : ComponentEntry(
        name = "Card", category = ComponentCategory.Display,
        description = "Cards with variants, elevation, padding, skeleton, and domain presets"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.CardShowcase() } }

    data object Avatar : ComponentEntry(
        name = "Avatar", category = ComponentCategory.Display,
        description = "Avatars with image, text, icon, status badges, and group display"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.AvatarShowcase() } }

    data object Icon : ComponentEntry(
        name = "Icon", category = ComponentCategory.Display,
        description = "Icons from vectors, painters, URLs, with tint, size, and animation"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.IconShowcase() } }

    data object Image : ComponentEntry(
        name = "Image", category = ComponentCategory.Display,
        description = "Images from URLs, painters, vectors, SVGs with crossfade and shapes"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.ImageShowcase() } }

    data object Divider : ComponentEntry(
        name = "Divider", category = ComponentCategory.Display,
        description = "Dividers with horizontal/vertical orientation, thickness, and color"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.DividerShowcase() } }

    data object Chart : ComponentEntry(
        name = "Chart", category = ComponentCategory.Display,
        description = "Charts: line, column, candlestick, trend, comparison, multi-line"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.ChartShowcase() } }

    // ── Feedback ─────────────────────────────────────────
    data object Alert : ComponentEntry(
        name = "Alert", category = ComponentCategory.Feedback,
        description = "Alerts with info/success/warning/error variants, dismiss, and actions"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.AlertShowcase() } }

    data object Toast : ComponentEntry(
        name = "Toast", category = ComponentCategory.Feedback,
        description = "Toast notifications with global host and position configuration"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.ToastShowcase() } }

    data object Badge : ComponentEntry(
        name = "Badge", category = ComponentCategory.Feedback,
        description = "Badges with variants, sizes, styles, pulse animation, and BadgedBox"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.BadgeShowcase() } }

    data object Skeleton : ComponentEntry(
        name = "Skeleton", category = ComponentCategory.Feedback,
        description = "Skeleton loaders: text, circle, image, button, card, list, grid"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.SkeletonShowcase() } }

    data object Indicator : ComponentEntry(
        name = "Indicator", category = ComponentCategory.Feedback,
        description = "Progress indicators: circular, linear, segmented, pager, loading"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.IndicatorShowcase() } }

    data object EmptyState : ComponentEntry(
        name = "EmptyState", category = ComponentCategory.Feedback,
        description = "Empty states with types: empty, search, error, network, permission"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.EmptyStateShowcase() } }

    data object Snackbar : ComponentEntry(
        name = "Snackbar", category = ComponentCategory.Feedback,
        description = "Snackbar notifications with global host and custom messages"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.SnackbarShowcase() } }

    // ── Overlay ──────────────────────────────────────────
    data object Dialog : ComponentEntry(
        name = "Dialog", category = ComponentCategory.Overlay,
        description = "Dialogs: alert, confirm, destructive with variants, sizes, and content"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.DialogShowcase() } }

    data object Menu : ComponentEntry(
        name = "Menu", category = ComponentCategory.Overlay,
        description = "Menus with items, icons, dividers, and alignment options"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.MenuShowcase() } }

    data object Popover : ComponentEntry(
        name = "Popover", category = ComponentCategory.Overlay,
        description = "Popover overlays with position control and custom content"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.PopoverShowcase() } }

    data object BottomSheet : ComponentEntry(
        name = "BottomSheet", category = ComponentCategory.Overlay,
        description = "Bottom sheets: standard, expandable, list, confirmation with drag handle"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.BottomSheetShowcase() } }

    data object Tooltip : ComponentEntry(
        name = "Tooltip", category = ComponentCategory.Overlay,
        description = "Tooltips with positions, auto-dismiss, and hover/click triggers"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.TooltipShowcase() } }

    // ── Navigation ───────────────────────────────────────
    data object TopNavBar : ComponentEntry(
        name = "TopNavBar", category = ComponentCategory.Navigation,
        description = "Top navigation bars with title, actions, profile avatar, and scrolling"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.TopNavBarShowcase() } }

    data object BottomNavBar : ComponentEntry(
        name = "BottomNavBar", category = ComponentCategory.Navigation,
        description = "Bottom navigation bars with center action, icons, labels, and scrolling"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.BottomNavBarShowcase() } }

    data object TabBar : ComponentEntry(
        name = "TabBar", category = ComponentCategory.Navigation,
        description = "Tab bars with variants, scrollable tabs, and colors"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.TabBarShowcase() } }

    data object Drawer : ComponentEntry(
        name = "Drawer", category = ComponentCategory.Navigation,
        description = "Navigation drawers with sections, items, header, footer, and positions"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.DrawerShowcase() } }

    data object Stepper : ComponentEntry(
        name = "Stepper", category = ComponentCategory.Navigation,
        description = "Steppers with vertical/horizontal orientation, indicators, and connectors"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.components.StepperShowcase() } }

    companion object {
        val all: List<ComponentEntry> = listOf(
            Button, Chip, Accordion, Tabs,
            TextField, TextArea, Checkbox, RadioButton, Switch, Slider, SearchBar, Dropdown,
            DatePicker, TimePicker, ColorPicker,
            Card, Avatar, Icon, Image, Divider, Chart,
            Alert, Toast, Badge, Skeleton, Indicator, EmptyState, Snackbar,
            Dialog, Menu, Popover, BottomSheet, Tooltip,
            TopNavBar, BottomNavBar, TabBar, Drawer, Stepper
        )

        fun byCategory(category: ComponentCategory): List<ComponentEntry> =
            all.filter { it.category == category }

        fun find(name: String): ComponentEntry? =
            all.find { it.name.equals(name, ignoreCase = true) }
    }
}
