package com.pixamob.pixacompose.components.navigation
import com.pixamob.pixacompose.theme.SizeVariant

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.ButtonShape
import com.pixamob.pixacompose.components.actions.ButtonColors
import com.pixamob.pixacompose.components.actions.ButtonStateColors
import com.pixamob.pixacompose.components.display.PixaAvatar
import com.pixamob.pixacompose.components.feedback.PixaNotificationBadge
import com.pixamob.pixacompose.components.feedback.BadgeVariant
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.elevationShadow

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

@Stable
data class TopNavAction(
    val icon: Painter,
    val description: String? = null,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
    val badge: Int? = null,
    val tint: Color? = null
)

/**
 * Title alignment options
 */
enum class TopNavTitleAlignment {
    /** Title aligned to start (left) */
    Start,
    /** Title centered */
    Center
}

/**
 * [Fixed] is opaque, docked to the screen top, with a title.
 * [Floating] is transparent, titleless, designed for overlaying maps/images,
 * with protective backgrounds on leading/trailing icons for visibility.
 */
enum class NavHeaderVariant {
    Fixed,
    Floating
}

// ═══════════════════════════════════════════════════════════════════════════════
// THEME - Size mappings and styling configurations
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Internal size configuration for top navigation bar
 */
@Stable
private data class TopNavSizeConfig(
    val height: Dp,
    val iconSize: Dp,
    val avatarSize: SizeVariant,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val actionSpacing: Dp,
    val titleFontScale: Float
)

/**
 * Maps size variant to concrete dimensions
 */
private fun SizeVariant.toSizeConfig(): TopNavSizeConfig = when (this) {
    SizeVariant.Small -> TopNavSizeConfig(
        height = HierarchicalSize.Container.Medium  ,
        iconSize =  HierarchicalSize.Icon.Small,
        avatarSize = SizeVariant.Small,
        horizontalPadding = HierarchicalSize.Spacing.Small,
        verticalPadding = HierarchicalSize.Spacing.Compact,
        actionSpacing = HierarchicalSize.Spacing.Compact,
        titleFontScale = 0.9f
    )
    SizeVariant.Medium -> TopNavSizeConfig(
        height = HierarchicalSize.Container.Large  ,
        iconSize =  HierarchicalSize.Icon.Medium,
        avatarSize = SizeVariant.Medium,
        horizontalPadding = HierarchicalSize.Spacing.Medium,
        verticalPadding = HierarchicalSize.Spacing.Small,
        actionSpacing = HierarchicalSize.Spacing.Small,
        titleFontScale = 1.0f
    )
    SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> TopNavSizeConfig(
        height = HierarchicalSize.Container.Huge  ,
        iconSize =  HierarchicalSize.Icon.Large,
        avatarSize = SizeVariant.Large,
        horizontalPadding = HierarchicalSize.Spacing.Medium,
        verticalPadding = HierarchicalSize.Spacing.Medium,
        actionSpacing = HierarchicalSize.Spacing.Medium,
        titleFontScale = 1.15f
    )
    else -> TopNavSizeConfig(
        height = HierarchicalSize.Container.Large,
        iconSize = HierarchicalSize.Icon.Medium,
        avatarSize = SizeVariant.Medium,
        horizontalPadding = HierarchicalSize.Spacing.Medium,
        verticalPadding = HierarchicalSize.Spacing.Small,
        actionSpacing = HierarchicalSize.Spacing.Small,
        titleFontScale = 1.0f
    )
}

/** Collapsed height ratio relative to [SizeVariant] height. */
private const val CollapsedHeightRatio = 0.78f
/** Collapsed title scale multiplier. */
private const val CollapsedTitleScaleRatio = 0.85f

/** Leading action start padding matches [HierarchicalSize.Spacing.Small]. */
private val LeadingActionStartPadding = HierarchicalSize.Spacing.Small

/** Drop shadow elevation for both Fixed and Floating variants. */
val NavHeaderElevation = HierarchicalSize.Shadow.Massive

/** Bottom border width. */
private val NavHeaderBorderWidth = HierarchicalSize.Border.Compact

// ═══════════════════════════════════════════════════════════════════════════════
// BASE - Internal composables
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Internal action button using PixaButton for consistent styling.
 * [protectionColor], when non-null, paints a solid chip behind the icon for visibility on complex backgrounds.
 */
