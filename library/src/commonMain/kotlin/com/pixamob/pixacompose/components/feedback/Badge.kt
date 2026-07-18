package com.pixamob.pixacompose.components.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.*

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Badge color. Limited to 5 semantic variants distinct from [AlertVariant]/[ToastVariant].
 */
enum class BadgeVariant {
    /** Default for most contexts: unread/new content, general emphasis. */
    Accent,
    /** Positive or completed states. */
    Success,
    /** Caution / attention needed. */
    Warning,
    /** Failed states, alerts requiring resolution. */
    Error,
    /** For badges placed on brand-colored surfaces, where a colored badge would lose contrast. */
    OnBrand
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class BadgeColors(
    val background: Color,
    val content: Color
)

@Immutable
@Stable
private data class NotificationBadgeConfig(
    val containerSize: Dp,
    val iconSize: Dp,
    val textStyle: TextStyle
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Resolves container/icon/text sizing for [PixaNotificationBadge].
 * Small → 16dp container, anything else → 20dp. Icon is 0.6× container size.
 */
@Composable
private fun getNotificationBadgeConfig(size: SizeVariant): NotificationBadgeConfig {
    val containerSize = if (size == SizeVariant.Small) {
        HierarchicalSize.Badge.Small
    } else {
        HierarchicalSize.Badge.Medium
    }
    return NotificationBadgeConfig(
        containerSize = containerSize,
        iconSize = containerSize * 0.6f,
        // labelSmall is the closest existing tier.
        textStyle = AppTheme.typography.labelSmall
    )
}

@Composable
private fun getBadgeColors(variant: BadgeVariant, colors: ColorPalette): BadgeColors = when (variant) {
    BadgeVariant.Accent -> BadgeColors(
        background = colors.accentContentDefault,
        content = colors.baseContentNegative
    )
    BadgeVariant.Success -> BadgeColors(
        background = colors.successContentDefault,
        content = colors.baseContentNegative
    )
    BadgeVariant.Warning -> BadgeColors(
        background = colors.warningContentDefault,
        content = colors.baseContentNegative
    )
    BadgeVariant.Error -> BadgeColors(
        background = colors.errorContentDefault,
        content = colors.baseContentNegative
    )
    // OnBrand uses neutral surface + brand content for readability against brand backgrounds.
    BadgeVariant.OnBrand -> BadgeColors(
        background = colors.baseSurfaceDefault,
        content = colors.brandContentDefault
    )
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * Notification badge for counts, capped strings, or a filled icon on items
 * requiring attention (unread messages, cart items, pending approvals).
 *
 * ### Anatomy
 * Circular container (pill at 2+ characters) that hugs its content — never
 * fixed width. Holds a numeric count / capped string, or a single filled
 * icon; never both combined.
 *
 * ### Behavior
 * - `count` above [maxCount] renders as `"$maxCount+"`.
 * - `count == 0`, `count == null` with no [text]/[icon] renders nothing.
 * - No tap behavior — attach interaction to the parent element.
 *
 * ### Usage
 * Always anchor to a parent with a tap target of at least 44dp via [BadgedBox].
 *
 * ### Accessibility
 * Not independently focusable — meaning must be conveyed by parent's content
 * description. [contentDescription] defaults to `null` (excluded from a11y tree).
 *
 * @param count Numeric count; takes precedence over [text]
 * @param text Capped string (e.g. "NEW")
 * @param icon Filled icon painter, shown instead of count/text
 * @param variant Badge color (Default: [BadgeVariant.Error])
 * @param size [SizeVariant.Small] → 16dp, anything else → 20dp
 * @param maxCount Ceiling before `"$maxCount+"` (Default: 99)
 * @param modifier Modifier for the badge container
 * @param contentDescription Accessibility label; leave `null` for parent-attached badges
 */
@Composable
fun PixaNotificationBadge(
    count: Int? = null,
    text: String? = null,
    icon: Painter? = null,
    variant: BadgeVariant = BadgeVariant.Error,
    size: SizeVariant = SizeVariant.Medium,
    maxCount: Int = 99,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val colors = AppTheme.colors
    val config = getNotificationBadgeConfig(size)
    val badgeColors = getBadgeColors(variant, colors)

    val displayText = when {
        count != null && count <= 0 -> null
        count != null && count > maxCount -> "$maxCount+"
        count != null -> count.toString()
        else -> text
    }

    // Nothing to show and no icon: hide entirely rather than render an empty container.
    if (displayText == null && icon == null) return

    // Container hugs content; only pill-shapes once content grows past a single character.
    val shape: Shape = if (icon != null || (displayText?.length ?: 0) <= 1) {
        CircleShape
    } else {
        AppTheme.shapes.pill
    }

    Box(
        modifier = modifier
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else {
                    Modifier.clearAndSetSemantics { }
                }
            )
            .sizeIn(minWidth = config.containerSize, minHeight = config.containerSize)
            .clip(shape)
            .background(badgeColors.background)
            .padding(horizontal = config.containerSize * 0.15f),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Nano),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                PixaIcon(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier.size(config.iconSize),
                    tint = badgeColors.content
                )
            }
            displayText?.takeIf { icon == null }?.let {
                BasicText(
                    text = it,
                    style = config.textStyle.copy(
                        color = badgeColors.content,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Hint badge — subtle status signal without quantification (online status,
 * new feature availability).
 *
 * ### Anatomy
 * Fixed 8dp circular dot ([HierarchicalSize.Badge.Nano]). No configurable
 * content — the dot itself is the entire signal.
 *
 * ### Behavior
 * No tap behavior — attach interaction to the parent. Must have a defined
 * dismissal path.
 *
 * ### Usage
 * Always anchor to a parent with a tap target of at least 44dp via [BadgedBox].
 *
 * @param variant Badge color (Default: [BadgeVariant.Accent])
 * @param outlineColor When set, draws a thin outline for contrast against backgrounds
 * @param modifier Modifier for the dot
 * @param contentDescription Accessibility label; leave `null` for parent-attached badges
 */
@Composable
fun PixaHintBadge(
    variant: BadgeVariant = BadgeVariant.Accent,
    outlineColor: Color = Color.Unspecified,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val colors = AppTheme.colors
    val badgeColors = getBadgeColors(variant, colors)

    Box(
        modifier = modifier
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else {
                    Modifier.clearAndSetSemantics { }
                }
            )
            .size(HierarchicalSize.Badge.Nano)
            .clip(CircleShape)
            .background(badgeColors.background)
            .then(
                if (outlineColor != Color.Unspecified) {
                    Modifier.border(width = HierarchicalSize.Border.Compact, color = outlineColor, shape = CircleShape)
                } else {
                    Modifier
                }
            )
    )
}

/**
 * BadgedBox — positions a [PixaNotificationBadge] or [PixaHintBadge] over a
 * parent element with a tap target of at least 44dp.
 *
 * Offset is fixed (not breakpoint-driven).
 *
 * @param badge The badge composable to overlay
 * @param modifier Modifier for the container
 * @param content The parent content the badge is attached to
 *
 * @sample
 * ```
 * BadgedBox(
 *     badge = { PixaNotificationBadge(count = 5, variant = BadgeVariant.Error) }
 * ) {
 *     PixaIcon(Icons.Default.Notifications, contentDescription = "Notifications")
 * }
 * ```
 */
@Composable
fun BadgedBox(
    badge: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        content()
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = HierarchicalSize.Spacing.Nano, y = -HierarchicalSize.Spacing.Nano)
        ) {
            badge()
        }
    }
}
