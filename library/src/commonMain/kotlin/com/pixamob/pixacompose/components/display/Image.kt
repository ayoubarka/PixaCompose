package com.pixamob.pixacompose.components.display

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.pixamob.library.generated.resources.Res
import com.valentinilk.shimmer.shimmer
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Stable
sealed class PixaImageSource {
    data class Url(val url: String) : PixaImageSource()
    data class Resource(val painter: Painter) : PixaImageSource()
    data class Vector(val imageVector: ImageVector) : PixaImageSource()
    data class SvgPath(
        val pathData: String,
        val viewportWidth: Float = 24f,
        val viewportHeight: Float = 24f,
        val defaultWidth: Dp = 24.dp,
        val defaultHeight: Dp = 24.dp
    ) : PixaImageSource()
    data class SvgFile(val filePath: String) : PixaImageSource()
    data class DrawableResource(val drawableResource: org.jetbrains.compose.resources.DrawableResource) : PixaImageSource()
}

// ════════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * @param source The image source (Url, Resource, Vector, SvgPath, SvgFile, or DrawableResource)
 * @param contentDescription Accessibility description
 * @param modifier Modifier (controls size, shape, padding, etc.)
 * @param contentScale How to scale the content
 * @param shape Shape to clip the image
 * @param size Fixed size (convenience parameter)
 * @param tint Tint color
 * @param loadingPlaceholder Custom loading placeholder
 * @param errorFallback Custom error fallback
 * @param brokenImageIcon Custom broken image icon
 * @param onClick Click handler
 * @param crossfade Enable crossfade animation
 * @param backgroundColor Background color
 * */
