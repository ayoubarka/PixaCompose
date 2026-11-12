package com.pixamob.pixacompose.primitives

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * The few icons the library's own components need (a check mark, a chevron, a close cross).
 * This is not an icon pack: apps bring their own icons as [ImageVector] or `Painter`.
 */
public object PixaIcons {
    public val Check: ImageVector by lazy { strokeIcon("Check") { moveTo(5f, 12.5f); lineTo(10f, 17.5f); lineTo(19f, 7f) } }

    public val Close: ImageVector by lazy {
        strokeIcon("Close") { moveTo(6f, 6f); lineTo(18f, 18f); moveTo(18f, 6f); lineTo(6f, 18f) }
    }

    public val Plus: ImageVector by lazy {
        strokeIcon("Plus") { moveTo(12f, 5f); lineTo(12f, 19f); moveTo(5f, 12f); lineTo(19f, 12f) }
    }

    public val Minus: ImageVector by lazy { strokeIcon("Minus") { moveTo(5f, 12f); lineTo(19f, 12f) } }

    public val ChevronDown: ImageVector by lazy {
        strokeIcon("ChevronDown") { moveTo(6f, 9f); lineTo(12f, 15f); lineTo(18f, 9f) }
    }

    public val ChevronRight: ImageVector by lazy {
        strokeIcon("ChevronRight") { moveTo(9f, 6f); lineTo(15f, 12f); lineTo(9f, 18f) }
    }

    public val ChevronLeft: ImageVector by lazy {
        strokeIcon("ChevronLeft") { moveTo(15f, 6f); lineTo(9f, 12f); lineTo(15f, 18f) }
    }
}

private fun strokeIcon(name: String, block: PathBuilder.() -> Unit): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).path(
        fill = null,
        // The color is a placeholder: Icon replaces it with the tint.
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
        pathFillType = PathFillType.NonZero,
        pathBuilder = block,
    ).build()
