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
 * Badge color, matching Uber Base's 5 approved badge colors exactly.
 *
 * This intentionally does *not* reuse the generic feedback semantic axis
 * (`Info`/`Neutral`, seen on [AlertVariant]/[ToastVariant]) — the Uber Base
 * badge spec only approves these 5, and adding the other feedback colors
 * here would offer combinations the spec doesn't sanction.
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
 *
 * Uber Base only defines two notification badge sizes (small 16dp / medium
 * 20dp), which map exactly onto [HierarchicalSize.Badge.Small]/`.Medium` — so
 * any [SizeVariant] other than [SizeVariant.Small] resolves to the medium
 * tier rather than inventing extra badge sizes the spec doesn't define.
 *
 * Icon size has no exact match in [HierarchicalSize.Icon] (spec wants 10dp/12dp,
 * the ladder jumps 10dp→14dp) — it's derived as a fixed proportion of the
 * container (0.6×) instead of a hardcoded per-tier literal, which reproduces
 * both spec ratios (10/16 = 0.625, 12/20 = 0.6) closely enough that the icon
 * still visually fills the container as the spec requires.
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
        // Uber's labelXSmall type ramp has no direct Pixa equivalent;
        // labelSmall is the smallest existing label tier.
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
    // Neutral base surface + brand content reads clearly against a brand-colored
    // parent, which is the one thing OnBrand exists to solve.
    BadgeVariant.OnBrand -> BadgeColors(
        background = colors.baseSurfaceDefault,
        content = colors.brandContentDefault
    )
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * Notification Badge — Uber Base's action-oriented indicator for counts,
 * capped strings, or a single filled icon on items that require attention
 * (unread messages, cart items, pending approvals).
 *
 * ### Anatomy
 * Circular container (pill once content reaches 2+ characters) that hugs its
 * content — it never has fixed width. Holds either a numeric count / capped
 * string (`labelSmall` type), or a single filled icon; never both content
 * kinds combined with more than a decorative gap.
 *
 * ### Behavior
 * - `count` above [maxCount] renders as `"$maxCount+"`.
 * - `count == 0`, `count == null` with no [text]/[icon] renders nothing —
 *   per spec, hide the badge at zero rather than showing an empty circle.
 * - Has no tap behavior of its own — attach `onClick`/interaction to the
 *   *parent* element, never to the badge itself.
 *
 * ### Usage
 * Always anchor this to a parent element with a tap target of at least 44dp
 * (a tab, avatar, icon button) — never use standalone; the badge has
 * neither sufficient touch target nor accessible context by itself. Use
 * [BadgedBox] to position it over that parent.
 *
 * ### Accessibility
 * The badge is not independently focusable — its meaning must be conveyed
 * by the parent's content description (e.g. "Messages. 2 new
 * notifications. Button."), so [contentDescription] is left `null` by
 * default and the badge is excluded from the accessibility tree. Only pass
 * [contentDescription] for the rare case of a genuinely standalone badge.
 *
 * @param count Numeric count to display; takes precedence over [text] when both are supplied.
 * @param text Capped string content (e.g. "NEW") when a plain count doesn't apply.
 * @param icon Filled icon painter, shown instead of [count]/[text] content.
 * @param variant Badge color, one of Uber Base's 5 approved colors.
 * @param size [SizeVariant.Small] (16dp) for dense UI like nav tabs, anything else resolves to medium (20dp).
 * @param maxCount Count ceiling before display switches to `"$maxCount+"` (default 99).
 * @param modifier Modifier applied to the badge container.
 * @param contentDescription Accessibility label; leave `null` unless the badge is genuinely standalone.
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
 * Hint Badge — Uber Base's subtle status signal without quantification, for
 * when presence matters more than an exact count (online status, new
 * feature availability).
 *
 * ### Anatomy
 * Fixed-size circular dot ([HierarchicalSize.Badge.Nano], 8dp). No
 * configurable content — do not attempt to add a label, number, or icon to
 * it; the dot itself is the entire signal. Never stretch or resize it.
 *
 * ### Behavior
 * Has no tap behavior of its own — same as [PixaNotificationBadge], attach
 * interaction to the parent, not the dot. Appears when its condition is
 * true and must have a defined dismissal path — a hint badge left
 * indefinitely visible with no way to clear it is a spec anti-pattern.
 *
 * ### Usage
 * Always anchor this to a parent with a tap target of at least 44dp via
 * [BadgedBox]; never standalone.
 *
 * @param variant Badge color, one of Uber Base's 5 approved colors.
 * @param outlineColor When set, draws a thin outline in this color so the dot
 *   reads clearly against whatever it overlaps (mirrors Uber iOS's default
 *   "mask attachment" outline-matches-background behavior). Left
 *   [Color.Unspecified] (no outline) by default since Pixa has no notion of
 *   "the color directly behind this composable" to default it to automatically.
 * @param modifier Modifier applied to the dot.
 * @param contentDescription Accessibility label; leave `null` unless the dot is genuinely standalone.
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
 * parent element, satisfying the spec's "always attach the badge to a
 * parent element with a tap target of at least 44dp" requirement. Neither
 * badge reflows or resizes across viewports, so this offset is fixed rather
 * than breakpoint-driven.
 *
 * @param badge The badge composable ([PixaNotificationBadge] or [PixaHintBadge]) to overlay.
 * @param modifier Modifier for the container.
 * @param content The parent content the badge is attached to.
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
