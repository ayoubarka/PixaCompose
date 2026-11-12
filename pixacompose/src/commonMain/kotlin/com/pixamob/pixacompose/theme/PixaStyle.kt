package com.pixamob.pixacompose.theme

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
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
    val spacing: PixaSpacing = PixaSpacing(),
    val motion: PixaMotion = PixaMotion(),
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
    /** Shadow of interactive controls: buttons, toggles. */
    val controlShadow: PixaShadow,
    /** Shadow of containers: cards, dialogs, sheets. */
    val containerShadow: PixaShadow,
    /** How clickable surfaces react while pressed. */
    val press: PressEffect,
    /** Outline shown around a control that has keyboard focus, in the `ring` color. */
    val focusRing: PixaFocusRing = PixaFocusRing(),
)

@Immutable
public data class PixaFocusRing(
    /** 0 disables the ring. */
    val width: Dp = 2.dp,
    /** Gap between the control's edge and the ring. */
    val offset: Dp = 2.dp,
)

/** Gaps and paddings. A style can make the whole library denser or roomier here. */
@Immutable
public data class PixaSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
)

/** How things move. Components pick a role; the style decides how it feels. */
@Immutable
public data class PixaMotion(
    /** Press feedback and other immediate reactions to touch. */
    val press: PixaSpring = PixaSpring(dampingRatio = 1f, stiffness = 1200f),
    /** State changes inside a control: a switch thumb, a selection indicator. */
    val state: PixaSpring = PixaSpring(dampingRatio = 1f, stiffness = 600f),
    /** Things entering or leaving the screen: dialogs, sheets, toasts. */
    val overlay: PixaSpring = PixaSpring(dampingRatio = 1f, stiffness = 380f),
    /** Length of one half-cycle of looping placeholders such as [Skeleton][com.pixamob.pixacompose.components.Skeleton]. */
    val pulseMillis: Int = 900,
)

@Immutable
public data class PixaSpring(
    /** 1 has no bounce; lower values overshoot more. */
    val dampingRatio: Float,
    val stiffness: Float,
) {
    public fun <T> spec(visibilityThreshold: T? = null): SpringSpec<T> =
        spring(dampingRatio, stiffness, visibilityThreshold)
}

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
