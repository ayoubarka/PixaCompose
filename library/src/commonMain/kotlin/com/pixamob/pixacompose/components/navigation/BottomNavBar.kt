package com.pixamob.pixacompose.components.navigation

import com.pixamob.pixacompose.theme.SizeVariant

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import com.pixamob.pixacompose.components.surfaces.PixaCard
import com.pixamob.pixacompose.components.surfaces.BaseCardVariant
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.elevationShadow
import com.pixamob.pixacompose.utils.pixaRipple

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

data class NavItem(
    val title: String,
    val iconSelected: Painter,
    val iconUnselected: Painter,
    val contentDescription: String? = null,
    val badgeCount: Int? = null,
    val enabled: Boolean = true
)

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class TabDisplayStyle {
    IconOnly,
    TextOnly,
    IconWithText
}

enum class NavIconStyle {
    BoldLine,
    Custom
}

/** Arrangement of icon and label within a single [TabDisplayStyle.IconWithText] item. */
enum class NavOrientation {
    /** Icon and label sit side by side (icon leading the text). */
    Horizontal,
    /** Icon stacked above the label — the standard bottom-navigation look. */
    Vertical
}

/**
 * Selected-state treatment for a bottom-nav item — the accent that marks the
 * current tab. [None] is a pure icon/label colour change; the others reuse
 * shared shape tokens ([AppTheme]'s pill, [CircleShape], rounded lines, a
 * droplet) rather than introducing a new visual system.
 *
 * The ready-made variants pick a sensible default (see [PixaColorNavBar],
 * [PixaPillNavBar], [PixaIndicatorNavBar]), but [PixaBottomNavBar] accepts an
 * explicit [NavIndicatorStyle] so any variant can be overridden.
 */
enum class NavIndicatorStyle {
    /** Colour-only — the selected icon/label just change colour, no accent shape. */
    None,
    /** Short rounded line beneath the selected item's content. */
    Underline,
    /** Rounded accent line pinned to the bar's top edge above the selected item. */
    TopLine,
    /** Small dot accent above the selected item's content. */
    Dot,
    /** Teardrop / water-drop accent above the selected item's content. */
    WaterDrop,
    /** Rounded pill/background wrapping the selected item's icon (and label). */
    Pill
}

/**
 * Motion intensity for selection transitions and the center action button.
 * Each maps to a shared [AnimationUtils] preset so nav motion stays consistent
 * with the rest of the library. Variant defaults can always be overridden via
 * [PixaBottomNavBar]'s `animationStyle` / `centerAnimationStyle`.
 */
enum class NavAnimationStyle {
    /** Snap — no animated transition. */
    None,
    /** Gentle, non-bouncy settle ([AnimationUtils.selectionSpring]). The default. */
    Subtle,
    /** Springy pop with a little overshoot ([AnimationUtils.standardSpring]). */
    Bouncy,
    /** Slow, low-stiffness glide ([AnimationUtils.slowSpring]). */
    Smooth
}

// ═══════════════════════════════════════════════════════════════════════════════
// THEME - Size mappings and styling configurations
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Internal size configuration for bottom navigation bar
 */
private data class NavBarSizeConfig(
    val height: Dp,
    val iconSize: Dp,
    val buttonSize: SizeVariant,
    val horizontalPadding: Dp,
    val verticalPadding: Dp
)

/**
 * Maps size variant to concrete dimensions. Heights read from
 * [HierarchicalSize.BottomNav]; icon sizes from [HierarchicalSize.Icon].
 */
