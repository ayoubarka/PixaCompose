package com.pixamob.pixacompose.components.display

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter

/**
 * PixaIcon - A unified, powerful icon component for Compose Multiplatform
 *
 * **Features:**
 * - Single entry point for all icon types (ImageVector, Painter, URL)
 * - Theme-aware tinting with LocalContentColor by default
 * - Optional animation (scale + fade on appearance)
 * - URL loading with placeholder and error states via Coil3
 * - Full accessibility support with contentDescription
 * - Multiplatform compatible (Android, iOS)
 * - Zero Material 3 conflicts (uses Image/AsyncImage directly)
 *
 * **Icon Sources:**
 * - [IconSource.Vector] - Compose ImageVector (e.g., Icons.Default.Home)
 * - [IconSource.Resource] - Painter from resources or custom painters
 * - [IconSource.Url] - Remote image loaded asynchronously
 *
 * @param source The icon source (Vector, Resource, or Url)
 * @param contentDescription Accessibility description. **Required for accessibility** (nullable but logged if null)
 * @param modifier Modifier for the icon container
 * @param tint Tint color. Uses LocalContentColor by default. Pass null for no tint (original colors)
 * @param size Icon size in Dp
 * @param animation Enable scale+fade animation on appearance
 * @param placeholder Placeholder painter shown while loading URL icons
 * @param error Error painter shown if URL loading fails
 * @param contentScale How to scale the icon content (default: Fit)
 *
 * @sample
 * ```
 * // Vector icon with animation
 * PixaIcon(
 *     source = IconSource.Vector(Icons.Default.Home),
 *     contentDescription = "Home",
 *     tint = Color.Blue,
 *     size = 32.dp,
 *     animation = true
 * )
 *
 * // Painter icon (resource)
 * PixaIcon(
 *     source = IconSource.Resource(painterResource(R.drawable.logo)),
 *     contentDescription = "App Logo",
 *     tint = null // preserve original colors
 * )
 *
 * // URL icon with error handling
 * PixaIcon(
 *     source = IconSource.Url("https://example.com/icon.png"),
 *     contentDescription = "Remote Logo",
 *     placeholder = painterResource(R.drawable.placeholder),
 *     error = painterResource(R.drawable.error),
 *     size = 48.dp
 * )
 * ```
 */
@Composable
fun PixaIcon(
    source: IconSource,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = LocalContentColor.current,
    size: Dp = 24.dp,
    animation: Boolean = false,
    placeholder: Painter? = null,
    error: Painter? = null,
    contentScale: ContentScale = ContentScale.Fit
) {
    // Warn if contentDescription is null (accessibility issue)
    LaunchedEffect(contentDescription) {
        if (contentDescription == null) {
            println("⚠️ PixaIcon: contentDescription is null. This may impact accessibility.")
        }
    }

    // Animation state
    var isVisible by remember { mutableStateOf(!animation) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "iconAlpha"
    )

    LaunchedEffect(Unit) {
        if (animation) {
            isVisible = true
        }
    }

    val animationModifier = if (animation) {
        modifier
            .size(size)
            .scale(scale)
            .alpha(alpha)
    } else {
        modifier.size(size)
    }

    when (source) {
        is IconSource.Vector -> {
            VectorIcon(
                imageVector = source.imageVector,
                contentDescription = contentDescription,
                modifier = animationModifier,
                tint = tint,
                contentScale = contentScale
            )
        }

        is IconSource.Resource -> {
            PainterIcon(
                painter = source.painter,
                contentDescription = contentDescription,
                modifier = animationModifier,
                tint = tint,
                contentScale = contentScale
            )
        }

        is IconSource.Url -> {
            UrlIcon(
                url = source.url,
                contentDescription = contentDescription,
                modifier = animationModifier,
                tint = tint,
                placeholder = placeholder,
                error = error,
                contentScale = contentScale
            )
        }
    }
}

// ============================================================================
// ICON SOURCE TYPES
// ============================================================================

/**
 * Sealed class representing different icon source types
 */
