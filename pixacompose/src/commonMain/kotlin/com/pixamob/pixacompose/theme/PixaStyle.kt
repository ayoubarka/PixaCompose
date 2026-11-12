package com.pixamob.pixacompose.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

/**
 * A complete visual language. Swapping the [PixaStyle] passed to [PixaTheme] is the only thing
 * needed to restyle every component; component code never changes per style.
 */
@Immutable
public data class PixaStyle(
    val name: String,
    val lightColors: PixaColors,
    val darkColors: PixaColors,
    val shapes: PixaShapes,
    val typography: PixaTypography,
    val decoration: PixaDecoration,
)

/** Corner treatment scale. Any [Shape] is allowed, not only rounded rectangles. */
@Immutable
public data class PixaShapes(
    /** Small controls: checkbox, badge. */
    val sm: Shape,
    /** Default controls: button, input. */
    val md: Shape,
    /** Containers: card, dialog, sheet. */
    val lg: Shape,
    /** Pills and circles. */
    val full: Shape,
)

@Immutable
public data class PixaTypography(
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val body: TextStyle,
    val bodySmall: TextStyle,
    /** Text inside controls: buttons, tabs, form labels. */
    val label: TextStyle,
    val caption: TextStyle,
)

/** Everything that separates one style from another beyond color, shape and type. */
@Immutable
public data class PixaDecoration(
    /** Outline width of bordered surfaces: card, outlined button, input. */
    val borderWidth: Dp,
    /** Outline width of filled surfaces such as a primary button. 0 disables it. */
    val filledBorderWidth: Dp,
    /** Shadow of raised surfaces: buttons and cards. */
    val shadow: PixaShadow,
    /** How clickable surfaces react while pressed. */
    val press: PressEffect,
)

public sealed interface PixaShadow {
    public data object None : PixaShadow

    /** Classic blurred elevation shadow. */
    @Immutable
    public data class Soft(val elevation: Dp) : PixaShadow

    /** Solid, unblurred copy of the surface offset behind it. Defaults to the border color. */
    @Immutable
    public data class Hard(
        val offset: DpOffset = DpOffset(3.dp, 4.dp),
        val color: Color = Color.Unspecified,
    ) : PixaShadow

    /** Blurred halo around the surface. Defaults to the surface's border color, else its fill. */
    @Immutable
    public data class Glow(
        val radius: Dp,
        val spread: Dp = 0.dp,
        val color: Color = Color.Unspecified,
    ) : PixaShadow
}

public sealed interface PressEffect {
    public data object None : PressEffect

    @Immutable
    public data class Scale(val scale: Float = 0.97f) : PressEffect

    @Immutable
    public data class Dim(val alpha: Float = 0.8f) : PressEffect

    /** The surface moves onto its [PixaShadow.Hard] shadow. No effect with other shadows. */
    public data object Sink : PressEffect
}