@Composable
fun PixaImage(
    source: PixaImageSource,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RectangleShape,
    size: Dp? = null,
    tint: Color? = null,
    loadingPlaceholder: Painter? = null,
    errorFallback: Painter? = null,
    brokenImageIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    crossfade: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    alignment: Alignment = Alignment.Center
) {
    // Accessibility warning
    LaunchedEffect(contentDescription) {
        if (contentDescription == null) {
            println("⚠️ PixaImage: contentDescription is null. This impacts accessibility. Source: $source")
        }
    }

    // Semantic annotations for accessibility
    val semanticsModifier = Modifier.semantics {
        contentDescription?.let { this.contentDescription = it }
        role = Role.Image
    }

    // Size handling
    val sizeModifier = size?.let { Modifier.size(it) } ?: Modifier

    // Click handling with ripple effect (mobile-friendly)
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(),
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(sizeModifier)
            .clip(shape)
            .background(backgroundColor)
            .then(clickModifier)
            .then(semanticsModifier),
        contentAlignment = alignment
    ) {
        when (source) {
            is PixaImageSource.Url -> {
                UrlImageRenderer(
                    url = source.url,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    tint = tint,
                    loadingPlaceholder = loadingPlaceholder,
                    errorFallback = errorFallback,
                    brokenImageIcon = brokenImageIcon,
                    crossfade = crossfade,
                    alignment = alignment
                )
            }

            is PixaImageSource.Resource -> {
                ResourceImageRenderer(
                    painter = source.painter,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    tint = tint,
                    alignment = alignment
                )
            }

            is PixaImageSource.Vector -> {
                VectorImageRenderer(
                    imageVector = source.imageVector,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    tint = tint,
                    contentScale = contentScale,
                    alignment = alignment
                )
            }

            is PixaImageSource.SvgPath -> {
                SvgPathRenderer(
                    pathData = source.pathData,
                    viewportWidth = source.viewportWidth,
                    viewportHeight = source.viewportHeight,
                    defaultWidth = source.defaultWidth,
                    defaultHeight = source.defaultHeight,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    tint = tint,
                    contentScale = contentScale,
                    alignment = alignment
                )
            }

            is PixaImageSource.SvgFile -> {
                SvgFileRenderer(
                    filePath = source.filePath,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    tint = tint,
                    alignment = alignment
                )
            }

            is PixaImageSource.DrawableResource -> {
                DrawableResourceRenderer(
                    drawableResource = source.drawableResource,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    tint = tint,
                    alignment = alignment
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL COMPONENTS
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun UrlImageRenderer(
    url: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale,
    tint: Color?,
    loadingPlaceholder: Painter?,
    errorFallback: Painter?,
    brokenImageIcon: ImageVector?,
    @Suppress("UNUSED_PARAMETER") crossfade: Boolean,
    alignment: Alignment
) {
    var loadState by remember(url) { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Main async image
        AsyncImage(
            model = url,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale,
            colorFilter = tint?.let { ColorFilter.tint(it) },
            alignment = alignment,
            onState = { state -> loadState = state }
        )

        // Overlay loading/error states
        when (loadState) {
            is AsyncImagePainter.State.Loading -> {
                if (loadingPlaceholder != null) {
                    Image(
                        painter = loadingPlaceholder,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = contentScale,
                        alignment = alignment
                    )
                } else {
                    ShimmerLoadingBox(modifier = Modifier.fillMaxSize())
                }
            }

            is AsyncImagePainter.State.Error -> {
                if (errorFallback != null) {
                    Image(
                        painter = errorFallback,
                        contentDescription = "Error loading image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        alignment = alignment
                    )
                } else {
                    DefaultErrorIndicator(
                        modifier = Modifier.fillMaxSize(),
                        brokenImageIcon = brokenImageIcon
                    )
                }
            }

            is AsyncImagePainter.State.Success,
            is AsyncImagePainter.State.Empty -> {
                // Image loaded successfully or empty state
            }
        }
    }
}

@Composable
private fun ResourceImageRenderer(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale,
    tint: Color?,
    alignment: Alignment
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = tint?.let { ColorFilter.tint(it) },
        alignment = alignment
    )
}

@Composable
private fun VectorImageRenderer(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier,
    tint: Color?,
    contentScale: ContentScale,
    alignment: Alignment
) {
    val painter = rememberVectorPainter(imageVector)
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = tint?.let { ColorFilter.tint(it) },
        alignment = alignment
    )
}

@Composable
private fun SvgPathRenderer(
    pathData: String,
    viewportWidth: Float,
    viewportHeight: Float,
    defaultWidth: Dp,
    defaultHeight: Dp,
    contentDescription: String?,
    modifier: Modifier,
    tint: Color?,
    contentScale: ContentScale,
    alignment: Alignment
) {
    val imageVector = remember(pathData, viewportWidth, viewportHeight, defaultWidth, defaultHeight) {
        createImageVectorFromPath(
            pathData = pathData,
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
            defaultWidth = defaultWidth,
            defaultHeight = defaultHeight
        )
    }

    val painter = rememberVectorPainter(imageVector)
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = tint?.let { ColorFilter.tint(it) },
        alignment = alignment
    )
}

/**
 * Renders SVG files from composeResources/files/ using Coil's AsyncImage with automatic SVG detection.
 *
 * Coil 3.3.0+ automatically detects and decodes SVG files by looking for the `<svg` marker
 * in the first 1KB of the file. The coil-svg library must be included in dependencies.
 *
 * Implementation based on: https://coil-kt.github.io/coil/svgs/
 *
 * File structure:
 *   composeResources/files/icons/faces/ic_face_happy.svg
 *
 * Usage:
 *   PixaImage(
 *       source = PixaImageSource.SvgFile("icons/faces/ic_face_happy.svg"),
 *       contentDescription = "Happy face",
 *       modifier = Modifier.size(48.dp),
 *       tint = Color.Blue
 *   )
 *
 * Note: Size is controlled purely by the modifier, not by the SVG's internal dimensions.
 */
@Composable
private fun SvgFileRenderer(
    filePath: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale,
    tint: Color?,
    alignment: Alignment
) {
    val uri = Res.getUri(filePath)

    // Coil AsyncImage with automatic SVG detection (coil-svg library)
    // The ImageLoader automatically detects SVGs by looking for <svg marker
    AsyncImage(
        model = uri,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = tint?.let { ColorFilter.tint(it) },
        alignment = alignment
    )
}

@Composable
private fun DrawableResourceRenderer(
    drawableResource: DrawableResource,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale,
    tint: Color?,
    alignment: Alignment
) {
    val painter = painterResource(drawableResource)

    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = tint?.let { ColorFilter.tint(it) },
        alignment = alignment
    )
}

private fun createImageVectorFromPath(
    pathData: String,
    viewportWidth: Float,
    viewportHeight: Float,
    defaultWidth: Dp,
    defaultHeight: Dp
): ImageVector {
    return ImageVector.Builder(
        name = "SvgPath",
        defaultWidth = defaultWidth,
        defaultHeight = defaultHeight,
        viewportWidth = viewportWidth,
        viewportHeight = viewportHeight
    ).apply {
        addPath(
            pathData = PathParser().parsePathString(pathData).toNodes(),
            fill = SolidColor(Color.Black)
        )
    }.build()
}

