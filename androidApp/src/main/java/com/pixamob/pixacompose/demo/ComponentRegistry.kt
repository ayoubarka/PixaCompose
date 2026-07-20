package com.pixamob.pixacompose.demo

import androidx.compose.runtime.Composable

enum class ComponentCategory(val displayName: String) {
    Actions("Actions"),
    Inputs("Inputs"),
    Display("Display"),
    Feedback("Feedback"),
    Navigation("Navigation"),
    Overlay("Overlay")
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
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.actions.ButtonShowcase() } }

    data object Chip : ComponentEntry(
        name = "Chip", category = ComponentCategory.Actions,
        description = "Chips for tags, filters, choices with icon, avatar, and dismiss"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.actions.ChipShowcase() } }

    data object FAB : ComponentEntry(
        name = "FAB", category = ComponentCategory.Actions,
        description = "Floating action buttons: regular, mini, and extended variants"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.actions.FABShowcase() } }

    data object IconButton : ComponentEntry(
        name = "IconButton", category = ComponentCategory.Actions,
        description = "Icon buttons with variant, size, and loading state"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.actions.IconButtonShowcase() } }

    data object Link : ComponentEntry(
        name = "Link", category = ComponentCategory.Actions,
        description = "Inline text links with underline, icon, and external indicator"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.actions.LinkShowcase() } }

    data object SegmentedButton : ComponentEntry(
        name = "SegmentedButton", category = ComponentCategory.Actions,
        description = "Segmented controls for mutually exclusive options"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.actions.SegmentedButtonShowcase() } }

    data object SlidingButton : ComponentEntry(
        name = "SlidingButton", category = ComponentCategory.Actions,
        description = "Slide-to-confirm action button with drag gesture"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.actions.SlidingButtonShowcase() } }

    data object TimedButton : ComponentEntry(
        name = "TimedButton", category = ComponentCategory.Actions,
        description = "Button with cooldown timer for rate-limited actions"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.actions.TimedButtonShowcase() } }

    data object ButtonGroup : ComponentEntry(
        name = "ButtonGroup", category = ComponentCategory.Actions,
        description = "Grouped buttons with horizontal and vertical layouts"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.actions.ButtonGroupShowcase() } }

    data object ButtonDock : ComponentEntry(
        name = "ButtonDock", category = ComponentCategory.Actions,
        description = "Sticky bottom button dock for primary and secondary actions"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.actions.ButtonDockShowcase() } }

    data object Tab : ComponentEntry(
        name = "Tab", category = ComponentCategory.Actions,
        description = "Tab components: primary, segmented, scrollable, vertical, with badges"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.actions.TabShowcase() } }

    // ── Inputs ───────────────────────────────────────────
    data object TextField : ComponentEntry(
        name = "TextField", category = ComponentCategory.Inputs,
        description = "Input fields: filled, outlined, ghost, email, password, search variants"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.TextFieldShowcase() } }

    data object Checkbox : ComponentEntry(
        name = "Checkbox", category = ComponentCategory.Inputs,
        description = "Checkboxes with indeterminate state and variants"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.CheckboxShowcase() } }

    data object Switch : ComponentEntry(
        name = "Switch", category = ComponentCategory.Inputs,
        description = "Toggle switches with labels, icons, and sizes"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.SwitchShowcase() } }

    data object Slider : ComponentEntry(
        name = "Slider", category = ComponentCategory.Inputs,
        description = "Sliders for continuous value selection"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.SliderShowcase() } }

    data object RadioButton : ComponentEntry(
        name = "RadioButton", category = ComponentCategory.Inputs,
        description = "Radio buttons for mutually exclusive single selection"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.RadioButtonShowcase() } }

    data object StarRating : ComponentEntry(
        name = "StarRating", category = ComponentCategory.Inputs,
        description = "Star ratings with interactive and read-only modes"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.StarRatingShowcase() } }

    data object TextArea : ComponentEntry(
        name = "TextArea", category = ComponentCategory.Inputs,
        description = "Multi-line text input with auto-resize and character count"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.TextAreaShowcase() } }

    data object ToggleButtonGroup : ComponentEntry(
        name = "ToggleButtonGroup", category = ComponentCategory.Inputs,
        description = "Toggle button groups for multi and single selection"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.ToggleButtonGroupShowcase() } }

    data object PinCode : ComponentEntry(
        name = "PinCode", category = ComponentCategory.Inputs,
        description = "PIN code input with digit boxes and auto-submit"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.PinCodeShowcase() } }

    data object QuantityStepper : ComponentEntry(
        name = "QuantityStepper", category = ComponentCategory.Inputs,
        description = "Quantity, wide, and time steppers with increment/decrement"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.QuantityStepperShowcase() } }

    data object RangeSlider : ComponentEntry(
        name = "RangeSlider", category = ComponentCategory.Inputs,
        description = "Dual-handle range slider for min/max selection"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.RangeSliderShowcase() } }

    data object SearchBar : ComponentEntry(
        name = "SearchBar", category = ComponentCategory.Inputs,
        description = "Search bars with suggestions, voice, and clear"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.SearchBarShowcase() } }

    data object Dropdown : ComponentEntry(
        name = "Dropdown", category = ComponentCategory.Inputs,
        description = "Dropdown menus with single and multi-select"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.DropdownShowcase() } }

    data object DatePicker : ComponentEntry(
        name = "DatePicker", category = ComponentCategory.Inputs,
        description = "Date pickers with calendar dropdown and dialog modes"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.DatePickerShowcase() } }

    data object TimePicker : ComponentEntry(
        name = "TimePicker", category = ComponentCategory.Inputs,
        description = "Time pickers with hour/minute selection wheels"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.TimePickerShowcase() } }

    data object ColorPicker : ComponentEntry(
        name = "ColorPicker", category = ComponentCategory.Inputs,
        description = "Color pickers with HSL, RGB, and palette grid modes"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.ColorPickerShowcase() } }

    data object Calendar : ComponentEntry(
        name = "Calendar", category = ComponentCategory.Inputs,
        description = "Calendar pickers for date selection and range picking"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.inputs.CalendarShowcase() } }

    // ── Display ──────────────────────────────────────────
    data object Icon : ComponentEntry(
        name = "Icon", category = ComponentCategory.Display,
        description = "Icons from vectors, painters, URLs, with tint, tone, size, and animation"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.display.IconShowcase() } }

    data object Avatar : ComponentEntry(
        name = "Avatar", category = ComponentCategory.Display,
        description = "Avatars with image, text, icon, status badges, and group display"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.display.AvatarShowcase() } }

    data object Card : ComponentEntry(
        name = "Card", category = ComponentCategory.Display,
        description = "Cards: base surface, content card anatomy, and domain presets (product, stat, task, etc.)"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.display.CardShowcase() } }

    data object Accordion : ComponentEntry(
        name = "Accordion", category = ComponentCategory.Display,
        description = "Expandable accordion panels for show/hide content"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.display.AccordionShowcase() } }

    data object Chart : ComponentEntry(
        name = "Chart", category = ComponentCategory.Display,
        description = "Bar, line, and pie charts powered by Vico"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.display.ChartShowcase() } }

    data object Divider : ComponentEntry(
        name = "Divider", category = ComponentCategory.Display,
        description = "Horizontal and vertical dividers with label support"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.display.DividerShowcase() } }

    data object Image : ComponentEntry(
        name = "Image", category = ComponentCategory.Display,
        description = "Images loaded from URLs, resources, or painters with placeholders"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.display.ImageShowcase() } }

    data object Banner : ComponentEntry(
        name = "Banner", category = ComponentCategory.Display,
        description = "Promotional banners with image, text, and action button"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.display.BannerShowcase() } }

    data object Carousel : ComponentEntry(
        name = "Carousel", category = ComponentCategory.Display,
        description = "Carousels for horizontal item scrolling with indicators"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.display.CarouselShowcase() } }

    data object ListItem : ComponentEntry(
        name = "ListItem", category = ComponentCategory.Display,
        description = "List items with leading, content, and trailing elements"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.display.ListItemShowcase() } }

    data object SectionHeading : ComponentEntry(
        name = "SectionHeading", category = ComponentCategory.Display,
        description = "Section headings with title, subtitle, and action"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.display.SectionHeadingShowcase() } }

    data object Tag : ComponentEntry(
        name = "Tag", category = ComponentCategory.Display,
        description = "Tags and labels with color variants and dismiss"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.display.TagShowcase() } }

    data object Tile : ComponentEntry(
        name = "Tile", category = ComponentCategory.Display,
        description = "Tiles for grid layouts with icon, text, and selection"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.display.TileShowcase() } }

    // ── Feedback ─────────────────────────────────────────
    data object Alert : ComponentEntry(
        name = "Alert", category = ComponentCategory.Feedback,
        description = "Alerts with info/success/warning/error variants, dismiss, and actions"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.feedback.AlertShowcase() } }

    data object Badge : ComponentEntry(
        name = "Badge", category = ComponentCategory.Feedback,
        description = "Badges for notifications, status, and counts"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.feedback.BadgeShowcase() } }

    data object EmptyState : ComponentEntry(
        name = "EmptyState", category = ComponentCategory.Feedback,
        description = "Empty state placeholders with icon, message, and action"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.feedback.EmptyStateShowcase() } }

    data object Indicator : ComponentEntry(
        name = "Indicator", category = ComponentCategory.Feedback,
        description = "Progress indicators: linear, circular, and steps"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.feedback.IndicatorShowcase() } }

    data object Skeleton : ComponentEntry(
        name = "Skeleton", category = ComponentCategory.Feedback,
        description = "Skeleton loading placeholders for content awaiting data"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.feedback.SkeletonShowcase() } }

    data object Snackbar : ComponentEntry(
        name = "Snackbar", category = ComponentCategory.Feedback,
        description = "Snackbars for brief messages with action support"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.feedback.SnackbarShowcase() } }

    data object SystemBanner : ComponentEntry(
        name = "SystemBanner", category = ComponentCategory.Feedback,
        description = "System-level banners for connectivity, updates, and warnings"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.feedback.SystemBannerShowcase() } }

    data object Toast : ComponentEntry(
        name = "Toast", category = ComponentCategory.Feedback,
        description = "Toast notifications for brief, non-intrusive messages"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.feedback.ToastShowcase() } }

    // ── Navigation ───────────────────────────────────────
    data object TopNavBar : ComponentEntry(
        name = "TopNavBar", category = ComponentCategory.Navigation,
        description = "Top navigation bars with title, actions, profile avatar, and scrolling"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.navigation.TopNavBarShowcase() } }

    data object BottomNavBar : ComponentEntry(
        name = "BottomNavBar", category = ComponentCategory.Navigation,
        description = "Bottom navigation bars with tabs and center action FAB"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.navigation.BottomNavBarShowcase() } }

    data object Drawer : ComponentEntry(
        name = "Drawer", category = ComponentCategory.Navigation,
        description = "Navigation drawers with sections, items, and header"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.navigation.DrawerShowcase() } }

    data object Stepper : ComponentEntry(
        name = "Stepper", category = ComponentCategory.Navigation,
        description = "Steppers for multi-step workflows in horizontal and vertical layout"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.navigation.StepperShowcase() } }

    data object TabBar : ComponentEntry(
        name = "TabBar", category = ComponentCategory.Navigation,
        description = "Tab bars with underline, filled, and pill variants"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.navigation.TabBarShowcase() } }

    // ── Overlay ──────────────────────────────────────────
    data object Dialog : ComponentEntry(
        name = "Dialog", category = ComponentCategory.Overlay,
        description = "Dialogs: alert, confirm, destructive with variants, sizes, and content"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.overlay.DialogShowcase() } }

    data object BottomSheet : ComponentEntry(
        name = "BottomSheet", category = ComponentCategory.Overlay,
        description = "Bottom sheets: expandable and fixed with snap points"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.overlay.BottomSheetShowcase() } }

    data object FullScreenModal : ComponentEntry(
        name = "FullScreenModal", category = ComponentCategory.Overlay,
        description = "Full-screen modals with title, navigation, and content"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.overlay.FullScreenModalShowcase() } }

    data object Menu : ComponentEntry(
        name = "Menu", category = ComponentCategory.Overlay,
        description = "Dropdown menus with icons, dividers, and selection"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.overlay.MenuShowcase() } }

    data object Popover : ComponentEntry(
        name = "Popover", category = ComponentCategory.Overlay,
        description = "Popovers for contextual content near trigger element"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.overlay.PopoverShowcase() } }

    data object Tooltip : ComponentEntry(
        name = "Tooltip", category = ComponentCategory.Overlay,
        description = "Tooltips with position variants and auto-dismiss"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.overlay.TooltipShowcase() } }

    // ── Theme ────────────────────────────────────────────
    data object Theme : ComponentEntry(
        name = "Theme", category = ComponentCategory.Display,
        description = "Color palettes, typography scale, and spacing tokens"
    ) { override val showcase: @Composable () -> Unit get() = { com.pixamob.pixacompose.demo.theme.ThemeShowcase() } }

    companion object {
        val all: List<ComponentEntry> = listOf(
            // Actions (11)
            Button, Chip, FAB, IconButton, Link, SegmentedButton, SlidingButton,
            TimedButton, ButtonGroup, ButtonDock, Tab,
            // Inputs (17)
            TextField, Checkbox, Switch, Slider, RadioButton, StarRating,
            TextArea, ToggleButtonGroup, PinCode, QuantityStepper, RangeSlider,
            SearchBar, Dropdown, DatePicker, TimePicker, ColorPicker, Calendar,
            // Display (13)
            Icon, Avatar, Card, Accordion, Chart, Divider, Image, Banner,
            Carousel, ListItem, SectionHeading, Tag, Tile,
            // Feedback (8)
            Alert, Badge, EmptyState, Indicator, Skeleton, Snackbar, SystemBanner, Toast,
            // Navigation (5)
            TopNavBar, BottomNavBar, Drawer, Stepper, TabBar,
            // Overlay (6)
            Dialog, BottomSheet, FullScreenModal, Menu, Popover, Tooltip,
            // Theme (1)
            Theme
        )

        fun byCategory(category: ComponentCategory): List<ComponentEntry> =
            all.filter { it.category == category }

        fun find(name: String): ComponentEntry? =
            all.find { it.name.equals(name, ignoreCase = true) }
    }
}
