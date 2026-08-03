# PixaCompose — Scan Sheet

## Theme
- Entry: `PixaTheme(useDarkTheme, colorOverrides, colorScales, fontFamily)`
- Tokens: `AppTheme.colors.*` (79 — brand/base/accent/success/error/warning/info, each with `Content*`/`Border*`/`Surface*`), `AppTheme.typography.*` (displayLarge→bodySmall, label/title/headline), `AppTheme.spacing.*` (none→xxlarge), `AppTheme.windowSizeClass` (Compact/Expanded)
- Key files: `theme/PixaTheme.kt`, `theme/Color.kt`, `theme/Typography.kt`, `theme/Dimen.kt`, `theme/CustomShapes.kt`, `theme/ShapeStyle.kt`

## Actions (`components/actions/`)
- PixaButton — CTA; Filled/Tonal/Outlined/Ghost/OnBrand, sizes, shape (Default/Pill), widthPolicy, icons, loading; Button.kt
- PixaButtonDock — overflow-aware action row, stacks on narrow; items, layout; ButtonDock.kt
- PixaButtonGroup — grouped buttons, single/multi selection, Clustered/HorizontalScroll; ButtonGroup.kt
- PixaChip — compact entity/action; Static/Selectable/Dismissible/Input, Filled/Tonal/Outlined/Ghost; Chip.kt
- PixaFAB / PixaExpandableFab — floating action, 3 variants, expandable multi-action; FAB.kt
- PixaIconButton — icon-only circle button; Filled/Outlined/Ghost/Tonal; IconButton.kt
- PixaLink — text hyperlink, underline animation; sizes, visited; Link.kt
- PixaSegmentedButton — mutually exclusive segments; Default/Pill shape, Fixed/Intrinsic width; SegmentedButton.kt
- PixaSlidingButton — drag-to-confirm; Brand/Success/Error, threshold Low/High; SlidingButton.kt
- PixaTab — tab item, icon/text/badge; Primary/Secondary/Segmented/Vertical; Tab.kt
- PixaTimedButton — countdown auto-disable; durations 15s–75s; TimedButton.kt

## Display (`components/display/`)
- PixaAccordion / PixaAccordionGroup — expandable panel; Default/Outlined/Filled, Single/Multiple expansion; Accordion.kt
- PixaAvatar / PixaAvatarGroup — user image + initials fallback + status dot; Circle/Rounded/Square, group stacking; Avatar.kt
- PixaBanner / PixaBannerCarousel — hero banner; image/color/multi-destination, Tall/Short; Banner.kt
- PixaCarousel — paged content carousel; dot/dash indicators, autoPlay, Scroll/Fade; Carousel.kt
- PixaChart — Vico charts; Line/Column/Candlestick, legend, grid, loading; Chart.kt
- PixaContentCard + 10 archetype cards (Product/Article/Booking/Task/Pin/App/Contact/Stat/Action/Select/Message) — image, title, description, metadata, onClick; ContentCard.kt
- PixaDivider — H/V separator; Cell/Module; Divider.kt
- PixaIcon — monochrome icon; vector/resource/URL, 10 tone presets; Icon.kt
- PixaImage — multi-source image (Url/Resource/Vector/SvgPath); aspect ratio presets, shimmer; Image.kt
- PixaListItem — row with leading + title + description + trailing; FullWidth/Selection, density; ListItem.kt
- PixaSectionHeading — section header + subheading + trailing action; SectionHeading.kt
- PixaTag — compact label; Primary/Secondary/Tertiary, 7 color families; Tag.kt
- PixaTile — selectable/actionable tile; artwork + trailing (Check/Radio/Switch/Badge); Tile.kt

## Feedback (`components/feedback/`)
- PixaAlert — feedback surface; Info/Success/Warning/Error, Filled/Outlined/Subtle; Alert.kt
- PixaNotificationBadge / PixaHintBadge — dot/count badge; Accent/Success/Warning/Error/OnBrand; Badge.kt
- PixaEmptyState — illustration + headline + 2 CTAs; Empty/Network/Client/Server/Permission/Custom; EmptyState.kt
- PixaCircularIndicator / PixaLinearIndicator / PixaProgressPill / PixaPagerIndicator — progress spinners/bar/pill/dots; Indicator.kt
- Skeleton family (Text/Circle/Image/Button/Card/ListItem/List/Grid) — shimmer placeholders; Skeleton.kt
- PixaSnackbar — ephemeral bar, queue, swipe-dismiss; PixaSnackbarHostState; Snackbar.kt
- PixaSystemBanner — urgent system banner; Accent/Warning/Negative/Positive, Single/Dual action; SystemBanner.kt
- PixaToast — stacked notifications; Info/Success/Warning/Error, Top/Bottom, PixaToastManager; Toast.kt

