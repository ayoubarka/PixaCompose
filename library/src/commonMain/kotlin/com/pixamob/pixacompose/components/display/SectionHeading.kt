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
 * Trailing content slot. Options: none, text button, icon button (mutually exclusive),
 * or up to 2 labels. Renders in the top row bottom-aligned with heading.
 */
sealed class SectionHeadingTrailing {
    data object None : SectionHeadingTrailing()
    data class TextButton(val text: String, val onClick: () -> Unit) : SectionHeadingTrailing()
    data class IconButton(
        val icon: Painter,
        val onClick: () -> Unit,
        val contentDescription: String? = null
    ) : SectionHeadingTrailing()

    /** Capped at 2 entries; extra entries are dropped. */
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
 * Resolves default content colors. Headings are non-interactive by default,
 * so only text content dims when disabled — no fill to theme.
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
 * PixaSectionHeading — starts a content section and structures the screen.
 *
 * ### Anatomy
 * Required [heading], optional [subheading], optional [trailing] slot
 * (text button, icon button, or up to 2 labels — text and icon buttons
 * are mutually exclusive). Top content aligns bottom.
 *
 * ### States
 * Preloading ([SkeletonText]), enabled, disabled (content dimmed).
 * Interactive states on trailing controls inherited from [PixaButton]/[PixaIconButton].
 *
 * ### Sizing
 * [size] drives inter-line spacing and trailing control size.
 *
 * ### Customization
 * [customColors] overrides content colors; [headingStyle]/[subheadingStyle]
 * swap type scale. Anatomy structure is not overridable.
 *
 * ### Usage notes
 * - Use to organize lists/cards/tiles into sections.
 * - Keep text style consistent across similar headings in the same screen.
 * - Trailing content truncates to a single line and yields at most 30% width.
 *
 * @param heading Required heading text
 * @param modifier Modifier for the heading container
 * @param subheading Optional supporting text below the heading
 * @param trailing Optional trailing slot (Default: None)
 * @param size Size variant (Default: Medium)
 * @param enabled Whether interactive (Default: true)
 * @param loading Shows [SkeletonText] (Default: false)
 * @param headingMaxLines Max heading lines (Default: 2)
 * @param headingStyle Heading [TextStyle] override (Default: titleBold)
 * @param subheadingStyle Subheading [TextStyle] override (Default: bodyRegular)
 * @param customColors Optional content color override
 * @param description Accessibility description
 *
 * @sample
 * PixaSectionHeading(
 *     heading = "Recent orders",
 *     subheading = "Last 30 days",
 *     trailing = SectionHeadingTrailing.TextButton("See all") { }
 * )
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
        // Fixed tight gap between heading/subheading lines (not size-driven).
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