private fun SizeVariant.toNavBarSizeConfig(): NavBarSizeConfig = when (this) {
    SizeVariant.Small -> NavBarSizeConfig(
        height = HierarchicalSize.BottomNav.Compact,
        iconSize = HierarchicalSize.Icon.Small,
        buttonSize = SizeVariant.Medium,
        horizontalPadding = HierarchicalSize.Spacing.Small,
        verticalPadding = HierarchicalSize.Spacing.Compact
    )
    SizeVariant.Medium -> NavBarSizeConfig(
        height = HierarchicalSize.BottomNav.Small,
        iconSize = HierarchicalSize.Icon.Medium,
        buttonSize = SizeVariant.Large,
        horizontalPadding = HierarchicalSize.Spacing.Medium,
        verticalPadding = HierarchicalSize.Spacing.Small
    )
    SizeVariant.Large -> NavBarSizeConfig(
        height = HierarchicalSize.BottomNav.Medium,
        iconSize = HierarchicalSize.Icon.Large,
        buttonSize = SizeVariant.Large,
        horizontalPadding = HierarchicalSize.Spacing.Medium,
        verticalPadding = HierarchicalSize.Spacing.Medium
    )
    SizeVariant.Huge -> NavBarSizeConfig(
        height = HierarchicalSize.BottomNav.Large,
        iconSize = HierarchicalSize.Icon.Large,
        buttonSize = SizeVariant.Huge,
        horizontalPadding = HierarchicalSize.Spacing.Large,
        verticalPadding = HierarchicalSize.Spacing.Medium
    )
    SizeVariant.Massive -> NavBarSizeConfig(
        height = HierarchicalSize.BottomNav.Huge,
        iconSize = HierarchicalSize.Icon.Large,
        buttonSize = SizeVariant.Huge,
        horizontalPadding = HierarchicalSize.Spacing.Large,
        verticalPadding = HierarchicalSize.Spacing.Medium
    )
    else -> NavBarSizeConfig(
        height = HierarchicalSize.BottomNav.Small,
        iconSize = HierarchicalSize.Icon.Medium,
        buttonSize = SizeVariant.Large,
        horizontalPadding = HierarchicalSize.Spacing.Medium,
        verticalPadding = HierarchicalSize.Spacing.Small
    )
}

/** Resolved animation specs for one [NavAnimationStyle]. */
@Immutable
private data class NavAnimationSpecs(
    val float: AnimationSpec<Float>,
    val color: AnimationSpec<Color>
)

/** Maps a [NavAnimationStyle] onto shared [AnimationUtils] presets. */
private fun NavAnimationStyle.toSpecs(): NavAnimationSpecs = when (this) {
    NavAnimationStyle.None -> NavAnimationSpecs(snap(), snap())
    NavAnimationStyle.Subtle -> NavAnimationSpecs(AnimationUtils.selectionSpring, AnimationUtils.colorSpring)
    NavAnimationStyle.Bouncy -> NavAnimationSpecs(AnimationUtils.standardSpring(), AnimationUtils.standardSpring())
    NavAnimationStyle.Smooth -> NavAnimationSpecs(AnimationUtils.slowSpring, AnimationUtils.colorSpring)
}

/**
 * Smooth water-drop / teardrop: a point at the top curving into a full round
 * bulb at the bottom (no hard corners). Sized by its bounding box, so the caller
 * controls the drop size via `Modifier.size(...)`.
 */
private val WaterDropShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val r = w / 2f
    moveTo(w / 2f, 0f)                                    // apex (top point)
    quadraticBezierTo(w, h * 0.5f, w, h - r)             // right flank into the bulb
    arcTo(                                                 // round bottom bulb
        rect = Rect(left = 0f, top = h - w, right = w, bottom = h),
        startAngleDegrees = 0f,
        sweepAngleDegrees = 180f,
        forceMoveTo = false
    )
    quadraticBezierTo(0f, h * 0.5f, w / 2f, 0f)          // left flank back to apex
    close()
}

/** Default accent size per [NavIndicatorStyle] when the caller passes no explicit size. */
private fun NavIndicatorStyle.defaultAccentSize(): Dp = when (this) {
    NavIndicatorStyle.Dot -> HierarchicalSize.Badge.Compact
    NavIndicatorStyle.WaterDrop -> HierarchicalSize.Icon.Medium
    NavIndicatorStyle.TopLine -> HierarchicalSize.Container.Small
    NavIndicatorStyle.Underline -> HierarchicalSize.Icon.Medium
    else -> HierarchicalSize.Badge.Compact
}