## Inputs (`components/inputs/`)
- PixaCheckbox / PixaCheckboxTree — 3-state (Unchecked/Checked/Indeterminate), Filled/Outlined/Ghost, hierarchical; Checkbox.kt
- PixaRadioButton — single-select; Filled/Outlined/Ghost, label Start/End; RadioButton.kt
- PixaSwitch — binary toggle; Filled/Outlined/Ghost, loading, description; Switch.kt
- PixaTextField — single-line input; icons, tags, loading, status; TextField.kt
- PixaTextArea — multi-line input; minLines, maxLength; TextArea.kt
- PixaSearchBar — search + suggestions popup; Filled/Outlined/Elevated; SearchBar.kt
- PixaDropdown — select; Outlined/Filled/Ghost, adaptive sheet/popover; Dropdown.kt
- PixaSlider — single-value slider; labels, bubble, ticks, gradient; Slider.kt
- PixaRangeSlider — dual-knob range; RangeSlider.kt
- PixaStarRating — Interactive/Descriptive; Large/Medium/Small; StarRating.kt
- PixaQuantityStepper — increment/decrement; Narrow/Wide, min/max/step; QuantityStepper.kt
- PixaToggleButtonGroup — multi-select toggle; Minimal/ListView/Gallery; ToggleButtonGroup.kt
- PixaPinCode — PIN/OTP digit slots; Masked/Unmasked, loading, isError; PinCode.kt
- PixaCalendar / PixaHeatmapCalendar — date selection; Single/Multiple/Range; Calendar.kt
- PixaDatePicker — wheel/calendar/heatmap/month/day/schedule variants; DatePicker.kt
- PixaTimePicker — time/duration; Wheel/Stepper/TimeOfDayPicker, Hour12/Hour24; TimePicker.kt
- PixaColorPicker — sat field + hue + alpha + hex/RGB + recent; ColorPicker.kt

## Navigation (`components/navigation/`)
- PixaTopNavBar — top app bar; Start/Center title, Fixed/Floating, navigationIcon + actions (actionShape/actionVariant); TopNavBar.kt
- PixaBottomNavBar — bottom base; IconOnly/TextOnly/IconWithText, orientation, indicator (None/Underline/TopLine/Dot/WaterDrop/Pill), animationStyle, raised center FAB; variants: PixaColorNavBar/PixaPillNavBar/PixaIndicatorNavBar/PixaCenterNavBar; BottomNavBar.kt
- PixaTabBar — horizontal tabs; Underline/Filled/Pill, FixedWidth/IntrinsicWidth; TabBar.kt
- PixaDrawer — slide-in nav; sections + items, Start/End; Drawer.kt
- PixaStepper — step progress; Dot/Number/Icon/Bar/CheckmarkNumber, V/H; Stepper.kt

## Overlays (`components/overlay/`)
- PixaDialog + Alert/Confirm/Destructive — modal; Default/Info/Success/Warning/Error, Modal/NonModal; Dialog.kt
- PixaMenu / PixaMenuContent — dropdown; items (chevron/grabber/switch/checkbox/search/divider); Menu.kt
- PixaPopover — rich popover; artwork + title + description + actions, Modal/NonModal; Popover.kt
- PixaTooltip / PixaTooltipBox — contextual; Unprompted/Prompted, Top/Bottom/Start/End; Tooltip.kt

## Surfaces (`components/surfaces/`)
- PixaSurfaceCard / PixaCard — content container; Elevated/Outlined/Filled/Tonal/Ghost, Isolated/Feed context; Card.kt
- PixaSheet — bottom sheet; Modal/NonModal, Expandable/Fixed; Sheet.kt
- PixaFullScreenModal — full-screen modal; StackedSheet/Immersive; FullScreenModal.kt
- PixaMediaContainer — media framing; ratio presets, matte, scrim, overlay, Image/Custom source; MediaContainer.kt
- PixaScaffold / PixaAdaptiveScaffold / PixaSimpleScaffold — screen-level layout container; top/bottom bar, rail, side pane, floating content slots; Scaffold.kt

## Utils
- `utils/AnimationUtils.kt` — MotionDuration, easing curves, standardSpring, fadeInWithScale
- `utils/ElevationUtils.kt` — PixaElevation (none/low/medium/high)
- `utils/ScreenUtil.kt` — window size / safe-area
- `utils/InteractionUtils.kt` — rippleClickable / press interactions
- `utils/ColorUtils.kt` — color blending / mapping helpers

## Petrilo picks (most-used)
- PixaTopNavBar, PixaBottomNavBar — shell/nav
- PixaButton + PixaButtonDock — CTAs (FullBleed)
- PixaTextField, PixaTextArea, PixaSearchBar — form entry
- PixaSwitch, PixaCheckbox, PixaRadioButton — settings/filters
- PixaDropdown, PixaSlider — selection
- PixaListItem, PixaTile, PixaCard / PixaContentCard — content rows/cards
- PixaSheet, PixaDialog — modals
- PixaToast, PixaSnackbar — transient feedback
- PixaSkeleton, PixaCircularIndicator — loading states
- PixaEmptyState — empty/error screens
- PixaIcon, PixaImage — media

## Read order
1. Read this file first for full map.
2. Read `theme/PixaTheme.kt` — required before using any component.
3. Read only the specific component `.kt` files you use — KDoc is self-contained and exhaustive.
