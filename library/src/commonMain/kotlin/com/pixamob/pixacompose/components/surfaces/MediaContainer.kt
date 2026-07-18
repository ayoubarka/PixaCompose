package com.pixamob.pixacompose.components.surfaces

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.pixamob.pixacompose.components.display.PixaImage
import com.pixamob.pixacompose.components.display.PixaImageRatio
import com.pixamob.pixacompose.components.display.PixaImageSource
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.utils.pixaRipple
import kotlin.math.abs
import org.jetbrains.compose.resources.painterResource

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Container aspect ratios per eBay Playbook's Media Container spec: "1:1, 2:3, 4:5, and 16:9" are
 * the four approved ratios — arbitrary ratios are called out as introducing inconsistency.
 */
enum class MediaContainerRatio(val value: Float) {
    Square(1f),
    Portrait2x3(2f / 3f),
    Portrait4x5(4f / 5f),
    Wide16x9(16f / 9f)
}

/**
 * How [PixaMediaContainer]'s media maps into its frame.
 * - [Fill] (default): content fills the entire container — the spec's "choose fill whenever possible".
 * - [Fit]: content is letterboxed, revealing the matte background.
 * - [Auto]: fills when the source's own ratio is close enough to the container's (default 75%
 *   tolerance, per the spec), otherwise falls back to [Fit]. Only resolvable for
 *   [MediaContainerSource.Image] sources with a known intrinsic size (URL or local resource) —
 *   other content behaves like [Fit] under [Auto] since no source ratio exists to compare.
 */
enum class MediaContainerFillType {
    Fill,
    Fit,
    Auto
}

/**
 * What [PixaMediaContainer] renders in its Media layer.
 * [Image] reuses [com.pixamob.pixacompose.components.display.PixaImage]'s existing source types (URL, local resource, vector, SVG). The spec's
 * other media types — video, GIF, 3D asset, Rive animation — have no renderer in PixaCompose yet, so
 * [Custom] is the escape hatch: callers supply their own composable (a video player, Rive view, etc.)
 * and the container still provides ratio framing, matte, scrim, and the disabled treatment around it.
 */
