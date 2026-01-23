package com.pixamob.pixacompose.theme

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.sin

/**
 * Position enum for shapes with directional configuration
 */
enum class ShapePosition {
    Top, Bottom, Left, Right
}

/**
 * Style enum for notch shapes
 */
enum class NotchStyle {
    Rounded, Sharp
}

/**
 * A shape with a concave (inward) curve.
 *
 * @param position The position of the concave curve (Top, Bottom, Left, Right)
 * @param curveDepth The depth of the curve as a fraction of the dimension (0.0 to 1.0)
 * @param cornerRadius The radius for the remaining corners
 *
 * Example usage:
 * ```
 * Box(
 *     modifier = Modifier
 *         .size(200.dp)
 *         .background(Color.Blue, ConcaveShape(position = ShapePosition.Top, curveDepth = 0.15f))
 * )
 * ```
 */
class ConcaveShape(
    private val position: ShapePosition = ShapePosition.Top,
    private val curveDepth: Float = 0.15f,
    private val cornerRadius: Float = 0f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height

            when (position) {
                ShapePosition.Top -> {
                    val curveHeight = height * curveDepth
                    moveTo(0f, 0f)
                    // Concave curve at top - dips DOWN into content
                    cubicTo(
                        x1 = width * 0.25f, y1 = curveHeight * 2f,
                        x2 = width * 0.75f, y2 = curveHeight * 2f,
                        x3 = width, y3 = 0f
                    )
                    lineTo(width, height - cornerRadius)
                    if (cornerRadius > 0f) {
                        quadraticTo(width, height, width - cornerRadius, height)
                        lineTo(cornerRadius, height)
                        quadraticTo(0f, height, 0f, height - cornerRadius)
                    } else {
                        lineTo(width, height)
                        lineTo(0f, height)
                    }
                    lineTo(0f, 0f)
                }

                ShapePosition.Bottom -> {
                    val curveHeight = height * curveDepth
                    moveTo(cornerRadius, 0f)
                    if (cornerRadius > 0f) {
                        quadraticTo(0f, 0f, 0f, cornerRadius)
                        lineTo(0f, height)
                    } else {
                        lineTo(0f, 0f)
                        lineTo(0f, height)
                    }
                    // Concave curve at bottom - dips UP into content
                    cubicTo(
                        x1 = width * 0.25f, y1 = height - curveHeight * 2f,
                        x2 = width * 0.75f, y2 = height - curveHeight * 2f,
                        x3 = width, y3 = height
                    )
                    lineTo(width, cornerRadius)
                    if (cornerRadius > 0f) {
                        quadraticTo(width, 0f, width - cornerRadius, 0f)
                    } else {
                        lineTo(width, 0f)
                    }
                    lineTo(cornerRadius, 0f)
                }

                ShapePosition.Left -> {
                    val curveWidth = width * curveDepth
                    moveTo(0f, 0f)
                    // Concave curve at left - dips RIGHT into content
                    cubicTo(
                        x1 = curveWidth * 2f, y1 = height * 0.25f,
                        x2 = curveWidth * 2f, y2 = height * 0.75f,
                        x3 = 0f, y3 = height
                    )
                    lineTo(width - cornerRadius, height)
                    if (cornerRadius > 0f) {
                        quadraticTo(width, height, width, height - cornerRadius)
                        lineTo(width, cornerRadius)
                        quadraticTo(width, 0f, width - cornerRadius, 0f)
                    } else {
                        lineTo(width, height)
                        lineTo(width, 0f)
                    }
                    lineTo(0f, 0f)
                }

                ShapePosition.Right -> {
                    val curveWidth = width * curveDepth
                    moveTo(cornerRadius, 0f)
                    if (cornerRadius > 0f) {
                        quadraticTo(0f, 0f, 0f, cornerRadius)
                        lineTo(0f, height - cornerRadius)
                        quadraticTo(0f, height, cornerRadius, height)
                    } else {
                        lineTo(0f, 0f)
                        lineTo(0f, height)
                    }
                    lineTo(width, height)
                    // Concave curve at right - dips LEFT into content
                    cubicTo(
                        x1 = width - curveWidth * 2f, y1 = height * 0.75f,
                        x2 = width - curveWidth * 2f, y2 = height * 0.25f,
                        x3 = width, y3 = 0f
                    )
                    lineTo(cornerRadius, 0f)
                }
            }
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * A shape with a convex (outward bulging) curve.
 *
 * @param position The position of the convex curve (Top, Bottom, Left, Right)
 * @param curveDepth The depth of the outward curve as a fraction (0.0 to 1.0)
 * @param cornerRadius The radius for the remaining corners
 *
 * Example usage:
 * ```
 * Card(
 *     shape = ConvexShape(position = ShapePosition.Bottom, curveDepth = 0.2f),
 *     modifier = Modifier.size(200.dp)
 * ) {
 *     // Content
 * }
 * ```
 */
class ConvexShape(
    private val position: ShapePosition = ShapePosition.Top,
    private val curveDepth: Float = 0.2f,
    private val cornerRadius: Float = 0f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height

            when (position) {
                ShapePosition.Top -> {
                    val curveHeight = height * curveDepth
                    moveTo(0f, curveHeight)
                    // Convex curve at top - bulges UP outward
                    cubicTo(
                        x1 = width * 0.25f, y1 = -curveHeight,
                        x2 = width * 0.75f, y2 = -curveHeight,
                        x3 = width, y3 = curveHeight
                    )
                    lineTo(width, height - cornerRadius)
                    if (cornerRadius > 0f) {
                        quadraticTo(width, height, width - cornerRadius, height)
                        lineTo(cornerRadius, height)
                        quadraticTo(0f, height, 0f, height - cornerRadius)
                    } else {
                        lineTo(width, height)
                        lineTo(0f, height)
                    }
                    lineTo(0f, curveHeight)
                }

                ShapePosition.Bottom -> {
                    moveTo(cornerRadius, 0f)
                    if (cornerRadius > 0f) {
                        quadraticTo(0f, 0f, 0f, cornerRadius)
                        lineTo(0f, height - height * curveDepth)
                    } else {
                        lineTo(0f, 0f)
                        lineTo(0f, height - height * curveDepth)
                    }
                    val curveHeight = height * curveDepth
                    // Convex curve at bottom - bulges DOWN outward
                    cubicTo(
                        x1 = width * 0.25f, y1 = height + curveHeight,
                        x2 = width * 0.75f, y2 = height + curveHeight,
                        x3 = width, y3 = height - curveHeight
                    )
                    lineTo(width, cornerRadius)
                    if (cornerRadius > 0f) {
                        quadraticTo(width, 0f, width - cornerRadius, 0f)
                    } else {
                        lineTo(width, 0f)
                    }
                    lineTo(cornerRadius, 0f)
                }

                ShapePosition.Left -> {
                    val curveWidth = width * curveDepth
                    moveTo(curveWidth, 0f)
                    // Convex curve at left - bulges LEFT outward
                    cubicTo(
                        x1 = -curveWidth, y1 = height * 0.25f,
                        x2 = -curveWidth, y2 = height * 0.75f,
                        x3 = curveWidth, y3 = height
                    )
                    lineTo(width - cornerRadius, height)
                    if (cornerRadius > 0f) {
                        quadraticTo(width, height, width, height - cornerRadius)
                        lineTo(width, cornerRadius)
                        quadraticTo(width, 0f, width - cornerRadius, 0f)
                    } else {
                        lineTo(width, height)
                        lineTo(width, 0f)
                    }
                    lineTo(curveWidth, 0f)
                }

                ShapePosition.Right -> {
                    moveTo(cornerRadius, 0f)
                    if (cornerRadius > 0f) {
                        quadraticTo(0f, 0f, 0f, cornerRadius)
                        lineTo(0f, height - cornerRadius)
                        quadraticTo(0f, height, cornerRadius, height)
                    } else {
                        lineTo(0f, 0f)
                        lineTo(0f, height)
                    }
                    val curveWidth = width * curveDepth
                    lineTo(width - curveWidth, height)
                    // Convex curve at right - bulges RIGHT outward
                    cubicTo(
                        x1 = width + curveWidth, y1 = height * 0.75f,
                        x2 = width + curveWidth, y2 = height * 0.25f,
                        x3 = width - curveWidth, y3 = 0f
                    )
                    lineTo(cornerRadius, 0f)
                }
            }
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * A shape with smooth wave pattern.
 *
 * @param position The position of the wave (Top, Bottom, Left, Right)
 * @param wavelength Number of complete wave cycles (default 2)
 * @param amplitude The height/depth of the wave as a fraction (0.0 to 1.0)
 * @param cornerRadius The radius for corners on non-wave edges
 *
 * Example usage:
 * ```
 * Surface(
 *     shape = WaveShape(position = ShapePosition.Bottom, wavelength = 3, amplitude = 0.1f),
 *     modifier = Modifier.fillMaxWidth().height(100.dp)
 * ) {
 *     // Content
 * }
 * ```
 */
class WaveShape(
    private val position: ShapePosition = ShapePosition.Bottom,
    private val wavelength: Int = 2,
    private val amplitude: Float = 0.1f,
    private val cornerRadius: Float = 0f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height

            when (position) {
                ShapePosition.Top -> {
                    val waveHeight = height * amplitude
                    moveTo(0f, waveHeight)

                    // Create wave pattern at top
                    val segments = wavelength * 20
                    for (i in 0..segments) {
                        val x = (i.toFloat() / segments) * width
                        val y = waveHeight * (1 + sin(i.toFloat() / segments * wavelength * 2 * PI.toFloat()))
                        lineTo(x, y)
                    }

                    lineTo(width, height - cornerRadius)
                    if (cornerRadius > 0f) {
                        quadraticTo(width, height, width - cornerRadius, height)
                        lineTo(cornerRadius, height)
                        quadraticTo(0f, height, 0f, height - cornerRadius)
                    } else {
                        lineTo(width, height)
                        lineTo(0f, height)
                    }
                    lineTo(0f, waveHeight)
                }

                ShapePosition.Bottom -> {
                    moveTo(cornerRadius, 0f)
                    if (cornerRadius > 0f) {
                        quadraticTo(0f, 0f, 0f, cornerRadius)
                    } else {
                        lineTo(0f, 0f)
                    }

                    val waveHeight = height * amplitude
                    val waveStart = height - waveHeight * 2
                    lineTo(0f, waveStart)

                    // Create wave pattern at bottom
                    val segments = wavelength * 20
                    for (i in 0..segments) {
                        val x = (i.toFloat() / segments) * width
                        val y = waveStart + waveHeight * (1 + sin(i.toFloat() / segments * wavelength * 2 * PI.toFloat()))
                        lineTo(x, y)
                    }

                    lineTo(width, cornerRadius)
                    if (cornerRadius > 0f) {
                        quadraticTo(width, 0f, width - cornerRadius, 0f)
                    } else {
                        lineTo(width, 0f)
                    }
                    lineTo(cornerRadius, 0f)
                }

                ShapePosition.Left -> {
                    val waveWidth = width * amplitude
                    moveTo(waveWidth, 0f)

                    // Create wave pattern at left
                    val segments = wavelength * 20
                    for (i in 0..segments) {
                        val y = (i.toFloat() / segments) * height
                        val x = waveWidth * (1 + sin(i.toFloat() / segments * wavelength * 2 * PI.toFloat()))
                        lineTo(x, y)
                    }

                    lineTo(width - cornerRadius, height)
                    if (cornerRadius > 0f) {
                        quadraticTo(width, height, width, height - cornerRadius)
                        lineTo(width, cornerRadius)
                        quadraticTo(width, 0f, width - cornerRadius, 0f)
                    } else {
                        lineTo(width, height)
                        lineTo(width, 0f)
                    }
                    lineTo(waveWidth, 0f)
                }

                ShapePosition.Right -> {
                    moveTo(cornerRadius, 0f)
                    if (cornerRadius > 0f) {
                        quadraticTo(0f, 0f, 0f, cornerRadius)
                        lineTo(0f, height - cornerRadius)
                        quadraticTo(0f, height, cornerRadius, height)
                    } else {
                        lineTo(0f, 0f)
                        lineTo(0f, height)
                    }

                    val waveWidth = width * amplitude
                    val waveStart = width - waveWidth * 2
                    lineTo(waveStart, height)

                    // Create wave pattern at right
                    val segments = wavelength * 20
                    for (i in segments downTo 0) {
                        val y = (i.toFloat() / segments) * height
                        val x = waveStart + waveWidth * (1 + sin(i.toFloat() / segments * wavelength * 2 * PI.toFloat()))
                        lineTo(x, y)
                    }

                    lineTo(cornerRadius, 0f)
                }
            }
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * A shape with arch/dome curve.
 *
 * @param position The position of the arch (Top, Bottom, Left, Right)
 * @param curvature The curvature intensity (0.0 to 1.0, where 0.5 is semicircle)
 * @param cornerRadius The radius for the remaining corners
 *
 * Example usage:
 * ```
 * Box(
 *     modifier = Modifier
 *         .size(200.dp)
 *         .background(Color.Green, ArchShape(position = ShapePosition.Top, curvature = 0.5f))
 * )
 * ```
 */
class ArchShape(
    private val position: ShapePosition = ShapePosition.Top,
    private val curvature: Float = 0.5f,
    private val cornerRadius: Float = 0f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height

            when (position) {
                ShapePosition.Top -> {
                    val archHeight = height * curvature
                    moveTo(0f, archHeight)

                    // Create smooth arch at top using quadratic curve
                    quadraticTo(
                        x1 = width / 2f, y1 = -archHeight,
                        x2 = width, y2 = archHeight
                    )

                    lineTo(width, height - cornerRadius)
                    if (cornerRadius > 0f) {
                        quadraticTo(width, height, width - cornerRadius, height)
                        lineTo(cornerRadius, height)
                        quadraticTo(0f, height, 0f, height - cornerRadius)
                    } else {
                        lineTo(width, height)
                        lineTo(0f, height)
                    }
                    lineTo(0f, archHeight)
                }

                ShapePosition.Bottom -> {
                    moveTo(cornerRadius, 0f)
                    if (cornerRadius > 0f) {
                        quadraticTo(0f, 0f, 0f, cornerRadius)
                    } else {
                        lineTo(0f, 0f)
                    }

                    val archHeight = height * curvature
                    lineTo(0f, height - archHeight)

                    // Create smooth arch at bottom
                    quadraticTo(
                        x1 = width / 2f, y1 = height + archHeight,
                        x2 = width, y2 = height - archHeight
                    )

                    lineTo(width, cornerRadius)
                    if (cornerRadius > 0f) {
                        quadraticTo(width, 0f, width - cornerRadius, 0f)
                    } else {
                        lineTo(width, 0f)
                    }
                    lineTo(cornerRadius, 0f)
                }

                ShapePosition.Left -> {
                    val archWidth = width * curvature
                    moveTo(archWidth, 0f)

                    // Create smooth arch at left
                    quadraticTo(
                        x1 = -archWidth, y1 = height / 2f,
                        x2 = archWidth, y2 = height
                    )

                    lineTo(width - cornerRadius, height)
                    if (cornerRadius > 0f) {
                        quadraticTo(width, height, width, height - cornerRadius)
                        lineTo(width, cornerRadius)
                        quadraticTo(width, 0f, width - cornerRadius, 0f)
                    } else {
                        lineTo(width, height)
                        lineTo(width, 0f)
                    }
                    lineTo(archWidth, 0f)
                }

                ShapePosition.Right -> {
                    moveTo(cornerRadius, 0f)
                    if (cornerRadius > 0f) {
                        quadraticTo(0f, 0f, 0f, cornerRadius)
                        lineTo(0f, height - cornerRadius)
                        quadraticTo(0f, height, cornerRadius, height)
                    } else {
                        lineTo(0f, 0f)
                        lineTo(0f, height)
                    }

                    val archWidth = width * curvature
                    lineTo(width - archWidth, height)

                    // Create smooth arch at right
                    quadraticTo(
                        x1 = width + archWidth, y1 = height / 2f,
                        x2 = width - archWidth, y2 = 0f
                    )

                    lineTo(cornerRadius, 0f)
                }
            }
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * A rounded tab shape (like browser tabs).
 *
 * @param position The position of the tab (Top or Bottom)
 * @param tabWidthFraction The width of the tab as a fraction of total width (0.0 to 1.0)
 * @param tabHeight The height of the tab portion
 * @param cornerRadius The radius for rounded corners
 *
 * Example usage:
 * ```
 * Surface(
 *     shape = TabShape(position = ShapePosition.Top, tabWidthFraction = 0.8f, tabHeight = 40f),
 *     modifier = Modifier.size(150.dp, 80.dp)
 * ) {
 *     Text("Tab Content")
 * }
 * ```
 */
class TabShape(
    private val position: ShapePosition = ShapePosition.Top,
    private val tabWidthFraction: Float = 0.8f,
    private val tabHeight: Float = 40f,
    private val cornerRadius: Float = 16f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height
            val tabWidth = width * tabWidthFraction.coerceIn(0f, 1f)
            val tabStart = (width - tabWidth) / 2f
            val tabEnd = tabStart + tabWidth

            when (position) {
                ShapePosition.Top -> {
                    // Start at bottom-left
                    moveTo(0f, height)
                    lineTo(0f, tabHeight + cornerRadius)

                    // Left vertical edge to tab
                    lineTo(tabStart, tabHeight + cornerRadius)

                    // Left tab corner
                    quadraticTo(
                        x1 = tabStart, y1 = tabHeight,
                        x2 = tabStart + cornerRadius, y2 = cornerRadius
                    )
                    quadraticTo(
                        x1 = tabStart + cornerRadius, y1 = 0f,
                        x2 = tabStart + cornerRadius * 2, y2 = 0f
                    )

                    // Top of tab
                    lineTo(tabEnd - cornerRadius * 2, 0f)

                    // Right tab corner
                    quadraticTo(
                        x1 = tabEnd - cornerRadius, y1 = 0f,
                        x2 = tabEnd - cornerRadius, y2 = cornerRadius
                    )
                    quadraticTo(
                        x1 = tabEnd, y1 = tabHeight,
                        x2 = tabEnd, y2 = tabHeight + cornerRadius
                    )

                    // Right vertical edge
                    lineTo(width, tabHeight + cornerRadius)
                    lineTo(width, height)
                    lineTo(0f, height)
                }

                ShapePosition.Bottom -> {
                    // Start at top-left
                    moveTo(0f, 0f)
                    lineTo(0f, height - tabHeight - cornerRadius)

                    // Left vertical edge to tab
                    lineTo(tabStart, height - tabHeight - cornerRadius)

                    // Left tab corner
                    quadraticTo(
                        x1 = tabStart, y1 = height - tabHeight,
                        x2 = tabStart + cornerRadius, y2 = height - cornerRadius
                    )
                    quadraticTo(
                        x1 = tabStart + cornerRadius, y1 = height,
                        x2 = tabStart + cornerRadius * 2, y2 = height
                    )

                    // Bottom of tab
                    lineTo(tabEnd - cornerRadius * 2, height)

                    // Right tab corner
                    quadraticTo(
                        x1 = tabEnd - cornerRadius, y1 = height,
                        x2 = tabEnd - cornerRadius, y2 = height - cornerRadius
                    )
                    quadraticTo(
                        x1 = tabEnd, y1 = height - tabHeight,
                        x2 = tabEnd, y2 = height - tabHeight - cornerRadius
                    )

                    // Right vertical edge
                    lineTo(width, height - tabHeight - cornerRadius)
                    lineTo(width, 0f)
                    lineTo(0f, 0f)
                }

                else -> {
                    // For Left/Right, create a simple rounded rectangle
                    addRoundRect(
                        androidx.compose.ui.geometry.RoundRect(
                            left = 0f,
                            top = 0f,
                            right = width,
                            bottom = height,
                            radiusX = cornerRadius,
                            radiusY = cornerRadius
                        )
                    )
                }
            }
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * A shape with a notch cutout.
 *
 * @param position The position of the notch (Top, Bottom, Left, Right)
 * @param notchSize The size of the notch as a fraction (0.0 to 1.0)
 * @param style The style of the notch (Rounded or Sharp)
 * @param cornerRadius The radius for the remaining corners
 *
 * Example usage:
 * ```
 * Card(
 *     shape = NotchShape(
 *         position = ShapePosition.Top,
 *         notchSize = 0.2f,
 *         style = NotchStyle.Rounded
 *     )
 * ) {
 *     // Content
 * }
 * ```
 */
class NotchShape(
    private val position: ShapePosition = ShapePosition.Top,
    private val notchSize: Float = 0.2f,
    private val style: NotchStyle = NotchStyle.Rounded,
    private val cornerRadius: Float = 0f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height

            when (position) {
                ShapePosition.Top -> {
                    val notchWidth = width * notchSize
                    val notchHeight = height * notchSize * 0.5f
                    val notchStart = (width - notchWidth) / 2f
                    val notchEnd = notchStart + notchWidth

                    moveTo(0f, notchHeight)
                    lineTo(notchStart, notchHeight)

                    when (style) {
                        NotchStyle.Rounded -> {
                            quadraticTo(
                                x1 = notchStart + notchWidth / 2f, y1 = -notchHeight,
                                x2 = notchEnd, y2 = notchHeight
                            )
                        }
                        NotchStyle.Sharp -> {
                            lineTo(notchStart + notchWidth / 2f, 0f)
                            lineTo(notchEnd, notchHeight)
                        }
                    }

                    lineTo(width, notchHeight)
                    lineTo(width, height - cornerRadius)
                    if (cornerRadius > 0f) {
                        quadraticTo(width, height, width - cornerRadius, height)
                        lineTo(cornerRadius, height)
                        quadraticTo(0f, height, 0f, height - cornerRadius)
                    } else {
                        lineTo(width, height)
                        lineTo(0f, height)
                    }
                    lineTo(0f, notchHeight)
                }

                ShapePosition.Bottom -> {
                    moveTo(cornerRadius, 0f)
                    if (cornerRadius > 0f) {
                        quadraticTo(0f, 0f, 0f, cornerRadius)
                    } else {
                        lineTo(0f, 0f)
                    }

                    val notchWidth = width * notchSize
                    val notchHeight = height * notchSize * 0.5f
                    val notchStart = (width - notchWidth) / 2f
                    val notchEnd = notchStart + notchWidth

                    lineTo(0f, height - notchHeight)
                    lineTo(notchStart, height - notchHeight)

                    when (style) {
                        NotchStyle.Rounded -> {
                            quadraticTo(
                                x1 = notchStart + notchWidth / 2f, y1 = height + notchHeight,
                                x2 = notchEnd, y2 = height - notchHeight
                            )
                        }
                        NotchStyle.Sharp -> {
                            lineTo(notchStart + notchWidth / 2f, height)
                            lineTo(notchEnd, height - notchHeight)
                        }
                    }

                    lineTo(width, height - notchHeight)
                    lineTo(width, cornerRadius)
                    if (cornerRadius > 0f) {
                        quadraticTo(width, 0f, width - cornerRadius, 0f)
                    } else {
                        lineTo(width, 0f)
                    }
                    lineTo(cornerRadius, 0f)
                }

                ShapePosition.Left -> {
                    val notchHeight = height * notchSize
                    val notchWidth = width * notchSize * 0.5f
                    val notchStart = (height - notchHeight) / 2f
                    val notchEnd = notchStart + notchHeight

                    moveTo(notchWidth, 0f)
                    lineTo(notchWidth, notchStart)

                    when (style) {
                        NotchStyle.Rounded -> {
                            quadraticTo(
                                x1 = -notchWidth, y1 = notchStart + notchHeight / 2f,
                                x2 = notchWidth, y2 = notchEnd
                            )
                        }
                        NotchStyle.Sharp -> {
                            lineTo(0f, notchStart + notchHeight / 2f)
                            lineTo(notchWidth, notchEnd)
                        }
                    }

                    lineTo(notchWidth, height)
                    lineTo(width - cornerRadius, height)
                    if (cornerRadius > 0f) {
                        quadraticTo(width, height, width, height - cornerRadius)
                        lineTo(width, cornerRadius)
                        quadraticTo(width, 0f, width - cornerRadius, 0f)
                    } else {
                        lineTo(width, height)
                        lineTo(width, 0f)
                    }
                    lineTo(notchWidth, 0f)
                }

                ShapePosition.Right -> {
                    moveTo(cornerRadius, 0f)
                    if (cornerRadius > 0f) {
                        quadraticTo(0f, 0f, 0f, cornerRadius)
                        lineTo(0f, height - cornerRadius)
                        quadraticTo(0f, height, cornerRadius, height)
                    } else {
                        lineTo(0f, 0f)
                        lineTo(0f, height)
                    }

                    val notchHeight = height * notchSize
                    val notchWidth = width * notchSize * 0.5f
                    val notchStart = (height - notchHeight) / 2f
                    val notchEnd = notchStart + notchHeight

                    lineTo(width - notchWidth, height)
                    lineTo(width - notchWidth, notchEnd)

                    when (style) {
                        NotchStyle.Rounded -> {
                            quadraticTo(
                                x1 = width + notchWidth, y1 = notchStart + notchHeight / 2f,
                                x2 = width - notchWidth, y2 = notchStart
                            )
                        }
                        NotchStyle.Sharp -> {
                            lineTo(width, notchStart + notchHeight / 2f)
                            lineTo(width - notchWidth, notchStart)
                        }
                    }

                    lineTo(width - notchWidth, 0f)
                    lineTo(cornerRadius, 0f)
                }
            }
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * A chat bubble shape with a tail.
 *
 * @param tailPosition The position of the tail (Left, Right, Bottom)
 * @param tailSize The size of the tail
 * @param cornerRadius The radius for rounded corners
 * @param tailOffset Offset from edge as fraction (0.0 to 1.0)
 *
 * Example usage:
 * ```
 * Surface(
 *     shape = BubbleShape(
 *         tailPosition = ShapePosition.Left,
 *         tailSize = 12f,
 *         cornerRadius = 16f
 *     ),
 *     modifier = Modifier.padding(16.dp)
 * ) {
 *     Text("Hello!", modifier = Modifier.padding(12.dp))
 * }
 * ```
 */
class BubbleShape(
    private val tailPosition: ShapePosition = ShapePosition.Left,
    private val tailSize: Float = 12f,
    private val cornerRadius: Float = 16f,
    private val tailOffset: Float = 0.8f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        // Handle RTL layout for left/right tails
        val effectiveTailPosition = when {
            layoutDirection == LayoutDirection.Rtl && tailPosition == ShapePosition.Left -> ShapePosition.Right
            layoutDirection == LayoutDirection.Rtl && tailPosition == ShapePosition.Right -> ShapePosition.Left
            else -> tailPosition
        }

        val path = Path().apply {
            val width = size.width
            val height = size.height

            when (effectiveTailPosition) {
                ShapePosition.Left -> {
                    val tailY = height * tailOffset.coerceIn(0.2f, 0.8f)

                    // Start from top-left, going clockwise
                    moveTo(cornerRadius + tailSize, 0f)

                    // Top edge
                    lineTo(width - cornerRadius, 0f)
                    quadraticTo(width, 0f, width, cornerRadius)

                    // Right edge
                    lineTo(width, height - cornerRadius)
                    quadraticTo(width, height, width - cornerRadius, height)

                    // Bottom edge
                    lineTo(cornerRadius + tailSize, height)
                    quadraticTo(tailSize, height, tailSize, height - cornerRadius)

                    // Left edge with tail
                    lineTo(tailSize, tailY + tailSize)
                    lineTo(0f, tailY)
                    lineTo(tailSize, tailY - tailSize)
                    lineTo(tailSize, cornerRadius)
                    quadraticTo(tailSize, 0f, cornerRadius + tailSize, 0f)
                }

                ShapePosition.Right -> {
                    val tailY = height * tailOffset.coerceIn(0.2f, 0.8f)

                    // Start from top-left, going clockwise
                    moveTo(cornerRadius, 0f)

                    // Top edge
                    lineTo(width - cornerRadius - tailSize, 0f)
                    quadraticTo(width - tailSize, 0f, width - tailSize, cornerRadius)

                    // Right edge with tail
                    lineTo(width - tailSize, tailY - tailSize)
                    lineTo(width, tailY)
                    lineTo(width - tailSize, tailY + tailSize)
                    lineTo(width - tailSize, height - cornerRadius)
                    quadraticTo(width - tailSize, height, width - cornerRadius - tailSize, height)

                    // Bottom edge
                    lineTo(cornerRadius, height)
                    quadraticTo(0f, height, 0f, height - cornerRadius)

                    // Left edge
                    lineTo(0f, cornerRadius)
                    quadraticTo(0f, 0f, cornerRadius, 0f)
                }

                ShapePosition.Bottom -> {
                    val tailX = width * tailOffset.coerceIn(0.2f, 0.8f)

                    // Start from top-left, going clockwise
                    moveTo(cornerRadius, 0f)

                    // Top edge
                    lineTo(width - cornerRadius, 0f)
                    quadraticTo(width, 0f, width, cornerRadius)

                    // Right edge
                    lineTo(width, height - cornerRadius - tailSize)
                    quadraticTo(width, height - tailSize, width - cornerRadius, height - tailSize)

                    // Bottom edge with tail
                    lineTo(tailX + tailSize, height - tailSize)
                    lineTo(tailX, height)
                    lineTo(tailX - tailSize, height - tailSize)
                    lineTo(cornerRadius, height - tailSize)
                    quadraticTo(0f, height - tailSize, 0f, height - cornerRadius - tailSize)

                    // Left edge
                    lineTo(0f, cornerRadius)
                    quadraticTo(0f, 0f, cornerRadius, 0f)
                }

                ShapePosition.Top -> {
                    // For top position, create standard bubble (tail pointing up is less common)
                    addRoundRect(
                        androidx.compose.ui.geometry.RoundRect(
                            left = 0f,
                            top = 0f,
                            right = width,
                            bottom = height,
                            radiusX = cornerRadius,
                            radiusY = cornerRadius
                        )
                    )
                }
            }
            close()
        }
        return Outline.Generic(path)
    }
}