// ═══════════════════════════════════════════════════════════════════════════════
// BASE - Internal composables
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Internal navigation item — icon/label plus the selected-state accent driven by
 * [indicator]. Content is centered on both axes; the accent is layered around it
 * WITHOUT shifting the content: [NavIndicatorStyle.TopLine], [NavIndicatorStyle.Dot]
 * and [NavIndicatorStyle.WaterDrop] pin to the top edge, [NavIndicatorStyle.Underline]
 * sits just below, and [NavIndicatorStyle.Pill] wraps the content. [indicatorSize]
 * overrides the accent's default size.
 */
@Composable
private fun AnimatedNavItem(
    item: NavItem,
    isSelected: Boolean,
    iconSize: Dp,
    tabDisplayStyle: TabDisplayStyle,
    orientation: NavOrientation,
    alwaysShowLabel: Boolean,
    indicator: NavIndicatorStyle,
    indicatorSize: Dp?,
    specs: NavAnimationSpecs,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconPainter = if (isSelected) item.iconSelected else item.iconUnselected

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            AppTheme.colors.brandContentDefault
        } else {
            AppTheme.colors.baseContentBody.copy(alpha = 0.5f)
        },
        animationSpec = specs.color,
        label = "navItemColor"
    )

    // Subtle scale feedback on the whole item.
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.0f else 0.95f,
        animationSpec = specs.float,
        label = "navItemScale"
    )

    // Selected icon gets a touch more emphasis.
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = specs.float,
        label = "navIconScale"
    )

    // Label is visible when selected, or always when alwaysShowLabel is set.
    val labelAlpha by animateFloatAsState(
        targetValue = if (isSelected || alwaysShowLabel) 1f else 0f,
        animationSpec = specs.float,
        label = "navLabelAlpha"
    )

    // Selection accent (and pill background) fades in for the selected item only.
    val indicatorAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = specs.float,
        label = "navIndicatorAlpha"
    )

    val accentColor = AppTheme.colors.brandContentDefault.copy(alpha = indicatorAlpha)

    val icon: @Composable () -> Unit = {
        PixaIcon(
            painter = iconPainter,
            contentDescription = null,
            modifier = Modifier.size(iconSize).scale(iconScale),
            tint = contentColor
        )
    }
    val label: @Composable () -> Unit = {
        BasicText(
            text = item.title,
            style = AppTheme.typography.labelMedium.copy(
                color = contentColor.copy(alpha = labelAlpha)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    // Icon + label only — accents are drawn as overlays around this centered
    // stack (below) so they never shift the icon/label.
    val contentStack: @Composable () -> Unit = {
        when (tabDisplayStyle) {
            TabDisplayStyle.IconOnly -> icon()
            TabDisplayStyle.TextOnly -> label()
            TabDisplayStyle.IconWithText -> {
                if (orientation == NavOrientation.Horizontal) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        icon()
                        Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Compact))
                        label()
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        icon()
                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Nano))
                        label()
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = pixaRipple(bounded = true),
                enabled = item.enabled,
                role = Role.Tab,
                onClick = onClick
            )
            .semantics {
                role = Role.Tab
                this.contentDescription = item.contentDescription ?: item.title
            },
        contentAlignment = Alignment.Center
    ) {
        when (indicator) {
            // Top-mounted accents: the accent reserves space at the top edge and
            // the icon/label center in the space beneath it — so the accent is
            // "on top", never overlapping or shifting the content.
            NavIndicatorStyle.TopLine,
            NavIndicatorStyle.Dot,
            NavIndicatorStyle.WaterDrop -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.padding(
                            top = if (indicator == NavIndicatorStyle.TopLine) HierarchicalSize.Spacing.None else HierarchicalSize.Spacing.Small
                        )
                    ) {
                        NavItemAccent(indicator, accentColor, indicatorSize)
                    }
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        contentStack()
                    }
                }
            }

            // Pill/background wrapping the centered content.
            NavIndicatorStyle.Pill -> {
                Box(
                    modifier = Modifier
                        .clip(AppTheme.shapes.pill)
                        .background(AppTheme.colors.baseSurfaceSubtle.copy(alpha = indicatorAlpha))
                        .padding(
                            horizontal = HierarchicalSize.Spacing.Medium,
                            vertical = HierarchicalSize.Spacing.Compact
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    contentStack()
                }
            }

            // Underline sits just below the centered content.
            NavIndicatorStyle.Underline -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    contentStack()
                    Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Compact))
                    NavItemAccent(indicator, accentColor, indicatorSize)
                }
            }

            // Colour-only: just the centered content.
            NavIndicatorStyle.None -> contentStack()
        }
    }
}