@Stable
sealed class MediaContainerSource {
    data class Image(val source: PixaImageSource) : MediaContainerSource()
    data class Custom(val content: @Composable BoxScope.() -> Unit) : MediaContainerSource()
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

/**
 * The Matte + Scrim color pair from the spec's anatomy.
 * [scrim] is intentionally not theme-adaptive — see [resolveMediaContainerColors].
 */
@Immutable
@Stable
data class MediaContainerColors(
    val matte: Color,
    val scrim: Color
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/** Default tolerance for [MediaContainerFillType.Auto] — the spec's "default tolerance is 75%". */
private const val AutoFillDefaultTolerance = 0.75f

/**
 * Fraction of the scrim's radius left transparent at the center, so the gradient only tones the
 * edges. The source spec doesn't give this numerically ("leaves a percentage... clear"), so this is
 * an approximation tuned to stay subtle; not exposed publicly since the spec discourages overriding
 * scrim opacity.
 */
private const val ScrimClearFraction = 0.55f

/** Spec: "images at 80x80 and larger" use the larger radius. */
private val MediaContainerLargeThreshold = 80.dp

/** Spec: "the opacity is lowered" for disabled images — matches PixaAvatar's disabled image opacity. */
private const val DisabledMediaAlpha = 0.4f

/**
 * Matte adapts between light/dark (a themed token); the scrim does not — the spec states it
 * "remains the same across light and dark mode" while only the matte "adjusts between modes". A
 * theme-adaptive color can't satisfy "same across modes" by definition, so the scrim is a literal
 * white with fixed alpha instead — the same pattern Drawer/Dialog/Sheet/Popover/FullScreenModal
 * already use for their own scrims (`Color.Black.copy(alpha = 0.5f)`), just white per "a light
 * radial scrim".
 */
@Composable
private fun resolveMediaContainerColors(): MediaContainerColors = MediaContainerColors(
    matte = AppTheme.colors.baseSurfaceSubtle,
    scrim = Color.White.copy(alpha = 0.28f)
)

/** Spec: 16dp radius for containers >=80x80, 8dp below — [AppTheme.shapes.rounded] already holds both. */
@Composable
private fun mediaContainerShapeFor(size: DpSize?): Shape {
    val qualifiesForLargeRadius = size != null &&
        size.width >= MediaContainerLargeThreshold &&
        size.height >= MediaContainerLargeThreshold
    return if (qualifiesForLargeRadius) AppTheme.shapes.rounded.extraLarge else AppTheme.shapes.rounded.medium
}

/** A light radial vignette — transparent center, [color] at the rim. Not exposed for customization. */
private fun mediaContainerScrimBrush(color: Color): Brush = Brush.radialGradient(
    colorStops = arrayOf(
        0f to Color.Transparent,
        ScrimClearFraction to Color.Transparent,
        1f to color
    )
)

/** Fully desaturates content behind it — the "images are desaturated" half of the disabled state. */
private fun Modifier.mediaContainerDesaturate(disabled: Boolean): Modifier =
    if (!disabled) this else this.drawWithContent {
        val grayscalePaint = Paint().apply {
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
        }
        drawIntoCanvas { canvas ->
            canvas.saveLayer(Rect(Offset.Zero, size), grayscalePaint)
            drawContent()
            canvas.restore()
        }
    }

/**
 * Resolves the effective [ContentScale] for [MediaContainerFillType.Auto]. URL/local-resource image
 * sources expose an intrinsic size to compare against [containerRatio] within [tolerance];
 * everything else (vector/SVG "authored artwork", or a caller-supplied [MediaContainerSource.Custom]
 * slot) has no source ratio to compare, so it stays [ContentScale.Fit] — the safe, detail-preserving
 * default.
 *
 * Note: for [PixaImageSource.Url], this reads intrinsic size via its own [rememberAsyncImagePainter]
 * request, separate from the one [com.pixamob.pixacompose.components.display.PixaImage] issues to actually render the image. Coil's memory/disk
 * cache means this is not a second network fetch in practice, but it is a known duplication — the
 * spec's tolerance rule needs the ratio before the image is placed, and [com.pixamob.pixacompose.components.display.PixaImage] doesn't expose it.
 */
@Composable
private fun rememberAutoContentScale(
    source: MediaContainerSource,
    containerRatio: Float,
    tolerance: Float
): ContentScale {
    val intrinsic = when (source) {
        is MediaContainerSource.Image -> when (val imageSource = source.source) {
            is PixaImageSource.Url -> rememberAsyncImagePainter(model = imageSource.url).intrinsicSize
            is PixaImageSource.Resource -> imageSource.painter.intrinsicSize
            is PixaImageSource.DrawableResource -> painterResource(imageSource.drawableResource).intrinsicSize
            else -> Size.Unspecified // authored artwork (vector/SVG) — no source ratio to compare
        }

        is MediaContainerSource.Custom -> Size.Unspecified
    }

    if (intrinsic == Size.Unspecified || intrinsic.width <= 0f || intrinsic.height <= 0f) {
        return ContentScale.Fit
    }

    val sourceRatio = intrinsic.width / intrinsic.height
    val withinTolerance = abs(sourceRatio - containerRatio) / containerRatio <= tolerance
    return if (withinTolerance) ContentScale.Crop else ContentScale.Fit
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun BoxScope.MediaContainerContent(
    source: MediaContainerSource,
    contentDescription: String?,
    contentScale: ContentScale,
    alignment: Alignment
) {
    when (source) {
        is MediaContainerSource.Image -> PixaImage(
            source = source.source,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale,
            ratio = PixaImageRatio.Original, // framing already applied by the container itself
            alignment = alignment
        )

        is MediaContainerSource.Custom -> source.content(this)
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaMediaContainer — a framed surface for still and animated media, migrated from eBay Playbook's
 * Media Container spec.
 *
 * ### Purpose
 * "Displays a variety of visual content" — images today via [MediaContainerSource.Image]; video,
 * GIF, 3D, and Rive content via [MediaContainerSource.Custom] since PixaCompose has no built-in
 * renderer for those yet (see that type's doc).
 *
 * ### Anatomy
 * Matte (background, adapts light/dark) → Media (the content) → Scrim (a toggleable light radial
 * overlay, constant across light/dark mode).
 *
 * ### Variants
 * [ratio]: one of the four approved [MediaContainerRatio] values — the spec calls arbitrary ratios
 * out as introducing inconsistency, so this container does not accept a free-form ratio.
 * [fillType]: [MediaContainerFillType.Fill] (default), [MediaContainerFillType.Fit], or
 * [MediaContainerFillType.Auto] (fills within [autoFillTolerance] of the container ratio, else Fit).
 *
 * ### States
 * [enabled] = false desaturates and dims the media layer (40% opacity, matching the same disabled
 * treatment [com.pixamob.pixacompose.components.display.PixaAvatar] already uses for images) — the spec's "disabled images are desaturated and
 * the opacity is lowered".
 *
 * ### Sizing
 * Corner radius is not a caller-facing parameter: the container measures itself and applies
 * [AppTheme.shapes.rounded]'s extraLarge (16dp) tier at 80x80 and above, medium (8dp) tier below —
 * exactly the spec's threshold. (Customization boundary: the spec explicitly discourages border
 * radius overrides.)
 *
 * ### Customization
 * [scrim] toggles the overlay on/off — its color/opacity is not exposed, per the spec's "don't
 * override scrim opacity". [onClick] is a Pixa-standard affordance ([PixaImage] carries the same
 * optional hook); the source spec does not document interaction behavior for this component.
 *
 * ### Usage notes
 * - Use fill whenever possible; only fit when the ratio would otherwise crop important detail.
 * - Keep the scrim on to hold a consistent shape across collections/grids of mixed content.
 *
 * @param source What to render in the Media layer
 * @param modifier Modifier for the container
 * @param ratio Container ratio (Default: [MediaContainerRatio.Square])
 * @param fillType Fill/Fit/Auto (Default: [MediaContainerFillType.Fill])
 * @param autoFillTolerance Ratio-closeness tolerance for [MediaContainerFillType.Auto] (Default: 0.75f, the spec's "75% of the container")
 * @param scrim Whether the radial scrim overlay is shown (Default: true)
 * @param enabled Disabled desaturates + dims the media layer (Default: true)
 * @param contentDescription Accessibility description, forwarded to an [MediaContainerSource.Image] source
 * @param alignment Content alignment / focal point when cropping
 * @param onClick Optional tap handler
 */
@Composable
fun PixaMediaContainer(
    source: MediaContainerSource,
    modifier: Modifier = Modifier,
    ratio: MediaContainerRatio = MediaContainerRatio.Square,
    fillType: MediaContainerFillType = MediaContainerFillType.Fill,
    autoFillTolerance: Float = AutoFillDefaultTolerance,
    scrim: Boolean = true,
    enabled: Boolean = true,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    onClick: (() -> Unit)? = null
) {
    val colors = resolveMediaContainerColors()
    val density = LocalDensity.current
    var measuredSize by remember { mutableStateOf<DpSize?>(null) }
    val shape = mediaContainerShapeFor(measuredSize)

    val contentScale = when (fillType) {
        MediaContainerFillType.Fill -> ContentScale.Crop
        MediaContainerFillType.Fit -> ContentScale.Fit
        MediaContainerFillType.Auto -> rememberAutoContentScale(source, ratio.value, autoFillTolerance)
    }

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = pixaRipple(bounded = true),
            enabled = enabled,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .aspectRatio(ratio.value)
            .onSizeChanged { sizePx ->
                with(density) { measuredSize = DpSize(sizePx.width.toDp(), sizePx.height.toDp()) }
            }
            .clip(shape)
            .background(colors.matte)
            .then(clickModifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .mediaContainerDesaturate(!enabled)
                .alpha(if (enabled) 1f else DisabledMediaAlpha)
        ) {
            MediaContainerContent(
                source = source,
                contentDescription = contentDescription,
                contentScale = contentScale,
                alignment = alignment
            )
        }

        if (scrim) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(mediaContainerScrimBrush(colors.scrim))
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/** Remote image by URL — the common case, forwarded as a [MediaContainerSource.Image]. */
@Composable
fun PixaMediaContainer(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    ratio: MediaContainerRatio = MediaContainerRatio.Square,
    fillType: MediaContainerFillType = MediaContainerFillType.Fill,
    autoFillTolerance: Float = AutoFillDefaultTolerance,
    scrim: Boolean = true,
    enabled: Boolean = true,
    alignment: Alignment = Alignment.Center,
    onClick: (() -> Unit)? = null
) {
    PixaMediaContainer(
        source = MediaContainerSource.Image(PixaImageSource.Url(url)),
        modifier = modifier,
        ratio = ratio,
        fillType = fillType,
        autoFillTolerance = autoFillTolerance,
        scrim = scrim,
        enabled = enabled,
        contentDescription = contentDescription,
        alignment = alignment,
        onClick = onClick
    )
}