@Stable
sealed class IconSource {
    /**
     * ImageVector source (e.g., Icons.Default.Home)
     */
    @Stable
    data class Vector(val imageVector: ImageVector) : IconSource()

    /**
     * Painter resource (e.g., painterResource, rememberImagePainter)
     */
    @Stable
    data class Resource(val painter: Painter) : IconSource()

    /**
     * Remote URL loaded via Coil
     */
    @Stable
    data class Url(val url: String) : IconSource()
}

// ============================================================================
// INTERNAL ICON RENDERERS
// ============================================================================

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
        // Show error painter
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

// ============================================================================
// CONVENIENCE FUNCTIONS
// ============================================================================

/**
 * PixaIcon from ImageVector - Convenience function
 */
@Composable
fun PixaIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = LocalContentColor.current,
    size: Dp = 24.dp,
    animation: Boolean = false
) {
    PixaIcon(
        source = IconSource.Vector(imageVector),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        size = size,
        animation = animation
    )
}

/**
 * PixaIcon from Painter - Convenience function
 */
@Composable
fun PixaIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = LocalContentColor.current,
    size: Dp = 24.dp,
    animation: Boolean = false
) {
    PixaIcon(
        source = IconSource.Resource(painter),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        size = size,
        animation = animation
    )
}

/**
 * PixaIcon from URL - Convenience function
 */
@Composable
fun PixaIcon(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = null,
    size: Dp = 24.dp,
    animation: Boolean = false,
    placeholder: Painter? = null,
    error: Painter? = null
) {
    PixaIcon(
        source = IconSource.Url(url),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        size = size,
        animation = animation,
        placeholder = placeholder,
        error = error
    )
}

// ============================================================================
// USAGE EXAMPLES
// ============================================================================

/**
 * USAGE EXAMPLES:
 *
 * ### 1. Vector Icon (Basic)
 * ```
 * PixaIcon(
 *     source = IconSource.Vector(Icons.Default.Home),
 *     contentDescription = "Home"
 * )
 * ```
 *
 * ### 2. Vector Icon with Animation & Custom Tint
 * ```
 * PixaIcon(
 *     source = IconSource.Vector(Icons.Filled.Favorite),
 *     contentDescription = "Favorite",
 *     tint = Color.Red,
 *     size = 32.dp,
 *     animation = true
 * )
 * ```
 *
 * ### 3. Painter Resource Icon
 * ```
 * PixaIcon(
 *     source = IconSource.Resource(painterResource(R.drawable.logo)),
 *     contentDescription = "App Logo",
 *     tint = null, // Preserve original colors
 *     size = 40.dp
 * )
 * ```
 *
 * ### 4. URL Icon with Placeholder & Error
 * ```
 * PixaIcon(
 *     source = IconSource.Url("https://example.com/icon.png"),
 *     contentDescription = "Remote Logo",
 *     placeholder = painterResource(R.drawable.placeholder),
 *     error = painterResource(R.drawable.error),
 *     size = 48.dp,
 *     animation = true
 * )
 * ```
 *
 * ### 5. Theme-Aware Icon (default LocalContentColor)
 * ```
 * PixaIcon(
 *     source = IconSource.Vector(Icons.Default.Settings),
 *     contentDescription = "Settings",
 *     // tint will automatically use LocalContentColor
 *     size = 20.dp
 * )
 * ```
 *
 * ### 6. Convenience Function (ImageVector)
 * ```
 * PixaIcon(
 *     imageVector = Icons.Default.Search,
 *     contentDescription = "Search",
 *     tint = Color.Blue,
 *     animation = true
 * )
 * ```
 *
 * ### 7. Convenience Function (Painter)
 * ```
 * PixaIcon(
 *     painter = painterResource(R.drawable.custom_icon),
 *     contentDescription = "Custom",
 *     size = 24.dp
 * )
 * ```
 *
 * ### 8. Convenience Function (URL)
 * ```
 * PixaIcon(
 *     url = "https://cdn.example.com/avatar.png",
 *     contentDescription = "User Avatar",
 *     placeholder = painterResource(R.drawable.avatar_placeholder),
 *     error = painterResource(R.drawable.avatar_error)
 * )
 * ```
 */

