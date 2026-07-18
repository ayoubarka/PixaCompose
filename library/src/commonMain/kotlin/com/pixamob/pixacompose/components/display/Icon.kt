package com.pixamob.pixacompose.components.display

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Semantic colour intent for an icon.
 *
 * Icons are monochrome by default ([Default]). Reach for a semantic tone only
 * when the icon's meaning requires it. [Inverse] is for icons on filled
 * semantic surfaces (not the page background).
 */
enum class IconTone {
    /** Primary foreground — the monochrome default. */
    Default,

    /** Lower-emphasis foreground for secondary/supporting icons. */
    Subtle,

    Brand,
    Accent,

    /** Semantic meanings — use only when the icon actually carries that meaning. */
    Info,
    Success,
    Warning,
    Error,

    /** Disabled state. */
    Disabled,

    /** For an icon drawn on top of a filled/semantic container. */
    Inverse
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Source of icon artwork. All three types are supported equally.
 * [Url] is for remotely-hosted marks that are commonly multicolour.
 */
@Stable
sealed class IconSource {
    @Stable
    data class Vector(val imageVector: ImageVector) : IconSource()

    @Stable
    data class Resource(val painter: Painter) : IconSource()

    @Stable
    data class Url(val url: String) : IconSource()
}

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Ambient icon tint. When a component provides its resolved text colour here,
 * any [PixaIcon] beneath it matches without repeating `tint = ...`.
 * [Color.Unspecified] falls back to the monochrome primary-foreground token.
 *
 * Icon-scoped ambient — components pass text colours explicitly, so a
 * library-wide ambient is unnecessary.
 */
val LocalIconTint = compositionLocalOf { Color.Unspecified }

/** Resolves an [IconTone] to its theme token. */
@Composable
private fun IconTone.toColor(): Color = when (this) {
    IconTone.Default -> AppTheme.colors.baseContentTitle
    IconTone.Subtle -> AppTheme.colors.baseContentCaption
    IconTone.Brand -> AppTheme.colors.brandContentDefault
    IconTone.Accent -> AppTheme.colors.accentContentDefault
    IconTone.Info -> AppTheme.colors.infoContentDefault
    IconTone.Success -> AppTheme.colors.successContentDefault
    IconTone.Warning -> AppTheme.colors.warningContentDefault
    IconTone.Error -> AppTheme.colors.errorContentDefault
    IconTone.Disabled -> AppTheme.colors.baseContentDisabled
    IconTone.Inverse -> AppTheme.colors.baseContentNegative
}

/**
 * Resolves effective tint in precedence order:
 * 1. `tint == null` — untinted (keeps artwork's own colours).
 * 2. explicit [Color] — caller override.
 * 3. non-[Default] [tone] — semantic meaning.
 * 4. [LocalIconTint] — ambient text colour from parent component.
 * 5. monochrome primary-foreground token (fallback).
 */
@Composable
private fun resolveIconTint(tint: Color?, tone: IconTone): Color? = when {
    tint == null -> null
    tint.isSpecified -> tint
    tone != IconTone.Default -> tone.toColor()
    else -> LocalIconTint.current.takeIf { it.isSpecified } ?: IconTone.Default.toColor()
}

/**
 * All tiers of the [HierarchicalSize.Icon] ladder. Arbitrary sizes cause
 * aliasing and mismatched stroke widths — prefer ladder tiers.
 */
private fun approvedIconSizes(): List<Dp> =
    SizeVariant.entries.map { HierarchicalSize.Icon.forVariant(it) }

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL ICON
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun VectorIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier,
    tint: Color?,
    contentScale: ContentScale
) {
    val painter = rememberVectorPainter(imageVector)
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = tint?.let { ColorFilter.tint(it) },
        contentScale = contentScale
    )
}

@Composable
private fun PainterIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier,
    tint: Color?,
    contentScale: ContentScale
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = tint?.let { ColorFilter.tint(it) },
        contentScale = contentScale
    )
}

