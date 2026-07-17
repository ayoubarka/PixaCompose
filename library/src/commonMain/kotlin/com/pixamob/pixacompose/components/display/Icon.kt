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
 * Semantic colour intent for an icon, mapped from the eBay iconography guidance:
 * "Icons are monochromatic by default. The majority will use our primary
 * foreground color token unless in a disabled state within a button", and
 * "Icons that convey semantic meaning, like attention, information, or
 * confirmation, can use semantic colors over primary or secondary backgrounds."
 *
 * Reach for a tone only when the icon's *meaning* requires it. [Default] is the
 * monochrome primary-foreground case and covers the majority of icons.
 *
 * [Inverse] is the guidance's companion rule: "If the icon is over a background
 * container using a semantic color, the icon will use an inverse color scheme
 * instead" — i.e. use it for an icon sitting on a filled semantic surface, not
 * on the page background.
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

    /** Disabled state (the guidance's "disabled state within a button"). */
    Disabled,

    /** For an icon drawn on top of a filled/semantic container. */
    Inverse
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Where the icon artwork comes from. All three source types are supported
 * equally — [Url] matters for remotely-hosted marks that ship outside the
 * bundled asset set, and is the one source that is commonly multicolour.
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
 * Ambient icon tint, implementing the guidance's pairing rule: "If an icon is
 * paired with a label or body of text, the icon will match the color of the
 * text."
 *
 * A component that renders text alongside icons provides its resolved text
 * colour here once, and any [PixaIcon] beneath it matches without every call
 * site repeating `tint = ...`. [Color.Unspecified] means "nothing provided" —
 * [PixaIcon] then falls back to the monochrome primary-foreground token.
 *
 * This is the PixaCompose-owned replacement for Material 3's `LocalContentColor`,
 * which this primitive no longer depends on. It is deliberately icon-scoped
 * rather than a general content-colour ambient: components in this library pass
 * text colours explicitly, so a library-wide ambient would have no other reader.
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
 * Resolves the effective tint, in precedence order:
 * 1. `tint == null` — caller opted out of tinting entirely; artwork keeps its
 *    own colours (logos, multicolour remote marks).
 * 2. an explicit [Color] — caller-supplied override wins.
 * 3. a non-[IconTone.Default] [tone] — a declared semantic meaning beats an
 *    ambient text colour.
 * 4. [LocalIconTint] — matches surrounding text, when a component provided it.
 * 5. the monochrome primary-foreground token.
 */
@Composable
private fun resolveIconTint(tint: Color?, tone: IconTone): Color? = when {
    tint == null -> null
    tint.isSpecified -> tint
    tone != IconTone.Default -> tone.toColor()
    else -> LocalIconTint.current.takeIf { it.isSpecified } ?: IconTone.Default.toColor()
}

/**
 * The approved icon sizes — every tier of the shared [HierarchicalSize.Icon]
 * ladder. This is PixaCompose's equivalent of the guidance's fixed 16/24px
 * asset sizes: the exact pixel values are eBay-asset-specific, but the rule
 * they exist to enforce ("Don't scale icons to arbitrary sizes. This leads to
 * aliasing and mismatched stroke widths") maps onto the ladder directly.
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
 * PixaIcon — the single core icon primitive for the library. Refined against
 * eBay's iconography guidance.
 *
 * ### Purpose
 * Renders one icon from a vector, painter, or URL [IconSource], sized from the
 * approved ladder and coloured from theme tokens.
 *
 * ### Colour
 * Monochrome by default: with no [tint] and no [tone], the icon uses the primary
 * foreground token. Set [tone] when the icon carries semantic meaning; provide
 * [LocalIconTint] (or pass [tint]) to match adjacent text. Pass `tint = null` to
 * keep the artwork's own colours — the escape hatch for logos and multicolour
 * remote marks. See [resolveIconTint] for the full precedence order.
 *
 * ### Sizing
 * [size] selects a tier of the shared [HierarchicalSize.Icon] ladder — prefer it.
 * [customSize] is an escape hatch that bypasses the ladder; the guidance is
 * explicit that arbitrary sizes cause "aliasing and mismatched stroke widths",
 * so an off-ladder [customSize] logs a development warning. Passing a ladder
 * token (e.g. `HierarchicalSize.Icon.Medium`) is always fine — that is how the
 * library's own components feed a resolved size through.
 *
 * ### Selected state
 * "Some icons have an outlined and a filled version. The filled versions are
 * used to indicate a selected state or to increase their prominence within a
 * hierarchy." Supply the filled artwork as [selectedSource] and drive it with
 * [selected]; without a [selectedSource], [selected] has no effect (this
 * primitive never fabricates a filled variant from an outlined one).
 *
 * ### Accessibility
 * [contentDescription] follows the Compose convention: a description for a
 * meaningful icon, `null` for a decorative one whose meaning is already carried
 * by adjacent text. The guidance does not address decorative-vs-meaningful
 * icons, so the Compose convention stands.
 *
 * ### Usage notes (caller's responsibility — layout, not this primitive)
 * - "Use 4px of spacing between 16px icons and text and 8px between 24px icons
 *   and text", with aligned vertical centres.
 * - "Do align icons to the top of the text box if paired with 3 or more lines of
 *   text" — don't centre them in that case.
 * - Prefer universally-understood icons; avoid icons whose meaning shifts across
 *   cultures, and don't use an icon where ambiguity would hurt comprehension.
 *
 * @param source The icon artwork (Vector, Resource, or Url)
 * @param contentDescription Accessibility description; `null` marks it decorative
 * @param modifier Modifier for the icon
 * @param tint Explicit colour override. Defaults to [Color.Unspecified] meaning
 *   "resolve from [tone]/[LocalIconTint]"; pass `null` to keep original colours
 * @param tone Semantic colour intent (Default: [IconTone.Default], monochrome)
 * @param size Icon size variant from the approved ladder (Default: [SizeVariant.Medium])
 * @param customSize Off-ladder exact size — discouraged, see Sizing above
 * @param selected Whether to render [selectedSource] instead of [source]
 * @param selectedSource Filled artwork used when [selected] is true
 * @param animation Enable the scale+fade entrance animation
 * @param placeholder Placeholder painter for URL icons
 * @param error Error painter for URL icons
 * @param contentScale How to scale the content
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
