package com.pixamob.pixacompose.utils

import androidx.compose.animation.core.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Utility object for standardized animations across the library
 * Provides consistent animation specs and helpers
 */
object AnimationUtils {

    /**
     * Standard spring animation spec (snappier feel)
     */
    fun <T> standardSpring(
        dampingRatio: Float = Spring.DampingRatioMediumBouncy,
        stiffness: Float = Spring.StiffnessMedium
    ): SpringSpec<T> = spring(
        dampingRatio = dampingRatio,
        stiffness = stiffness
    )

    /**
     * Fast spring animation (for quick transitions)
     */
    fun <T> fastSpring(): SpringSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )

    /**
     * Smooth spring animation (for smooth transitions)
     */
    fun <T> smoothSpring(): SpringSpec<T> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    /**
     * Standard tween animation (300ms)
     */
    fun <T> standardTween(
        durationMillis: Int = 300,
        easing: Easing = FastOutSlowInEasing
    ): TweenSpec<T> = tween(
        durationMillis = durationMillis,
        easing = easing
    )

    /**
     * Fast tween animation (150ms)
     */
    fun <T> fastTween(): TweenSpec<T> = tween(
        durationMillis = 150,
        easing = FastOutLinearInEasing
    )

    /**
     * Slow tween animation (500ms)
     */
    fun <T> slowTween(): TweenSpec<T> = tween(
        durationMillis = 500,
        easing = FastOutSlowInEasing
    )

    /**
     * Repeatable animation (for pulse effects)
     */
    fun <T> repeatableAnimation(
        iterations: Int = RepeatMode.Restart.ordinal,
        animation: DurationBasedAnimationSpec<T> = tween(1000)
    ): RepeatableSpec<T> = repeatable(
        iterations = iterations,
        animation = animation,
        repeatMode = RepeatMode.Restart
    )

    /**
     * Infinite repeatable animation
     */
    fun <T> infiniteRepeatable(
        animation: DurationBasedAnimationSpec<T> = tween(1000),
        repeatMode: RepeatMode = RepeatMode.Restart
    ): InfiniteRepeatableSpec<T> = infiniteRepeatable(
        animation = animation,
        repeatMode = repeatMode
    )

    /**
     * Standard fade in transition
     */
    val fadeInTransition: EnterTransition = fadeIn(
        animationSpec = tween(300)
    )

    /**
     * Standard fade out transition
     */
    val fadeOutTransition: ExitTransition = fadeOut(
        animationSpec = tween(300)
    )

    /**
     * Scale in transition (for buttons, chips)
     */
    val scaleInTransition: EnterTransition = scaleIn(
        initialScale = 0.8f,
        animationSpec = spring()
    ) + fadeIn()

    /**
     * Scale out transition
     */
    val scaleOutTransition: ExitTransition = scaleOut(
        targetScale = 0.8f,
        animationSpec = spring()
    ) + fadeOut()

    /**
     * Slide in from bottom transition
     */
    val slideInFromBottomTransition: EnterTransition = slideInVertically(
        initialOffsetY = { it },
        animationSpec = spring()
    ) + fadeIn()

    /**
     * Slide out to bottom transition
     */
    val slideOutToBottomTransition: ExitTransition = slideOutVertically(
        targetOffsetY = { it },
        animationSpec = spring()
    ) + fadeOut()

    /**
     * Animated visibility helper with standard transitions
     */
    @Composable
    fun AnimatedVisibilityStandard(
        visible: Boolean,
        modifier: Modifier = Modifier,
        enter: EnterTransition = fadeInTransition,
        exit: ExitTransition = fadeOutTransition,
        content: @Composable () -> Unit
    ) {
        AnimatedVisibility(
            visible = visible,
            modifier = modifier,
            enter = enter,
            exit = exit,
            content = { content() }
        )
    }
}

/**
 * Extension function to create a spring animation spec
 */
fun <T> springAnimation(
    dampingRatio: Float = Spring.DampingRatioMediumBouncy,
    stiffness: Float = Spring.StiffnessMedium
): SpringSpec<T> = AnimationUtils.standardSpring(dampingRatio, stiffness)

/**
 * Extension function to create a tween animation spec
 */
fun <T> tweenAnimation(
    durationMillis: Int = 300,
    easing: Easing = FastOutSlowInEasing
): TweenSpec<T> = AnimationUtils.standardTween(durationMillis, easing)