@Composable
private fun UrlIcon(
    url: String,
    contentDescription: String?,
    modifier: Modifier,
    tint: Color?,
    placeholder: Painter?,
    error: Painter?,
    contentScale: ContentScale
) {
    var showError by remember { mutableStateOf(false) }

    if (showError && error != null) {
        Image(
            painter = error,
            contentDescription = contentDescription,
            modifier = modifier,
            colorFilter = tint?.let { ColorFilter.tint(it) },
            contentScale = contentScale
        )
    } else {
        AsyncImage(
            model = url,
            contentDescription = contentDescription,
            modifier = modifier,
            placeholder = placeholder,
            error = error,
            onError = { showError = true },
            colorFilter = tint?.let { ColorFilter.tint(it) },
            contentScale = contentScale
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaIcon — the single core icon primitive for the library.
 *
 * ### Color
 * Monochrome by default. Set [tone] for semantic meaning; pass [tint] to match
 * adjacent text; pass `tint = null` to keep artwork's own colors (logos,
 * multicolour remote marks). See [resolveIconTint] for precedence.
 *
 * ### Sizing
 * [size] selects a [HierarchicalSize.Icon] ladder tier — prefer this.
 * [customSize] is an escape hatch (logs a dev warning for off-ladder values).
 *
 * ### States
 * Supply filled artwork as [selectedSource] and drive with [selected].
 * Without [selectedSource], [selected] has no effect.
 *
 * ### Accessibility
 * [contentDescription] for meaningful icons; `null` marks decorative icons.
 *
 * @param source Icon artwork (Vector, Resource, or Url)
 * @param contentDescription Accessibility description; `null` = decorative
 * @param modifier Modifier for the icon
 * @param tint Explicit colour. Default: resolve from [tone]/[LocalIconTint]; `null` = keep original colours
 * @param tone Semantic colour intent (Default: monochrome)
 * @param size Icon size variant from the ladder (Default: Medium)
 * @param customSize Off-ladder exact size (discouraged)
 * @param selected Whether to render [selectedSource] instead of [source]
 * @param selectedSource Filled artwork when [selected] is true
 * @param animation Enable scale+fade entrance animation
 * @param placeholder Placeholder painter for URL icons
 * @param error Error painter for URL icons
 * @param contentScale Content scaling mode
 */
@Composable
fun PixaIcon(
    source: IconSource,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    tint: Color? = Color.Unspecified,
    tone: IconTone = IconTone.Default,
    size: SizeVariant = SizeVariant.Medium,
    customSize: Dp? = null,
    selected: Boolean = false,
    selectedSource: IconSource? = null,
    animation: Boolean = false,
    placeholder: Painter? = null,
    error: Painter? = null,
    contentScale: ContentScale = ContentScale.Fit
) {
    // Guidance: "Don't scale icons to arbitrary sizes." Ladder tokens passed
    // through as Dp stay silent; only genuinely off-ladder values warn.
    LaunchedEffect(customSize) {
        if (customSize != null && customSize !in approvedIconSizes()) {
            println(
                "⚠️ PixaIcon: customSize=$customSize is not an approved icon size. " +
                    "Prefer size = SizeVariant.* (HierarchicalSize.Icon ladder); " +
                    "arbitrary sizes cause aliasing and mismatched stroke widths."
            )
        }
    }

    val effectiveSize = customSize ?: HierarchicalSize.Icon.forVariant(size)
    val effectiveTint = resolveIconTint(tint, tone)
    val effectiveSource = if (selected && selectedSource != null) selectedSource else source

    // Animation state
    var isVisible by remember { mutableStateOf(!animation) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = AnimationUtils.standardSpring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = AnimationUtils.selectionSpring,
        label = "iconAlpha"
    )

    LaunchedEffect(Unit) {
        if (animation) {
            isVisible = true
        }
    }

    val iconModifier = if (animation) {
        modifier
            .size(effectiveSize)
            .scale(scale)
            .alpha(alpha)
    } else {
        modifier.size(effectiveSize)
    }

    when (effectiveSource) {
        is IconSource.Vector -> VectorIcon(
            imageVector = effectiveSource.imageVector,
            contentDescription = contentDescription,
            modifier = iconModifier,
            tint = effectiveTint,
            contentScale = contentScale
        )

        is IconSource.Resource -> PainterIcon(
            painter = effectiveSource.painter,
            contentDescription = contentDescription,
            modifier = iconModifier,
            tint = effectiveTint,
            contentScale = contentScale
        )

        is IconSource.Url -> UrlIcon(
            url = effectiveSource.url,
            contentDescription = contentDescription,
            modifier = iconModifier,
            tint = effectiveTint,
            placeholder = placeholder,
            error = error,
            contentScale = contentScale
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaIcon from an [ImageVector]. For outlined/filled selected-state pairs, use
 * the [IconSource]-based [PixaIcon] with `selectedSource`.
 */
@Composable
fun PixaIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = Color.Unspecified,
    tone: IconTone = IconTone.Default,
    size: SizeVariant = SizeVariant.Medium,
    customSize: Dp? = null,
    animation: Boolean = false
) {
    PixaIcon(
        source = IconSource.Vector(imageVector),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        tone = tone,
        size = size,
        customSize = customSize,
        animation = animation
    )
}

/**
 * PixaIcon from a [Painter]. For outlined/filled selected-state pairs, use the
 * [IconSource]-based [PixaIcon] with `selectedSource`.
 */
@Composable
fun PixaIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = Color.Unspecified,
    tone: IconTone = IconTone.Default,
    size: SizeVariant = SizeVariant.Medium,
    customSize: Dp? = null,
    animation: Boolean = false
) {
    PixaIcon(
        source = IconSource.Resource(painter),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        tone = tone,
        size = size,
        customSize = customSize,
        animation = animation
    )
}

/**
 * PixaIcon from a URL. [tint] defaults to `null` here — remotely-hosted marks
 * are usually multicolour and should keep their own colours; pass a colour or
 * a [tone] explicitly to force a monochrome remote glyph.
 */
@Composable
fun PixaIcon(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = null,
    tone: IconTone = IconTone.Default,
    size: SizeVariant = SizeVariant.Medium,
    customSize: Dp? = null,
    animation: Boolean = false,
    placeholder: Painter? = null,
    error: Painter? = null
) {
    PixaIcon(
        source = IconSource.Url(url),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        tone = tone,
        size = size,
        customSize = customSize,
        animation = animation,
        placeholder = placeholder,
        error = error
    )
}
