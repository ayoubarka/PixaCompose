package com.pixamob.pixacompose.components.display

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.compose.material3.Icon as Material3Icon
import androidx.compose.material3.LocalContentColor

/**
 * Icon Component
 *
 * A flexible icon component that supports multiple resource types:
 * - ImageVector (Compose vector graphics)
 * - Painter (e.g., painterResource)
 * - URL (loaded via Coil)
 * - Material 3 Icon wrapper for backward compatibility
 *
 * Features:
 * - Multiple resource type support
 * - Theme-aware default tinting
 * - Configurable size and tint
 * - Async loading for URL-based icons
 * - Full accessibility support
 *
 * @sample
 * ```
 * // Vector icon
 * Icon(
 *     imageVector = Icons.Default.Home,
 *     contentDescription = "Home"
 * )
 *
 * // Painter icon
 * Icon(
 *     painter = painterResource(R.drawable.ic_custom),
 *     contentDescription = "Custom"
 * )
 *
 * // URL icon
 * Icon(
 *     url = "https://example.com/icon.png",
 *     contentDescription = "Remote icon"
 * )
 * ```
 */

// ============================================================================
// ICON VARIANTS
// ============================================================================

/**
 * Icon from ImageVector
 *
 * @param imageVector The ImageVector to display
 * @param contentDescription Accessibility description
 * @param modifier Modifier for the icon
 * @param tint Tint color (null for no tint, uses LocalContentColor by default)
 * @param size Size of the icon
 */
@Composable
fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = LocalContentColor.current,
    size: Dp = 24.dp
) {
    Material3Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        tint = tint ?: Color.Unspecified
    )
}

/**
 * Icon from Painter
 *
 * @param painter The Painter to display
 * @param contentDescription Accessibility description
 * @param modifier Modifier for the icon
 * @param tint Tint color (null for no tint, uses LocalContentColor by default)
 * @param size Size of the icon
 */
@Composable
fun Icon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = LocalContentColor.current,
    size: Dp = 24.dp
) {
    Material3Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        tint = tint ?: Color.Unspecified
    )
}

/**
 * Icon from URL (async loading via Coil)
 *
 * @param url The URL to load the icon from
 * @param contentDescription Accessibility description
 * @param modifier Modifier for the icon
 * @param tint Tint color (null for no tint)
 * @param size Size of the icon
 */
@Composable
fun Icon(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = null,
    size: Dp = 24.dp
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        colorFilter = tint?.let { ColorFilter.tint(it) },
        contentScale = ContentScale.Fit
    )
}

// ============================================================================
// CONVENIENCE FUNCTIONS
// ============================================================================

/**
 * Tinted Icon - Icon with explicit tint color
 *
 * @param imageVector The ImageVector to display
 * @param contentDescription Accessibility description
 * @param tint Tint color
 * @param modifier Modifier for the icon
 * @param size Size of the icon
 */
@Composable
fun TintedIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        size = size
    )
}

/**
 * Tinted Icon from Painter
 *
 * @param painter The Painter to display
 * @param contentDescription Accessibility description
 * @param tint Tint color
 * @param modifier Modifier for the icon
 * @param size Size of the icon
 */
@Composable
fun TintedIcon(
    painter: Painter,
    contentDescription: String?,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        size = size
    )
}

// ============================================================================
// USAGE EXAMPLES
// ============================================================================

/**
 * USAGE EXAMPLES:
 *
 * 1. Simple vector icon:
 * ```
 * Icon(
 *     imageVector = Icons.Default.Home,
 *     contentDescription = "Home"
 * )
 * ```
 *
 * 2. Custom sized icon with tint:
 * ```
 * Icon(
 *     imageVector = Icons.Filled.Favorite,
 *     contentDescription = "Favorite",
 *     tint = Color.Red,
 *     size = 32.dp
 * )
 * ```
 *
 * 3. Painter icon:
 * ```
 * Icon(
 *     painter = painterResource(R.drawable.ic_custom),
 *     contentDescription = "Custom icon",
 *     tint = AppTheme.colors.brandContentDefault
 * )
 * ```
 *
 * 4. URL icon with error handling:
 * ```
 * Icon(
 *     url = "https://example.com/icon.png",
 *     contentDescription = "Remote icon",
 *     tint = Color.Blue,
 *     placeholder = painterResource(R.drawable.ic_placeholder),
 *     error = painterResource(R.drawable.ic_error)
 * )
 * ```
 *
 * 5. Untinted icon (original colors):
 * ```
 * Icon(
 *     painter = painterResource(R.drawable.ic_multicolor),
 *     contentDescription = "Multicolor icon",
 *     tint = null
 * )
 * ```
 *
 * 6. Theme-aware icon:
 * ```
 * Icon(
 *     imageVector = Icons.Default.Settings,
 *     contentDescription = "Settings",
 *     tint = AppTheme.colors.baseContentBody,
 *     size = 20.dp
 * )
 * ```
 */