@Composable
private fun ActionButton(
    action: TopNavAction,
    iconSize: Dp,
    contentColor: Color,
    protectionColor: Color?,
    mirrorForRtl: Boolean,
    modifier: Modifier = Modifier
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(
        modifier = modifier
            .then(if (mirrorForRtl && isRtl) Modifier.graphicsLayer(scaleX = -1f) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // Use action.tint or contentColor for the icon
        val iconColor = action.tint ?: contentColor
        val chipColor = protectionColor ?: Color.Transparent

        PixaButton(
            onClick = action.onClick,
            leadingIcon = action.icon,
            size = when (iconSize) {
                HierarchicalSize.Icon.Small -> SizeVariant.Small
                HierarchicalSize.Icon.Medium -> SizeVariant.Medium
                HierarchicalSize.Icon.Large -> SizeVariant.Large
                else -> SizeVariant.Medium
            },
            variant = ButtonVariant.Ghost,
            shape = ButtonShape.Circle,
            enabled = action.enabled,
            description = action.description,
            modifier = Modifier.size(iconSize + 20.dp), // Touch target 44dp minimum
            customColors = ButtonStateColors(
                default = ButtonColors(
                    background = chipColor,
                    content = iconColor,
                    border = Color.Transparent
                ),
                disabled = ButtonColors(
                    background = chipColor,
                    content = iconColor.copy(alpha = 0.5f),
                    border = Color.Transparent
                )
            )
        )

        // Badge overlay
        if (action.badge != null && action.badge > 0) {
            PixaNotificationBadge(
                count = action.badge,
                variant = BadgeVariant.Error,
                size = SizeVariant.Small,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

/**
 * Internal title composable with overflow handling and optional subtitle
 */
@Composable
private fun TopNavTitleSection(
    title: String?,
    subtitle: String?,
    alignment: TopNavTitleAlignment,
    fontScale: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = when (alignment) {
            TopNavTitleAlignment.Start -> Alignment.Start
            TopNavTitleAlignment.Center -> Alignment.CenterHorizontally
        }
    ) {
        if (title != null) {
            BasicText(
                text = title,
                style = AppTheme.typography.titleBold.copy(
                    fontSize = AppTheme.typography.titleBold.fontSize * fontScale,
                    color = AppTheme.colors.baseContentTitle,
                    textAlign = when (alignment) {
                        TopNavTitleAlignment.Start -> TextAlign.Start
                        TopNavTitleAlignment.Center -> TextAlign.Center
                    }
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.semantics { heading() }
            )
        }

        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            BasicText(
                text = subtitle,
                style = AppTheme.typography.captionRegular.copy(
                    color = AppTheme.colors.baseContentSubtitle,
                    textAlign = when (alignment) {
                        TopNavTitleAlignment.Start -> TextAlign.Start
                        TopNavTitleAlignment.Center -> TextAlign.Center
                    }
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PUBLIC - Main component
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Top navigation bar with actions, title/subtitle, avatar, and badges.
 *
 * ### Anatomy
 * Status bar spacer → actions row (start actions → title/avatar → end actions) → optional bottom divider.
 *
 * ### Variants
 * [NavHeaderVariant.Fixed]: opaque, carries title, collapsible.
 * [NavHeaderVariant.Floating]: transparent, no title, background-protected icons.
 *
 * ### States
 * Expanded/collapsed (Fixed only, caller-driven), enabled/disabled per action.
 *
 * ### Sizing
 * [SizeVariant] Small (48dp), Medium (56dp), Large (72dp+) — drives height, icon size, font scale.
 *
 * @param variant Fixed (opaque + title) or Floating (transparent, no title)
 * @param collapsed Fixed only: animate between expanded/collapsed
 * @param title Optional title text (ignored when Floating)
 * @param subtitle Optional subtitle below title
 * @param titleComposable Custom title composable (overrides title/subtitle)
 * @param titleAlignment Start or Center; forced Start when collapsed
 * @param startActions Leading action buttons
 * @param endActions Trailing action buttons
 * @param mirrorLeadingIconForRtl Flips first start icon for RTL (default true)
 * @param profileImageUrl Avatar image URL as trailing action
 * @param onAvatarClick Avatar click callback (required with profileImageUrl)
 * @param containerColor Background override
 * @param contentColor Icon/text color
 * @param size Height/icon/font preset
 * @param elevation Shadow elevation (defaults to [NavHeaderElevation])
 * @param bottomDivider Show bottom divider line
 * @param includeSafeAreaPadding Status bar inset
 * @param enableScrolling Horizontal scroll for overflow actions
 */
@Composable
fun PixaTopNavBar(
    modifier: Modifier = Modifier,
    variant: NavHeaderVariant = NavHeaderVariant.Fixed,
    collapsed: Boolean = false,
    title: String? = null,
    subtitle: String? = null,
    titleComposable: @Composable (() -> Unit)? = null,
    titleAlignment: TopNavTitleAlignment? = null,
    startActions: List<TopNavAction> = emptyList(),
    endActions: List<TopNavAction> = emptyList(),
    mirrorLeadingIconForRtl: Boolean = true,
    profileImageUrl: String? = null,
    containerColor: Color? = null,
    contentColor: Color = AppTheme.colors.baseContentTitle,
    size: SizeVariant = SizeVariant.Medium,
    elevation: Dp = NavHeaderElevation,
    bottomDivider: Boolean = false,
    includeSafeAreaPadding: Boolean = false,
    enableScrolling: Boolean = false,
    onAvatarClick: (() -> Unit)? = null,
    ) {
    // Validation
    if (profileImageUrl != null) {
        require(onAvatarClick != null) {
            "onAvatarClick is required when profileImageUrl is provided"
        }
    }

    val isFloating = variant == NavHeaderVariant.Floating
    val sizeConfig = size.toSizeConfig()
    val resolvedContainerColor = containerColor ?: if (isFloating) Color.Transparent else AppTheme.colors.baseSurfaceDefault
    val protectionColor = if (isFloating) AppTheme.colors.baseSurfaceSubtle else null
    val isCollapsed = collapsed && !isFloating

    val targetHeight = if (isCollapsed) sizeConfig.height * CollapsedHeightRatio else sizeConfig.height
    val targetFontScale = if (isCollapsed) sizeConfig.titleFontScale * CollapsedTitleScaleRatio else sizeConfig.titleFontScale
    val animatedHeight by animateDpAsState(targetValue = targetHeight, animationSpec = AnimationUtils.standardSpring(), label = "nav_header_height")
    val animatedFontScale by animateFloatAsState(targetValue = targetFontScale, animationSpec = AnimationUtils.standardSpring(), label = "nav_header_title_scale")

    // Calculate status bar padding
    val statusBarPadding = if (includeSafeAreaPadding) {
        WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    } else {
        0.dp
    }

    // Collapsed always left-aligns.
    val resolvedAlignment = when {
        isCollapsed -> TopNavTitleAlignment.Start
        titleAlignment != null -> titleAlignment
        startActions.isEmpty() -> TopNavTitleAlignment.Center
        else -> TopNavTitleAlignment.Start
    }

    // Scroll state for actions
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (elevation > 0.dp) Modifier.elevationShadow(elevation, RectangleShape) else Modifier)
            .background(resolvedContainerColor)
    ) {
        // Status bar spacer
        if (statusBarPadding > 0.dp) {
            Spacer(modifier = Modifier.height(statusBarPadding))
        }

        // Main content row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .padding(horizontal = sizeConfig.horizontalPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading button gets an extra left inset.
            if (startActions.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(sizeConfig.actionSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(start = LeadingActionStartPadding)
                        .then(
                            if (enableScrolling && startActions.size > 2) {
                                Modifier.horizontalScroll(scrollState)
                            } else {
                                Modifier
                            }
                        )
                ) {
                    startActions.forEachIndexed { index, action ->
                        ActionButton(
                            action = action,
                            iconSize = sizeConfig.iconSize,
                            contentColor = contentColor,
                            protectionColor = protectionColor,
                            mirrorForRtl = index == 0 && mirrorLeadingIconForRtl
                        )
                    }
                }

                Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))
            }

            // Flexible-weight title section (none for Floating variant).
            if (!isFloating) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = HierarchicalSize.Spacing.Compact),
                    contentAlignment = when (resolvedAlignment) {
                        TopNavTitleAlignment.Start -> Alignment.CenterStart
                        TopNavTitleAlignment.Center -> Alignment.Center
                    }
                ) {
                    when {
                        titleComposable != null -> {
                            // Custom title composable
                            titleComposable()
                        }
                        title != null || subtitle != null -> {
                            // Default title/subtitle
                            TopNavTitleSection(
                                title = title,
                                subtitle = subtitle,
                                alignment = resolvedAlignment,
                                fontScale = animatedFontScale
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // End actions section
            if (endActions.isNotEmpty() || profileImageUrl != null) {
                Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(sizeConfig.actionSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = if (enableScrolling && endActions.size > 3) {
                        Modifier.horizontalScroll(scrollState)
                    } else {
                        Modifier
                    }
                ) {
                    endActions.forEach { action ->
                        ActionButton(
                            action = action,
                            iconSize = sizeConfig.iconSize,
                            contentColor = contentColor,
                            protectionColor = protectionColor,
                            mirrorForRtl = false
                        )
                    }

                    // Profile avatar (if provided)
                    if (profileImageUrl != null && onAvatarClick != null) {
                        Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Compact))
                        PixaAvatar(
                            imageUrl = profileImageUrl,
                            size = sizeConfig.avatarSize,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                role = Role.Button,
                                onClick = onAvatarClick
                            )
                        )
                    }
                }
            }
        }

        // Bottom divider.
        if (bottomDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(NavHeaderBorderWidth)
                    .background(AppTheme.colors.baseBorderSubtle)
            )
        }
    }
}