/**
 * Draws one selected-state accent shape. [size] overrides the per-style default
 * ([NavIndicatorStyle.defaultAccentSize]); Pill/None render nothing here.
 * - Dot → circle of diameter [size]
 * - WaterDrop → smooth [WaterDropShape], height [size]
 * - Underline / TopLine → rounded bar of width [size]
 */
@Composable
private fun NavItemAccent(
    style: NavIndicatorStyle,
    color: Color,
    size: Dp?
) {
    val accent = size ?: style.defaultAccentSize()
    when (style) {
        NavIndicatorStyle.Underline, NavIndicatorStyle.TopLine -> Box(
            modifier = Modifier
                .width(accent)
                .height(HierarchicalSize.Radius.Compact)
                .clip(AppTheme.shapes.pill)
                .background(color)
        )
        NavIndicatorStyle.Dot -> Box(
            modifier = Modifier
                .size(accent)
                .clip(CircleShape)
                .background(color)
        )
        NavIndicatorStyle.WaterDrop -> Box(
            modifier = Modifier
                // Slightly taller than wide for a natural teardrop.
                .size(width = accent * 0.82f, height = accent)
                .clip(WaterDropShape)
                .background(color)
        )
        else -> Unit
    }
}

/**
 * Internal center action button (FAB). Reimplemented on [pixaRipple] rather than
 * [com.pixamob.pixacompose.components.actions.PixaButton] so the bar can toggle
 * the ripple, drive the press animation from [NavAnimationStyle], and let the
 * button overflow above the bar without the card clipping it. Colours mirror the
 * Filled button (brand surface + brand content).
 */
