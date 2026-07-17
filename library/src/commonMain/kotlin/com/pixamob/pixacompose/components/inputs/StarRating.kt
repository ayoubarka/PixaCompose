package com.pixamob.pixacompose.components.inputs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.components.feedback.Skeleton
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Uber Base's top-level Star Rating axis. [Interactive] is "tap/click to select" (always 5 large
 * stars, spec's only supported interactive size — "change interactive star size below large variant"
 * is explicitly prohibited). [Descriptive] is purely visual, no interaction — see [StarRatingSize] for
 * its two supported sizes.
 */
enum class StarRatingVariant {
    Interactive,
    Descriptive
}

/**
 * Spec's 3 named sizes. [Large] is [StarRatingVariant.Interactive]-only. [Medium] (5 stars, "best
 * paired with accompanying headings") and [Small] (single star + required leading numeral) are
 * [StarRatingVariant.Descriptive]-only. Mixing size with the wrong variant isn't blocked at the type
 * level (would require two separate enums for two call sites each), but is documented as a spec
 * constraint on [PixaStarRating] itself.
 */
enum class StarRatingSize {
    Large,
    Medium,
    Small
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class StarRatingColors(
    val activeFill: Color,
    val inactiveOutline: Color,
    val hoverOverlay: Color,
    val pressedOverlay: Color,
    val focusBorder: Color,
    val descriptiveContent: Color
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Spec: active stars use `contentPrimary`, inactive stars use `contentTertiary`, hover/pressed are
 * black overlays (never color-only — spec explicitly forbids "using hue alone to differentiate star
 * states"; fill-vs-outline is the state signal, these overlays are on top of that, not instead of it),
 * and interactive star colors are explicitly non-customizable ("Prohibited: modify interactive star
 * colors") — so unlike every other migrated component this session, [PixaStarRating] intentionally has
 * no `colors: StarRatingColors?` override parameter for the interactive variant.
 */
@Composable
private fun getStarRatingColors(): StarRatingColors {
    val colors = AppTheme.colors
    return StarRatingColors(
        activeFill = colors.baseContentTitle,
        inactiveOutline = colors.baseContentCaption,
        hoverOverlay = Color.Black.copy(alpha = 0.04f),
        pressedOverlay = Color.Black.copy(alpha = 0.08f),
        focusBorder = colors.brandBorderFocus,
        descriptiveContent = colors.baseContentTitle
    )
}

/** Spec: "Small: 2px spacing... Medium: 4px spacing... Large: 8px spacing" — all three land exactly on
 * [HierarchicalSize.Spacing]'s existing ladder (Nano/Compact/Small). */
private fun spacingFor(size: StarRatingSize): Dp = when (size) {
    StarRatingSize.Small -> HierarchicalSize.Spacing.Nano
    StarRatingSize.Medium -> HierarchicalSize.Spacing.Compact
    StarRatingSize.Large -> HierarchicalSize.Spacing.Small
}

/** Spec gives no exploitable literal star-icon diameter (the 608×176/608×152/513×92px tables read as
 * full Figma-frame captures — the same false lead already flagged in this codebase for other migrated
 * specs' dimension tables — not per-star sizes), so this descends [HierarchicalSize.Icon]'s existing
 * ladder by named tier instead. */
private fun iconSizeFor(size: StarRatingSize): Dp = when (size) {
    StarRatingSize.Large -> HierarchicalSize.Icon.Massive
    StarRatingSize.Medium -> HierarchicalSize.Icon.Large
    StarRatingSize.Small -> HierarchicalSize.Icon.Small
}

/** Spec: "Inactive stars: outlined in 3px `contentTertiary` border" and "Focus: 3px border outline" —
 * both match [HierarchicalSize.Border.Large] exactly. */
private val StarOutlineWidth = HierarchicalSize.Border.Large

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL STAR RATING
// ════════════════════════════════════════════════════════════════════════════

/** A 5-pointed star polygon, drawn locally (no bundled icon asset, matching how [com.pixamob.pixacompose.theme.CustomShapes]
 * draws other raw shape geometry) so [filled] vs outline can share one exact silhouette — spec:
 * "Using different colors for star stroke and fill to indicate state fails WCAG 1.4.11" is avoided by
 * construction, since fill/outline is the actual state signal here, not a color swap on the same shape. */
private fun starPath(sizePx: Float): Path {
    val path = Path()
    val outerRadius = sizePx / 2f
    val innerRadius = outerRadius * 0.382f // classic 5-point star inner/outer ratio
    val center = Offset(outerRadius, outerRadius)
    val points = 5
    for (i in 0 until points * 2) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        // Start at the top point (-90°), stepping by 36° (180°/points) per vertex.
        val angle = (PI / points) * i - PI / 2
        val x = center.x + radius * cos(angle).toFloat()
        val y = center.y + radius * sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

@Composable
private fun StarIcon(
    filled: Boolean,
    size: Dp,
    fillColor: Color,
    outlineColor: Color,
    strokeWidth: Dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val path = starPath(this.size.minDimension)
        if (filled) {
            drawPath(path, color = fillColor, style = Fill)
        } else {
            drawPath(path, color = outlineColor, style = Stroke(width = strokeWidth.toPx()))
        }
    }
}

/** Spec: "Whole values: numeral only, omit decimal/trailing zeros... Fractional: 1-2 decimal places,
 * avoid trailing zeros" (e.g. 4f → "4", 4.2f → "4.2", 4.25f → "4.25", 4.20f → "4.2"). */
private fun formatRatingValue(rating: Float): String {
    val rounded = (rating * 100).roundToInt() / 100f
    return if (rounded == rounded.toInt().toFloat()) {
        rounded.toInt().toString()
    } else {
        val twoDecimals = (rounded * 100).roundToInt() / 100f
        twoDecimals.toString().trimEnd('0').trimEnd('.')
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaStarRating — communicates feedback on a scale of 0-5, either collecting a rating
 * ([StarRatingVariant.Interactive]) or displaying one ([StarRatingVariant.Descriptive]).
 *
 * ### Anatomy
 * [StarRatingVariant.Interactive]: 5 [StarRatingSize.Large] stars in a row, always all 5, tap-to-select.
 * [StarRatingVariant.Descriptive] + [StarRatingSize.Medium]: 5 stars, purely visual. [StarRatingVariant.Descriptive]
 * + [StarRatingSize.Small]: a single star preceded by the required numeric [rating] text — spec anti-pattern:
 * "rating values positioned after the star icon reduces scannability," so the numeral always renders first.
 *
 * ### States
 * Interactive: Enabled (fill vs 3px outline, never color alone — WCAG 1.4.11), Hover (4% black overlay,
 * pointer input), Pressed (8% black overlay), Focus (3px accent border), and [loading] (a [Skeleton]
 * placeholder row, spec's "Preloading" state). Descriptive has no states — "purely visual; no
 * interaction or state changes."
 *
 * ### Customization
 * Spec explicitly prohibits changing interactive star colors, size (below Large), or the 0-5 scale —
 * so unlike other migrated components, [PixaStarRating] has no interactive color-override parameter;
 * [trailingText] is the only open content slot, and only for [StarRatingSize.Small].
 *
 * ### Usage notes
 * Descriptive Medium ratings must be paired with an explanatory heading and shown "only when a rating
 * of 1-5 has been provided" — both are caller-side layout/data decisions this component can't enforce,
 * documented here per the migration's usage-rule-preservation requirement.
 *
 * @param variant [StarRatingVariant.Interactive] or [StarRatingVariant.Descriptive]
 * @param size [StarRatingSize.Large] for Interactive; [StarRatingSize.Medium]/[StarRatingSize.Small] for Descriptive
 * @param value Current rating, 0-5 (0 = unrated). For [StarRatingVariant.Interactive] this is the selected count;
 *   for [StarRatingVariant.Descriptive] this may be fractional (e.g. 4.2) — the star row rounds to the nearest
 *   whole star while [rating]'s displayed text keeps the exact fractional value, per spec's text formatting rules.
 * @param onValueChange Required for [StarRatingVariant.Interactive]; called with the tapped star's 1-based index
 * @param modifier Modifier for the row
 * @param trailingText Optional supplementary text (e.g. "(1,000 ratings)") — [StarRatingSize.Small] only
 * @param loading Spec's "Preloading" state — renders a [Skeleton] placeholder instead of stars
 * @param enabled Whether an [StarRatingVariant.Interactive] rating accepts input
 */
@Composable
fun PixaStarRating(
    variant: StarRatingVariant,
    size: StarRatingSize,
    value: Float,
    modifier: Modifier = Modifier,
    onValueChange: ((Int) -> Unit)? = null,
    trailingText: String? = null,
    loading: Boolean = false,
    enabled: Boolean = true
) {
    val colors = getStarRatingColors()
    val iconSize = iconSizeFor(size)
    val spacing = spacingFor(size)
    val isInteractive = variant == StarRatingVariant.Interactive

    if (loading) {
        val starCount = if (size == StarRatingSize.Small) 1 else 5
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(spacing)) {
            repeat(starCount) {
                Skeleton(width = iconSize, height = iconSize, shape = CircleShape)
            }
        }
        return
    }

    val stateDescription = when (value.roundToInt().coerceIn(0, 5)) {
        0 -> "No rating"
        1 -> "1 star"
        else -> "${value.roundToInt().coerceIn(0, 5)} stars"
    }

    if (size == StarRatingSize.Small) {
        // Spec: single star, required leading numeric value, optional trailing text — numeral always
        // precedes the star icon.
        Row(
            modifier = modifier.semantics { contentDescription = "Star rating"; this.stateDescription = stateDescription },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            BasicText(
                text = formatRatingValue(value),
                style = AppTheme.typography.labelMedium.copy(color = colors.descriptiveContent)
            )
            StarIcon(
                filled = true,
                size = iconSize,
                fillColor = colors.descriptiveContent,
                outlineColor = colors.descriptiveContent,
                strokeWidth = StarOutlineWidth
            )
            if (trailingText != null) {
                BasicText(
                    text = trailingText,
                    style = AppTheme.typography.labelMedium.copy(color = colors.descriptiveContent)
                )
            }
        }
        return
    }

    // Interactive (5 large stars) and Descriptive-Medium (5 stars) share the same 5-star row anatomy.
    Row(
        modifier = modifier
            .then(if (isInteractive) Modifier.progressSemantics(value / 5f) else Modifier)
            .semantics { contentDescription = "Star rating"; this.stateDescription = stateDescription },
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        for (starIndex in 1..5) {
            val filled = starIndex <= value.roundToInt().coerceIn(0, 5)
            if (isInteractive) {
                val interactionSource = remember { MutableInteractionSource() }
                val isHovered by interactionSource.collectIsHoveredAsState()
                val isPressed by interactionSource.collectIsPressedAsState()
                val isFocused by interactionSource.collectIsFocusedAsState()
                val overlay = when {
                    isPressed -> colors.pressedOverlay
                    isHovered -> colors.hoverOverlay
                    else -> Color.Transparent
                }
                Box(
                    modifier = Modifier
                        .size(HierarchicalSize.TouchTarget.Small)
                        .focusable(interactionSource = interactionSource, enabled = enabled)
                        .hoverable(interactionSource = interactionSource, enabled = enabled)
                        .clip(CircleShape)
                        .background(overlay)
                        .then(
                            if (isFocused && enabled) {
                                Modifier.border(StarOutlineWidth, colors.focusBorder, CircleShape)
                            } else {
                                Modifier
                            }
                        )
                        .clickable(
                            enabled = enabled,
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.Button,
                            onClick = { onValueChange?.invoke(starIndex) }
                        )
                        .padding((HierarchicalSize.TouchTarget.Small - iconSize) / 2),
                    contentAlignment = Alignment.Center
                ) {
                    StarIcon(
                        filled = filled,
                        size = iconSize,
                        fillColor = colors.activeFill,
                        outlineColor = colors.inactiveOutline,
                        strokeWidth = StarOutlineWidth
                    )
                }
            } else {
                StarIcon(
                    filled = filled,
                    size = iconSize,
                    fillColor = colors.descriptiveContent,
                    outlineColor = colors.descriptiveContent,
                    strokeWidth = StarOutlineWidth
                )
            }
        }
    }
}
