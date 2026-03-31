package com.pixamob.pixacompose.utils

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Screen Utilities
 *
 * Provides utility functions for accessing screen dimensions and
 * performing screen-related calculations in a Composable context.
 *
 * **Important:** Wrap your root composable with [ScreenSizeProvider] so
 * that [getScreenHeight] and [getScreenWidth] return the correct values.
 *
 * ## Usage Examples
 *
 * ```kotlin
 * @Composable
 * fun App() {
 *     ScreenSizeProvider {
 *         MyScreen()
 *     }
 * }
 *
 * @Composable
 * fun MyScreen() {
 *     val screenHeight = ScreenUtil.getScreenHeight()
 *     val screenWidth = ScreenUtil.getScreenWidth()
 *
 *     Box(
 *         modifier = Modifier
 *             .height(screenHeight * 0.5f)
 *             .width(screenWidth)
 *     ) {
 *         // Content
 *     }
 * }
 * ```
 */
object ScreenUtil {

    /**
     * Get the screen height in Dp
     *
     * @return The current screen height as Dp
     */
    @Composable
    fun getScreenHeight(): Dp {
        return LocalScreenSize.current.height
    }

    /**
     * Get the screen width in Dp
     *
     * @return The current screen width as Dp
     */
    @Composable
    fun getScreenWidth(): Dp {
        return LocalScreenSize.current.width
    }

    /**
     * Get the screen height in pixels
     *
     * @return The current screen height in pixels
     */
    @Composable
    fun getScreenHeightPx(): Float {
        val density = LocalDensity.current
        return with(density) { getScreenHeight().toPx() }
    }

    /**
     * Get the screen width in pixels
     *
     * @return The current screen width in pixels
     */
    @Composable
    fun getScreenWidthPx(): Float {
        val density = LocalDensity.current
        return with(density) { getScreenWidth().toPx() }
    }

    /**
     * Calculate a percentage of screen height
     *
     * @param percentage The percentage (0.0 to 1.0)
     * @return The calculated height as Dp
     */
    @Composable
    fun percentOfHeight(percentage: Float): Dp {
        return getScreenHeight() * percentage
    }

    /**
     * Calculate a percentage of screen width
     *
     * @param percentage The percentage (0.0 to 1.0)
     * @return The calculated width as Dp
     */
    @Composable
    fun percentOfWidth(percentage: Float): Dp {
        return getScreenWidth() * percentage
    }

    /**
     * Get the screen aspect ratio (width / height)
     *
     * @return The aspect ratio as a Float
     */
    @Composable
    fun getAspectRatio(): Float {
        return getScreenWidth().value / getScreenHeight().value
    }

    /**
     * Check if the screen is in landscape orientation
     *
     * @return True if width > height
     */
    @Composable
    fun isLandscape(): Boolean {
        return getScreenWidth() > getScreenHeight()
    }

    /**
     * Check if the screen is in portrait orientation
     *
     * @return True if height >= width
     */
    @Composable
    fun isPortrait(): Boolean {
        return getScreenHeight() >= getScreenWidth()
    }

    /**
     * Get the smaller dimension of the screen
     *
     * @return The smaller of width or height
     */
    @Composable
    fun getSmallestDimension(): Dp {
        return minOf(getScreenWidth(), getScreenHeight())
    }

    /**
     * Get the larger dimension of the screen
     *
     * @return The larger of width or height
     */
    @Composable
    fun getLargestDimension(): Dp {
        return maxOf(getScreenWidth(), getScreenHeight())
    }
}

/**
 * Data class representing screen size
 */
data class ScreenSize(
    val width: Dp,
    val height: Dp
)

/**
 * Composition local for screen size.
 * Provided by [ScreenSizeProvider]. If not provided, defaults to 0.dp.
 */
val LocalScreenSize = compositionLocalOf {
    ScreenSize(width = 0.dp, height = 0.dp)
}

/**
 * Provider composable that measures available space and provides screen size
 * to all children via [LocalScreenSize].
 *
 * Uses [SubcomposeLayout] so the dimensions are available **synchronously**
 * on the very first composition frame — no "0 on first frame" problem.
 *
 * Place this **once** at (or near) the root of your composable tree.
 *
 * @param content The composable content that will have access to screen size
 *
 * @sample
 * ```
 * @Composable
 * fun App() {
 *     ScreenSizeProvider {
 *         val width = ScreenUtil.getScreenWidth()
 *         MyScreen()
 *     }
 * }
 * ```
 */
@Composable
fun ScreenSizeProvider(
    content: @Composable () -> Unit
) {
    SubcomposeLayout(modifier = Modifier.fillMaxSize()) { constraints ->
        val width = with(density) { constraints.maxWidth.toDp() }
        val height = with(density) { constraints.maxHeight.toDp() }

        val measurables = subcompose("screen_content") {
            androidx.compose.runtime.CompositionLocalProvider(
                LocalScreenSize provides ScreenSize(width = width, height = height)
            ) {
                content()
            }
        }

        val placeables = measurables.map { it.measure(constraints) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEach { it.placeRelative(0, 0) }
        }
    }
}

// ============================================================================
// EXTENSION FUNCTIONS
// ============================================================================

/**
 * Extension function to get screen height from any Composable context
 */
@Composable
fun getScreenHeight(): Dp = ScreenUtil.getScreenHeight()

/**
 * Extension function to get screen width from any Composable context
 */
@Composable
fun getScreenWidth(): Dp = ScreenUtil.getScreenWidth()
