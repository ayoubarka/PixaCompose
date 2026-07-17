package com.pixamob.pixacompose.components.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.ButtonWidthPolicy
import com.pixamob.pixacompose.components.actions.IconButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.actions.PixaIconButton
import com.pixamob.pixacompose.components.feedback.SkeletonText
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Trailing content slot, mapped from Uber Base's Section Heading trailing
 * options: none, a single text button, a single icon button (mutually
 * exclusive with [TextButton] per spec), or up to 2 labels/paragraphs.
 * Renders in the top row next to [PixaSectionHeading]'s `heading`, bottom-
 * aligned with it, per the spec's "top content (heading + trailing) aligns
 * bottom" rule.
 */
sealed class SectionHeadingTrailing {
    data object None : SectionHeadingTrailing()
    data class TextButton(val text: String, val onClick: () -> Unit) : SectionHeadingTrailing()
    data class IconButton(
        val icon: Painter,
        val onClick: () -> Unit,
        val contentDescription: String? = null
    ) : SectionHeadingTrailing()

    /** Capped at 2 entries per spec anatomy; extra entries are dropped, not wrapped. */
    data class Labels(val labels: List<String>) : SectionHeadingTrailing()
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class SectionHeadingColors(
    val headingContent: Color = Color.Unspecified,
    val subheadingContent: Color = Color.Unspecified,
    val labelContent: Color = Color.Unspecified,
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Resolves default content colors. Uber Base's "Disabled" state (`backgroundStateDisabled`
 * fill / `contentStateDisabled` text) only applies once [PixaSectionHeading]'s `enabled`
 * is false — headings are non-interactive by default, so there is no fill to
 * theme, only the text/icon content color per the spec's disabled row.
 */
@Composable
private fun getSectionHeadingTheme(colors: ColorPalette, enabled: Boolean): SectionHeadingColors {
    if (!enabled) {
        return SectionHeadingColors(
            headingContent = colors.baseContentDisabled,
            subheadingContent = colors.baseContentDisabled,
            labelContent = colors.baseContentDisabled,
        )
    }
    return SectionHeadingColors(
        headingContent = colors.baseContentTitle,
        subheadingContent = colors.baseContentSubtitle,
        labelContent = colors.baseContentBody,
    )
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL SECTION HEADING
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun TrailingContent(
    trailing: SectionHeadingTrailing,
    enabled: Boolean,
    size: SizeVariant,
    labelColor: Color,
    modifier: Modifier = Modifier,
) {
    when (trailing) {
        SectionHeadingTrailing.None -> Unit

        is SectionHeadingTrailing.TextButton -> PixaButton(
            text = trailing.text,
            onClick = trailing.onClick,
            modifier = modifier,
            variant = ButtonVariant.Ghost,
            widthPolicy = ButtonWidthPolicy.Flexible,
            size = size,
            enabled = enabled,
            maxLines = 1,
        )

        is SectionHeadingTrailing.IconButton -> PixaIconButton(
            icon = trailing.icon,
            onClick = trailing.onClick,
            modifier = modifier,
            variant = IconButtonVariant.Ghost,
            size = size,
            enabled = enabled,
            contentDescription = trailing.contentDescription,
        )

        is SectionHeadingTrailing.Labels -> Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
            verticalAlignment = Alignment.Bottom,
        ) {
            trailing.labels.take(2).forEach { label ->
                BasicText(
                    text = label,
                    style = AppTheme.typography.labelMedium.copy(color = labelColor),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaSectionHeading — starts a new content section and gives users an
 * overview of how content is structured on the screen.
 *
 * ### Anatomy
 * Required container + [heading]. Top content is [heading] plus an optional
 * [trailing] slot (text button, icon button, or up to 2 labels — text button
 * and icon button are mutually exclusive per spec). Bottom content is an
 * optional [subheading]. Top content aligns bottom, matching the spec's
 * "heading + trailing aligns bottom" rule.
 *
 * ### Variants
 * Driven by [trailing] ([SectionHeadingTrailing.None]/`.TextButton`/`.IconButton`/`.Labels`).
 *
 * ### States
 * Preloading ([loading] → [SkeletonText] placeholders, matching the spec's
 * `backgroundPrimary`-filled placeholder), enabled (`contentTitle`/`contentSubtitle`),
 * disabled ([enabled] → `contentStateDisabled` on heading/subheading/labels;
 * hover/pressed/focus on [trailing] interactive elements are inherited for
 * free from the reused [PixaButton]/[PixaIconButton] Ghost variants rather
 * than reimplemented here).
 *
 * ### Sizing
 * [size] drives inter-line spacing and the reused trailing control's own
 * size tier. Uber Base doesn't define discrete heading height tiers itself
 * (only fixed icon-button/text-button row heights, which come from the
 * reused button components), so this follows Pixa's own [SizeVariant]
 * convention rather than a spec-defined ladder.
 *
 * ### Adaptive behavior
 * "Extend edge-to-edge" at narrow widths vs. "take the width of the UI
 * elements they represent" at wide widths is a caller-side layout concern —
 * this composable always fills its incoming width via `fillMaxWidth()`;
 * screen-level code controls edge-to-edge vs. contained width through
 * [AppTheme.pageMargin]/container width, per `CLAUDE.md`'s "adaptive
 * behavior is a default/fallback, explicit caller-provided sizes remain
 * authoritative" rule. No second responsive system is introduced here.
 *
 * ### Customization
 * [customColors] overrides heading/subheading/label content color only
 * (per spec's "text style and color can be overridden to reduce prominence,
 * provided consistency is maintained" boundary); [headingStyle]/[subheadingStyle]
 * likewise allow swapping type scale for less-prominent repetitive sections.
 * Anatomy (required heading, optional subheading, single trailing slot) is
 * not overridable — a structural change the spec explicitly discourages.
 *
 * ### Usage notes
 * - Use to organize lists/cards/tiles into sections; never in place of a
 *   navigation header, and never stacked directly above one.
 * - Keep text style/color choices consistent across similar section headings
 *   in the same screen — inconsistent styling is the spec's named anti-pattern.
 * - Trailing content truncates to a single line and yields at most 30% of
 *   the row's width to [heading] when both are present (enforced via
 *   [BoxWithConstraints] below).
 *
 * @param heading Required heading text (wraps or truncates per [headingMaxLines])
 * @param modifier Modifier for the heading container
 * @param subheading Optional supporting text below the heading
 * @param trailing Optional trailing slot (Default: [SectionHeadingTrailing.None])
 * @param size Size variant driving spacing and the trailing control's size (Default: [SizeVariant.Medium])
 * @param enabled Whether the heading (and any trailing control) is interactive (Default: true)
 * @param loading Whether to render the preloading [SkeletonText] placeholder (Default: false)
 * @param headingMaxLines Max lines before [heading] truncates (Default: 2, per spec's one/two-line configurations)
 * @param headingStyle Optional override of the heading's [TextStyle] (Default: `AppTheme.typography.titleBold`)
 * @param subheadingStyle Optional override of the subheading's [TextStyle] (Default: `AppTheme.typography.bodyRegular`)
 * @param customColors Optional [SectionHeadingColors] overriding heading/subheading/label content color
 * @param description Accessibility description appended to the heading trait announcement
 *
 * @sample
 * ```
 * PixaSectionHeading(
 *     heading = "Recent orders",
 *     subheading = "Last 30 days",
 *     trailing = SectionHeadingTrailing.TextButton("See all") { }
 * )
 * ```
 */
@Composable
fun PixaSectionHeading(
    heading: String,
    modifier: Modifier = Modifier,
    subheading: String? = null,
    trailing: SectionHeadingTrailing = SectionHeadingTrailing.None,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    loading: Boolean = false,
    headingMaxLines: Int = 2,
    headingStyle: TextStyle? = null,
    subheadingStyle: TextStyle? = null,
    customColors: SectionHeadingColors? = null,
    description: String? = null,
) {
    val spacing = HierarchicalSize.Spacing.forVariant(size)

    if (loading) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing),
        ) {
            SkeletonText(width = HierarchicalSize.Container.Massive, size = size)
            if (subheading != null) {
                SkeletonText(width = HierarchicalSize.Container.Huge, size = size)
            }
        }
        return
    }

    val theme = customColors ?: getSectionHeadingTheme(AppTheme.colors, enabled)
    val resolvedHeadingStyle = (headingStyle ?: AppTheme.typography.titleBold).copy(color = theme.headingContent)
    val resolvedSubheadingStyle =
        (subheadingStyle ?: AppTheme.typography.bodyRegular).copy(color = theme.subheadingContent)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = false) {
                heading()
                description?.let { contentDescription = it }
            },
        // Fixed tight gap between heading/subheading lines (not size-driven,
        // matching the spec's own heading-height ranges which don't vary this).
        verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact),
    ) {
        if (trailing == SectionHeadingTrailing.None) {
            BasicText(
                text = heading,
                style = resolvedHeadingStyle,
                maxLines = headingMaxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val trailingMaxWidth = maxWidth * 0.3f
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    BasicText(
                        text = heading,
                        style = resolvedHeadingStyle,
                        maxLines = headingMaxLines,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = true),
                    )
                    TrailingContent(
                        trailing = trailing,
                        enabled = enabled,
                        size = size,
                        labelColor = theme.labelContent,
                        modifier = Modifier.widthIn(max = trailingMaxWidth).wrapContentWidth(Alignment.End),
                    )
                }
            }
        }

        if (subheading != null) {
            BasicText(
                text = subheading,
                style = resolvedSubheadingStyle,
                maxLines = if (headingMaxLines <= 1) 1 else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