@Composable
private fun CenterActionButton(
    centerIcon: Painter,
    centerContentDescription: String?,
    diameter: Dp,
    iconSize: Dp,
    enabled: Boolean,
    rippleEnabled: Boolean,
    specs: NavAnimationSpecs,
    onCenterAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.92f else 1f,
        animationSpec = specs.float,
        label = "centerFabScale"
    )

    val background = if (enabled) AppTheme.colors.brandSurfaceDefault else AppTheme.colors.baseSurfaceDisabled
    val iconTint = if (enabled) AppTheme.colors.brandContentDefault else AppTheme.colors.baseContentDisabled

    Box(
        modifier = modifier
            .scale(pressScale)
            .size(diameter)
            .elevationShadow(ComponentElevation.Medium, CircleShape, enabled = enabled)
            .clip(CircleShape)
            .background(background)
            .clickable(
                interactionSource = interactionSource,
                indication = if (rippleEnabled) pixaRipple(bounded = false) else null,
                enabled = enabled,
                role = Role.Button,
                onClick = onCenterAction
            )
            .semantics {
                role = Role.Button
                centerContentDescription?.let { contentDescription = it }
            },
        contentAlignment = Alignment.Center
    ) {
        PixaIcon(
            painter = centerIcon,
            contentDescription = centerContentDescription,
            modifier = Modifier.size(iconSize),
            tint = iconTint
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PUBLIC - Main component
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Bottom navigation bar with 2-5 items and an optional, positionable center FAB.
 * This is the single source of truth — the ready-made variants ([PixaColorNavBar],
 * [PixaPillNavBar], [PixaIndicatorNavBar], [PixaCenterNavBar]) are thin wrappers
 * that only preset defaults here.
 *
 * ### Anatomy
 * Nav items (icon + optional label + selected accent) inside a card, with an
 * optional center FAB overlaid on top (so it can be raised above the bar).
 *
 * ### Variants
 * [TabDisplayStyle]: IconOnly, TextOnly, IconWithText.
 * [NavOrientation]: Vertical (icon over label, standard), Horizontal (icon beside label).
 * [NavIndicatorStyle]: None (colour-only), Underline, TopLine, Dot, WaterDrop, Pill.
 * [NavAnimationStyle]: None, Subtle, Bouncy, Smooth.
 *
 * ### States
 * Selected (animated colour/scale + accent), Disabled (per-item and per-center).
 *
 * ### Sizing
 * [SizeVariant] Small/Medium/Large drive height ([HierarchicalSize.BottomNav]),
 * icon size and padding; [height] overrides the bar height explicitly.
 *
 * ### Center button
 * [withCenterAction] overlays a FAB in the middle gap. [centerRaiseFraction]
 * lifts it above the bar: 0 = vertically centered, 1 = raised by a full button
 * diameter (part of it sits outside the bar). [centerRippleEnabled],
 * [centerEnabled] and [centerAnimationStyle] tune its behavior.
 *
 * ### Dynamic behavior
 * - 2-4 items + center FAB: FAB in the middle, tabs on both sides.
 * - 5 items or no FAB: evenly distributed width.
 * - >5 items: horizontal scrolling + auto-scroll to selected.
 *
 * @param items Navigation items (2+)
 * @param selectedIndex 0-based selected index
 * @param onItemSelected Callback receiving item index
 * @param withCenterAction Shows center FAB when true
 * @param centerIcon Painter for center action (required when [withCenterAction])
 * @param centerContentDescription Accessibility label for center button
 * @param onCenterAction Center button press callback
 * @param centerEnabled Center button enabled state
 * @param centerRaiseFraction Fraction (0..1) of the button's own size to raise it above the centered position
 * @param centerRippleEnabled Toggle the center button ripple
 * @param centerAnimationStyle Center button motion; null inherits [animationStyle]
 * @param size Height/icon/padding preset
 * @param height Explicit bar height override; null derives from [size]
 * @param iconStyle BoldLine vs Custom icon painters
 * @param orientation Icon/label arrangement within [TabDisplayStyle.IconWithText] items
 * @param showBackground Wraps in [PixaCard] when true
 * @param cardVariant Card background variant
 * @param tabDisplayStyle Icon/Text/Icon+Text per tab
 * @param alwaysShowLabel Keeps labels visible for unselected tabs too
 * @param indicator Selected-state accent style (default None = colour-only)
 * @param indicatorSize Explicit accent size in dp (dot diameter / drop height / line width); null uses a per-style default
 * @param animationStyle Selection motion preset for items (and center button unless overridden)
 * @param enableScrolling Horizontal scroll for >5 items
 * @param enableAutoScroll Auto-scroll to selected item
 */
@Composable
fun PixaBottomNavBar(
    items: List<NavItem>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    withCenterAction: Boolean = false,
    centerIcon: Painter? = null,
    centerContentDescription: String? = null,
    centerEnabled: Boolean = true,
    centerRaiseFraction: Float = 0f,
    centerRippleEnabled: Boolean = true,
    centerAnimationStyle: NavAnimationStyle? = null,
    size: SizeVariant = SizeVariant.Medium,
    height: Dp? = null,
    iconStyle: NavIconStyle = NavIconStyle.BoldLine,
    orientation: NavOrientation = NavOrientation.Vertical,
    showBackground: Boolean = true,
    cardVariant: BaseCardVariant = BaseCardVariant.Elevated,
    tabDisplayStyle: TabDisplayStyle = TabDisplayStyle.IconWithText,
    alwaysShowLabel: Boolean = false,
    indicator: NavIndicatorStyle = NavIndicatorStyle.None,
    indicatorSize: Dp? = null,
    animationStyle: NavAnimationStyle = NavAnimationStyle.Subtle,
    enableScrolling: Boolean = true,
    enableAutoScroll: Boolean = true,
    onItemSelected: (Int) -> Unit,
    onCenterAction: () -> Unit = {},
) {
    // Validation with helpful error messages
    require(items.size >= 2) {
        "BottomNavBar requires at least 2 items for proper navigation. Provided: ${items.size}"
    }

    require(selectedIndex in items.indices) {
        "selectedIndex $selectedIndex is out of bounds for ${items.size} items (valid range: 0-${items.lastIndex})"
    }

    if (withCenterAction) {
        require(centerIcon != null) {
            "centerIcon is required when withCenterAction=true. Provide a valid Painter or set withCenterAction=false"
        }
    }

    val sizeConfig = size.toNavBarSizeConfig()
    val barHeight = height ?: sizeConfig.height
    val itemSpecs = animationStyle.toSpecs()
    val centerSpecs = (centerAnimationStyle ?: animationStyle).toSpecs()

    val shouldScroll = enableScrolling && items.size > 5
    val scrollState = rememberScrollState()

    // Auto-scroll to selected item with smooth animation
    LaunchedEffect(selectedIndex) {
        if (shouldScroll && enableAutoScroll && items.isNotEmpty()) {
            // Calculate approximate scroll position based on item width
            val itemWidth = 80f
            val targetScroll = (selectedIndex * itemWidth).coerceAtLeast(0f).toInt()
            scrollState.animateScrollTo(targetScroll)
        }
    }

    // Center action is shown only when withCenterAction=true AND items.size <= 4
    val showCenterActionButton = withCenterAction && centerIcon != null && items.size <= 4

    // Items list with a middle gap reserved for the (overlaid) center FAB.
    val displayItems = if (showCenterActionButton) {
        val middleIndex = (items.size + 1) / 2
        items.take(middleIndex) + listOf(null) + items.drop(middleIndex)
    } else {
        items.map { it as NavItem? }
    }

    // Center FAB geometry: raise it above the bar by a fraction of its own size.
    val fabDiameter = HierarchicalSize.Container.forVariant(sizeConfig.buttonSize)
    val fabIconSize = HierarchicalSize.Icon.forVariant(sizeConfig.buttonSize)
    val raiseAmount = fabDiameter * centerRaiseFraction.coerceIn(0f, 1f)

    // Inner items row (fills the bar surface, evenly distributes the tabs). The
    // only padding is a small INNER gutter so items don't touch the rounded
    // corners — the bar surface itself always fills the caller's width.
    val itemsRow: @Composable () -> Unit = {
        val rowModifier = Modifier
            .fillMaxSize()
            .padding(horizontal = sizeConfig.horizontalPadding)

        if (shouldScroll) {
            Row(
                modifier = rowModifier.horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavBarItems(displayItems, selectedIndex, onItemSelected, sizeConfig, tabDisplayStyle, orientation, alwaysShowLabel, indicator, indicatorSize, itemSpecs, shouldScroll)
            }
        } else {
            Row(
                modifier = rowModifier,
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavBarItems(displayItems, selectedIndex, onItemSelected, sizeConfig, tabDisplayStyle, orientation, alwaysShowLabel, indicator, indicatorSize, itemSpecs, shouldScroll)
            }
        }
    }

    // Bar surface (card or plain box), sized to the bar height.
    val barSurface: @Composable (Modifier) -> Unit = { surfaceModifier ->
        if (showBackground) {
            PixaCard(
                modifier = surfaceModifier.fillMaxWidth().height(barHeight),
                variant = cardVariant,
                elevation = when (cardVariant) {
                    BaseCardVariant.Elevated -> ComponentElevation.Medium
                    else -> ComponentElevation.None
                },
                padding = SizeVariant.None
            ) {
                itemsRow()
            }
        } else {
            Box(surfaceModifier.fillMaxWidth().height(barHeight)) {
                itemsRow()
            }
        }
    }

    if (showCenterActionButton) {
        // Overlay path: the FAB is floated on top so it can rise above the bar
        // without the card clipping it. The only outer inset is a top reserve
        // that keeps a raised FAB inside the composable's bounds (0 when unraised).
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = raiseAmount)
        ) {
            barSurface(Modifier.align(Alignment.BottomCenter))

            centerIcon?.let { fabIcon ->
                CenterActionButton(
                    centerIcon = fabIcon,
                    centerContentDescription = centerContentDescription,
                    diameter = fabDiameter,
                    iconSize = fabIconSize,
                    enabled = centerEnabled,
                    rippleEnabled = centerRippleEnabled,
                    specs = centerSpecs,
                    onCenterAction = onCenterAction,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        // Lift from the bottom to the bar's vertical center, then raise further.
                        .offset(y = (fabDiameter - barHeight) / 2f - raiseAmount)
                        .zIndex(1f)
                )
            }
        }
    } else {
        // No center action: the bar surface fills the caller's bounds edge-to-edge,
        // with no forced outer margin (add your own via `modifier` if you want the
        // floating-card look).
        barSurface(modifier)
    }
}

