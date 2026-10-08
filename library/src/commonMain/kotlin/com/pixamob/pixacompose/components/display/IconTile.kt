package com.pixamob.pixacompose.components.display

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.theme.forVariant

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Icon tile container shape. [Rounded] is a rounded square; [Circle] is a circle;
 * [Squircle] uses the theme's rounded shape for the current size.
 */
enum class IconTileShape {
    Rounded,
    Circle,
    Squircle
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class IconTileColors(
    val background: Color,
    /** null means keep the artwork's original colors (useful for [IconSource.Url]). */
    val content: Color?,
    val border: Color? = null
)

@Immutable
@Stable
private data class IconTileSizeConfig(
    val containerSize: Dp,
    val iconSize: Dp
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getIconTileColors(tone: IconTone, colors: ColorPalette): IconTileColors = when (tone) {
    IconTone.Default -> IconTileColors(
        background = colors.baseSurfaceSubtle,
        content = colors.baseContentTitle
    )
    IconTone.Subtle -> IconTileColors(
        background = colors.baseSurfaceSubtle,
        content = colors.baseContentCaption
    )
    IconTone.Brand -> IconTileColors(
        background = colors.brandSurfaceSubtle,
        content = colors.brandContentDefault
    )
    IconTone.Accent -> IconTileColors(
        background = colors.accentSurfaceSubtle,
        content = colors.accentContentDefault
    )
    IconTone.Info -> IconTileColors(
        background = colors.infoSurfaceSubtle,
        content = colors.infoContentDefault
    )
    IconTone.Success -> IconTileColors(
        background = colors.successSurfaceSubtle,
        content = colors.successContentDefault
    )
    IconTone.Warning -> IconTileColors(
        background = colors.warningSurfaceSubtle,
        content = colors.warningContentDefault
    )
    IconTone.Error -> IconTileColors(
        background = colors.errorSurfaceSubtle,
        content = colors.errorContentDefault
    )
    IconTone.Disabled -> IconTileColors(
        background = colors.baseSurfaceDisabled,
        content = colors.baseContentDisabled
    )
    IconTone.Inverse -> IconTileColors(
        background = colors.baseContentTitle,
        content = colors.baseContentNegative
    )
}

private fun getIconTileSizeConfig(size: SizeVariant): IconTileSizeConfig = when (size) {
    SizeVariant.Nano -> IconTileSizeConfig(
        containerSize = HierarchicalSize.Container.Compact,
        iconSize = HierarchicalSize.Icon.Compact
    )
    SizeVariant.Compact, SizeVariant.Small -> IconTileSizeConfig(
        containerSize = HierarchicalSize.Container.Medium,
        iconSize = HierarchicalSize.Icon.Medium
    )
    SizeVariant.Medium -> IconTileSizeConfig(
        containerSize = HierarchicalSize.Container.Huge,
        iconSize = HierarchicalSize.Icon.Large
    )
    SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> IconTileSizeConfig(
        containerSize = HierarchicalSize.Container.Massive,
        iconSize = HierarchicalSize.Icon.Huge
    )
    else -> IconTileSizeConfig(
        containerSize = HierarchicalSize.Container.Huge,
        iconSize = HierarchicalSize.Icon.Large
    )
}

@Composable
private fun IconTileShape.toShape(size: SizeVariant): Shape = when (this) {
    IconTileShape.Rounded -> AppTheme.shapes.rounded.large
    IconTileShape.Circle -> AppTheme.shapes.pill
    IconTileShape.Squircle -> AppTheme.shapes.rounded.forVariant(size)
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaIconTile — a tinted, rounded-square (or circular) container for an icon.
 *
 * ### Anatomy
 * Solid background + centered [PixaIcon]. The container size and icon size are
 * derived from [SizeVariant].
 *
 * ### Variants
 * [IconTone] selects the semantic color pair: background uses the matching
 * `*SurfaceSubtle` token and the icon uses the matching `*ContentDefault` token.
 *
 * ### Sizing
 * [SizeVariant.Small] → 48dp container / 24dp icon  
 * [SizeVariant.Medium] → 64dp container / 32dp icon  
 * [SizeVariant.Large] → 96dp container / 48dp icon
 *
 * ### Customization
 * [customColors] overrides the resolved background/content/border. Pass
 * `content = null` to keep a multicolor remote icon untinted.
 *
 * @param icon Icon artwork source
 * @param modifier Modifier for the tile
 * @param tone Semantic color tone (Default: [IconTone.Brand])
 * @param size Size variant (Default: [SizeVariant.Medium])
 * @param shape Container shape (Default: [IconTileShape.Rounded])
 * @param customColors Optional color override
 * @param contentDescription Accessibility label for the icon
 */
@Composable
fun PixaIconTile(
    icon: IconSource,
    modifier: Modifier = Modifier,
    tone: IconTone = IconTone.Brand,
    size: SizeVariant = SizeVariant.Medium,
    shape: IconTileShape = IconTileShape.Rounded,
    customColors: IconTileColors? = null,
    contentDescription: String? = null
) {
    val colors = customColors ?: getIconTileColors(tone, AppTheme.colors)
    val sizeConfig = getIconTileSizeConfig(size)
    val resolvedShape = shape.toShape(size)

    Box(
        modifier = modifier
            .size(sizeConfig.containerSize)
            .clip(resolvedShape)
            .background(colors.background)
            .then(
                if (colors.border != null) {
                    Modifier.border(HierarchicalSize.Border.Compact, colors.border, resolvedShape)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        PixaIcon(
            source = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(sizeConfig.iconSize),
            tint = colors.content
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Circular icon tile convenience.
 */
@Composable
fun PixaIconCircle(
    icon: IconSource,
    modifier: Modifier = Modifier,
    tone: IconTone = IconTone.Brand,
    size: SizeVariant = SizeVariant.Medium,
    customColors: IconTileColors? = null,
    contentDescription: String? = null
) = PixaIconTile(
    icon = icon,
    modifier = modifier,
    tone = tone,
    size = size,
    shape = IconTileShape.Circle,
    customColors = customColors,
    contentDescription = contentDescription
)