// ============================================================================
// LOADING & ERROR INDICATORS
// ============================================================================

@Composable
private fun ShimmerLoadingBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shimmer()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
    )
}

@Composable
private fun DefaultErrorIndicator(
    modifier: Modifier = Modifier,
    brokenImageIcon: ImageVector? = null
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = brokenImageIcon ?: rememberBrokenImageVector(),
            contentDescription = "Failed to load image",
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun rememberBrokenImageVector(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "BrokenImage",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            addPath(
                pathData = PathParser()
                    .parsePathString(
                        "M19,3 L5,3 C3.9,3 3,3.9 3,5 L3,19 " +
                                "C3,20.1 3.9,21 5,21 L19,21 " +
                                "C20.1,21 21,20.1 21,19 L21,5 " +
                                "C21,3.9 20.1,3 19,3 Z " +
                                "M19,19 L5,19 L5,5 L19,5 L19,19 Z " +
                                "M15,14 L11,10 L13,8 L17,12 Z " +
                                "M7,10 L11,14 L9,16 L5,12 Z"
                    ).toNodes(),
                fill = SolidColor(Color.Black)
            )
        }.build()
    }
}

// ============================================================================
// CONVENIENCE FUNCTIONS
// ============================================================================

@Composable
fun PixaImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RectangleShape,
    size: Dp? = null,
    loadingPlaceholder: Painter? = null,
    errorFallback: Painter? = null,
    onClick: (() -> Unit)? = null,
    crossfade: Boolean = true,
    backgroundColor: Color = Color.Transparent
) {
    PixaImage(
        source = PixaImageSource.Url(url),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        shape = shape,
        size = size,
        loadingPlaceholder = loadingPlaceholder,
        errorFallback = errorFallback,
        onClick = onClick,
        crossfade = crossfade,
        backgroundColor = backgroundColor
    )
}

@Composable
fun PixaImage(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RectangleShape,
    size: Dp? = null,
    tint: Color? = null,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = Color.Transparent
) {
    PixaImage(
        source = PixaImageSource.Resource(painter),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        shape = shape,
        size = size,
        tint = tint,
        onClick = onClick,
        backgroundColor = backgroundColor
    )
}

@Composable
fun PixaImage(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color? = null,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = Color.Transparent,
    contentScale: ContentScale = ContentScale.Fit
) {
    PixaImage(
        source = PixaImageSource.Vector(imageVector),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        size = size,
        tint = tint,
        onClick = onClick,
        backgroundColor = backgroundColor
    )
}

@Composable
fun PixaImage(
    svgPath: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color? = null,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = Color.Transparent,
    contentScale: ContentScale = ContentScale.Fit,
    viewportWidth: Float = 24f,
    viewportHeight: Float = 24f
) {
    PixaImage(
        source = PixaImageSource.SvgPath(
            pathData = svgPath,
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
            defaultWidth = size,
            defaultHeight = size
        ),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        size = size,
        tint = tint,
        onClick = onClick,
        backgroundColor = backgroundColor
    )
}

/**
 * PixaImage from SVG file path - Convenience function
 *
 * Uses Coil 3.3.0's automatic SVG detection and decoding.
 * Requires coil-svg library dependency.
 *
 * The SVG is loaded from composeResources/files/ directory.
 * Size controlled by modifier only.
 *
 * Example:
 *   PixaImage(
 *       svgFilePath = "icons/logo.svg",
 *       contentDescription = "App logo",
 *       modifier = Modifier.size(64.dp),
 *       tint = MaterialTheme.colorScheme.primary
 *   )
 */
@Composable
fun PixaImage(
    svgFilePath: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = null,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = Color.Transparent,
    contentScale: ContentScale = ContentScale.Fit
) {
    PixaImage(
        source = PixaImageSource.SvgFile(filePath = svgFilePath),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        tint = tint,
        onClick = onClick,
        backgroundColor = backgroundColor
    )
}

@Composable
fun PixaImage(
    drawableResource: DrawableResource,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = null,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = Color.Transparent,
    contentScale: ContentScale = ContentScale.Fit
) {
    PixaImage(
        source = PixaImageSource.DrawableResource(drawableResource = drawableResource),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        tint = tint,
        onClick = onClick,
        backgroundColor = backgroundColor
    )
}