/**
 * Internal composable for rendering navigation items
 */
@Composable
private fun RowScope.NavBarItems(
    items: List<NavItem?>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    sizeConfig: NavBarSizeConfig,
    tabDisplayStyle: TabDisplayStyle,
    orientation: NavOrientation,
    alwaysShowLabel: Boolean,
    indicator: NavIndicatorStyle,
    indicatorSize: Dp?,
    specs: NavAnimationSpecs,
    shouldScroll: Boolean
) {
    var actualIndex = 0
    items.forEach { item ->
        if (item == null) {
            // Gap reserved for the overlaid center FAB.
            if (!shouldScroll) {
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Spacer(modifier = Modifier.width(HierarchicalSize.Container.Huge))
            }
        } else {
            val isSelected = selectedIndex == actualIndex
            val currentIndex = actualIndex

            AnimatedNavItem(
                item = item,
                isSelected = isSelected,
                iconSize = sizeConfig.iconSize,
                tabDisplayStyle = tabDisplayStyle,
                orientation = orientation,
                alwaysShowLabel = alwaysShowLabel,
                indicator = indicator,
                indicatorSize = indicatorSize,
                specs = specs,
                onClick = { onItemSelected(currentIndex) },
                modifier = if (shouldScroll) {
                    Modifier
                        .width(HierarchicalSize.Container.Massive)
                        .fillMaxHeight()
                } else {
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                }
            )

            actualIndex++
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// READY-MADE VARIANTS - Convenience composables on top of PixaBottomNavBar
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Colour-only bottom nav: the selected tab differs only by icon/label colour,
 * with every label always visible (no accent shape). Mirrors the "all labels"
 * reference variant.
 */
@Composable
fun PixaColorNavBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium,
    height: Dp? = null,
    tabDisplayStyle: TabDisplayStyle = TabDisplayStyle.IconWithText,
    animationStyle: NavAnimationStyle = NavAnimationStyle.Subtle,
) {
    PixaBottomNavBar(
        items = items,
        selectedIndex = selectedIndex,
        onItemSelected = onItemSelected,
        modifier = modifier,
        size = size,
        height = height,
        tabDisplayStyle = tabDisplayStyle,
        alwaysShowLabel = true,
        indicator = NavIndicatorStyle.None,
        animationStyle = animationStyle,
    )
}

/**
 * Pill bottom nav: the selected item sits inside a rounded pill/background.
 * Defaults to icon-only tabs (a circle around the selected icon); switch
 * [tabDisplayStyle] to IconWithText + [orientation] Horizontal for a
 * label-inside-pill look.
 */
@Composable
fun PixaPillNavBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium,
    height: Dp? = null,
    tabDisplayStyle: TabDisplayStyle = TabDisplayStyle.IconOnly,
    orientation: NavOrientation = NavOrientation.Horizontal,
    animationStyle: NavAnimationStyle = NavAnimationStyle.Bouncy,
) {
    PixaBottomNavBar(
        items = items,
        selectedIndex = selectedIndex,
        onItemSelected = onItemSelected,
        modifier = modifier,
        size = size,
        height = height,
        tabDisplayStyle = tabDisplayStyle,
        orientation = orientation,
        indicator = NavIndicatorStyle.Pill,
        animationStyle = animationStyle,
    )
}

/**
 * Indicator bottom nav: a top-mounted accent ([NavIndicatorStyle.TopLine] by
 * default, or [NavIndicatorStyle.Dot] / [NavIndicatorStyle.WaterDrop] /
 * [NavIndicatorStyle.Underline]) marks the selected tab, with labels shown.
 * TopLine/Dot/WaterDrop are pinned to the top edge (not nested in the content);
 * [accentSize] sets the accent size in dp.
 */
@Composable
fun PixaIndicatorNavBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    accent: NavIndicatorStyle = NavIndicatorStyle.TopLine,
    accentSize: Dp? = null,
    size: SizeVariant = SizeVariant.Medium,
    height: Dp? = null,
    tabDisplayStyle: TabDisplayStyle = TabDisplayStyle.IconWithText,
    animationStyle: NavAnimationStyle = NavAnimationStyle.Smooth,
) {
    PixaBottomNavBar(
        items = items,
        selectedIndex = selectedIndex,
        onItemSelected = onItemSelected,
        modifier = modifier,
        size = size,
        height = height,
        tabDisplayStyle = tabDisplayStyle,
        alwaysShowLabel = true,
        indicator = accent,
        indicatorSize = accentSize,
        animationStyle = animationStyle,
    )
}

