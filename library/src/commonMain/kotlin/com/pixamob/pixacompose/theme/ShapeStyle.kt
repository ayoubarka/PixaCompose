package com.pixamob.pixacompose.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

val roundedCornerShapes = Shapes(
    extraSmall = RoundedCornerShape(RadiusSize.ExtraSmall),
    small = RoundedCornerShape(RadiusSize.Small),
    medium = RoundedCornerShape(RadiusSize.Medium),
    large = RoundedCornerShape(RadiusSize.Large),
    extraLarge = RoundedCornerShape(RadiusSize.ExtraLarge)
)

val cutCornerShapes = Shapes(
    extraSmall = CutCornerShape(RadiusSize.ExtraSmall),
    small = CutCornerShape(RadiusSize.Small),
    medium = CutCornerShape(RadiusSize.Medium),
    large = CutCornerShape(RadiusSize.Large),
    extraLarge = CutCornerShape(RadiusSize.ExtraLarge)
)

@Immutable
data class ShapeStyles(
    val rounded: Shapes,
    val cut: Shapes
)

val shapeStyles = ShapeStyles(
    rounded = roundedCornerShapes,
    cut = cutCornerShapes
)

val LocalShapeStyle =
    staticCompositionLocalOf { ShapeStyles(roundedCornerShapes, cutCornerShapes) }