/**
 * Center-action bottom nav: a prominent FAB in the middle gap, raised above the
 * bar by [centerRaiseFraction] (0 = centered, 1 = fully lifted). Tabs use a
 * colour-only selection with labels shown, matching the reference center-button row.
 */
@Composable
fun PixaCenterNavBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    centerIcon: Painter,
    onCenterAction: () -> Unit,
    modifier: Modifier = Modifier,
    centerContentDescription: String? = null,
    centerEnabled: Boolean = true,
    centerRaiseFraction: Float = 0.5f,
    centerRippleEnabled: Boolean = true,
    size: SizeVariant = SizeVariant.Medium,
    height: Dp? = null,
    indicator: NavIndicatorStyle = NavIndicatorStyle.None,
    animationStyle: NavAnimationStyle = NavAnimationStyle.Bouncy,
) {
    PixaBottomNavBar(
        items = items,
        selectedIndex = selectedIndex,
        onItemSelected = onItemSelected,
        modifier = modifier,
        withCenterAction = true,
        centerIcon = centerIcon,
        onCenterAction = onCenterAction,
        centerContentDescription = centerContentDescription,
        centerEnabled = centerEnabled,
        centerRaiseFraction = centerRaiseFraction,
        centerRippleEnabled = centerRippleEnabled,
        size = size,
        height = height,
        tabDisplayStyle = TabDisplayStyle.IconWithText,
        alwaysShowLabel = true,
        indicator = indicator,
        animationStyle = animationStyle,
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// CONVENIENCE - Helper extensions and utilities
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Helper extension to validate if item count is optimal for bottom navigation
 * Logs a warning if >5 items (scrolling required) or <2 items (insufficient for navigation)
 */
fun List<NavItem>.validateForBottomNav(): Boolean {
    return when {
        size < 2 -> {
            println("Warning: BottomNavBar works best with 2-5 items. Found $size items.")
            false
        }
        size > 5 -> {
            println("Info: BottomNavBar has >5 items ($size). Horizontal scrolling will be enabled.")
            true
        }
        else -> true
    }
}

/**
 * Creates a minimal NavItem with just title and icons
 */
fun createNavItem(
    title: String,
    iconSelected: Painter,
    iconUnselected: Painter
): NavItem = NavItem(
    title = title,
    iconSelected = iconSelected,
    iconUnselected = iconUnselected,
    contentDescription = null,
    badgeCount = null,
    enabled = true
)
