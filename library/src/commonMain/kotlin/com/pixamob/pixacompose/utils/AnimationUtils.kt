package com.pixamob.pixacompose.utils

import androidx.compose.animation.core.*
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Named duration tiers (ms) all timing factories in [AnimationUtils] build
 * on. Pixa-friendly equivalent of Uber Base's timing bands — pick a tier by
 * how big/urgent the change feels, not by copying a number:
 *
 * - [Instant]: near-immediate feedback (a value snapping into place with no
 *   perceptible travel time — rarely needed on its own, mostly a building
 *   block for other factories).
 * - [Fast]: quick feedback transitions (press states, small color changes).
 * - [Standard]: the default for most enter/exit and color/opacity motion.
 * - [Slow]: deliberate, larger-movement transitions (sheets, drawers,
 *   anything that visibly travels a long distance).
 * - [Emphasized]: reserved for hero/attention moments that should read as
 *   unmistakably deliberate — use sparingly, per Uber Base's "purposeful,
 *   not decorative" motion principle.
 */
object MotionDuration {
    const val Instant = 100
    const val Fast = 150
    const val Standard = 300
    const val Slow = 500
    const val Emphasized = 650
}

/**
 * Compose-friendly translations of Uber Base's five named easing curves
 * (see `uber-base-timing.md`). Prefer these over Compose's built-in
 * [FastOutSlowInEasing]/[FastOutLinearInEasing] when a component's motion
 * matches one of these specific intents.
 */

/** "Accelerate/decelerate" — moving or pushing an element already in view. Uber Base default: 500ms. */
val AccelerateDecelerateEasing: Easing = CubicBezierEasing(0.83f, 0f, 0.17f, 1f)

/** "Decelerate" — an element entering view from off-screen, or snapping to a position after a drag. Uber Base default: 500ms. */
val DecelerateEasing: Easing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)

/** "Accelerate" — an element passively exiting without direct user interaction (e.g. a snackbar timing out). Uber Base default: 400ms. */
val AccelerateEasing: Easing = CubicBezierEasing(0.64f, 0f, 0.78f, 0f)

/** "Responsive accelerate" — an element exiting because the user directly dismissed it. Uber Base default: 200ms. */
val ResponsiveAccelerateEasing: Easing = CubicBezierEasing(0.11f, 0f, 0.5f, 0f)

/**
 * "Quintic ease-in-out" — a true degree-5 power curve, not a cubic-bezier
 * approximation like the four interaction easings above. Uber Base's
 * Placeholder (shimmer) spec names this exact curve for its 1000ms sweep;
 * no other component's spec calls for it, so it's a one-off `Easing { }`
 * rather than a `CubicBezierEasing` alongside the others.
 */
val QuinticEaseInOutEasing: Easing = Easing { fraction ->
    if (fraction < 0.5f) {
        16f * fraction * fraction * fraction * fraction * fraction
    } else {
        1f - (-2f * fraction + 2f).let { it * it * it * it * it } / 2f
    }
}

/**
 * "Quintic ease-out" — Uber Base's Progress Bar spec names this exact curve
 * for a stepped/segmented bar's active-segment loop ("loops a fill from left
 * to right and fades back... quintic ease-out, 500ms"). Distinct from
 * [QuinticEaseInOutEasing]: this one front-loads its motion (fast start,
 * slow settle) rather than easing at both ends.
 */
val QuinticEaseOutEasing: Easing = Easing { fraction ->
    1f - (1f - fraction).let { it * it * it * it * it }
}

/**
 * Utility object for standardized animations across the library.
 *
 * ## Motion taxonomy
 *
 * Every preset/factory below belongs to one semantic category. When adding a
 * new component or extending an existing one, pick a category first (what is
 * this motion *communicating*?), then a preset from it — don't reach for a
 * raw `spring()`/`tween()`, and don't pick a preset by "which one looks about
 * right," pick it by what it's semantically for. See DOCUMENTATION.md's
 * "Motion" section for the full interaction → preset mapping table.
 *
 * - **Selection**: a user picked one option among several (tabs, segments,
 *   checkboxes, radio buttons, selected list rows). → [selectionSpring]
 * - **Feedback**: a color/state changed in response to input or a state
 *   transition (hover, press, enabled/disabled, validation). → [colorSpring]
 * - **Drag/gesture follow**: a value tracks a continuous user gesture in
 *   real time (slider/range-slider thumb position while dragging, switch
 *   thumb sliding). → [thumbSpring], [fastSpring]
 * - **Emphasis**: draws attention to a value or status without being tied to
 *   a discrete user selection (progress sweep/width, a pulsing badge).
 *   → [indicatorSpring], [repeatableAnimation]
 * - **Reveal**: a component enters — appears, expands, or slides into view
 *   (dialogs, sheets, menus, popovers, snackbars, toasts appearing).
 *   → [fadeInTransition], [scaleInTransition], [slideInFromBottomTransition],
 *   [enterSnapTween], [AnimatedVisibilityStandard]
 * - **Dismissal**: a component exits — disappears, collapses, or slides out
 *   (the same components above, leaving). → [fadeOutTransition],
 *   [scaleOutTransition], [slideOutToBottomTransition], [passiveExitTween],
 *   [userDismissExitTween]
 * - **Loading**: an animation that repeats for as long as a background state
 *   persists (indeterminate progress, skeleton shimmer-adjacent pulses).
 *   → [infiniteRepeatable], [repeatableAnimation]
 * - **Navigation**: communicates the relationship between an outgoing and
 *   incoming surface (unrelated content swap, e.g. switching bottom-nav
 *   tabs) — Uber Base's "context change" pattern. → [contextChangeTransitionSpec].
 *   Uber Base's other navigation patterns (drill forward/back, slide
 *   forward/back between full screens) are explicitly a design spec only,
 *   not yet implemented in Base's own codebase either — PixaCompose is a
 *   component library, not a navigation/routing library, so those apply at
 *   the app level, not here.
 * - **Surface transition**: an overlay-style surface entering/exiting in a
 *   way that also implies its relationship to the content behind it
 *   (partial obstruction like a sheet/dialog vs. full replacement like a
 *   context change). → [slideInFromBottomTransition]/[slideOutToBottomTransition]
 *   (partial obstruction), [contextChangeTransitionSpec] (full replacement)
 *
 * Generic tuning factories ([standardSpring], [standardTween], [fastTween],
 * [slowTween], [smoothSpring], [fastSpringSpec]) exist for the cases a named
 * semantic preset doesn't cover — reach for a semantic preset first; fall
 * back to a generic factory (never a raw `spring()`/`tween()`) only when
 * none fits.
 */
object AnimationUtils {

    // ════════════════════════════════════════════════════════════════════
    // SELECTION — a user picked one option among several
    // ════════════════════════════════════════════════════════════════════

    /**
     * Selection spring animation (tab/segment selection transitions,
     * checkbox/switch check-state transitions, selected list rows).
     */
    val selectionSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    // ════════════════════════════════════════════════════════════════════
    // FEEDBACK — a color/state changed in response to input
    // ════════════════════════════════════════════════════════════════════

    /**
     * Color spring animation (background/content/border color transitions
     * on hover, press, enabled/disabled, and other state changes).
     */
    val colorSpring = spring<Color>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    // ════════════════════════════════════════════════════════════════════
    // DRAG / GESTURE FOLLOW — a value tracks a continuous user gesture
    // ════════════════════════════════════════════════════════════════════

    /**
     * Thumb spring animation (slider/switch thumb movement) — bouncier and
     * stiffer than [selectionSpring] so a dragged thumb feels physically
     * responsive rather than deliberate.
     */
    val thumbSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessHigh
    )

    /**
     * Fast spring animation preset — quick, snappy tracking for gesture-driven
     * values (slider/range-slider thumb position while actively dragging).
     */
    val fastSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )

    // ════════════════════════════════════════════════════════════════════
    // EMPHASIS — draws attention to a value or status
    // ════════════════════════════════════════════════════════════════════

    /**
     * Indicator spring animation (progress sweep/width transitions on
     * progress bars, tab indicators, badges drawing attention to a value).
     */
    val indicatorSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    /**
     * Slow spring animation preset — gentle, deliberate emphasis for
     * transitions that should read as unhurried (e.g. a stepper advancing
     * to a new step).
     */
    val slowSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )

    /**
     * Repeatable animation (for pulse/attention-loop effects). Per Uber Base's
     * accessibility guidance, non-essential repeating motion shouldn't loop
     * for more than ~5 seconds without a way to stop it — pass a finite
     * [iterations] for anything that isn't communicating an ongoing background
     * process (use [infiniteRepeatable] for those instead).
     */
    fun <T> repeatableAnimation(
        iterations: Int = RepeatMode.Restart.ordinal,
        animation: DurationBasedAnimationSpec<T> = tween(1000)
    ): RepeatableSpec<T> = repeatable(
        iterations = iterations,
        animation = animation,
        repeatMode = RepeatMode.Restart
    )

    // ════════════════════════════════════════════════════════════════════
    // LOADING — repeats for as long as a background state persists
    // ════════════════════════════════════════════════════════════════════

    /**
     * Infinite repeatable animation — indeterminate progress indicators and
     * other motion that should keep going for as long as the underlying
     * operation is in flight (not a fixed-count attention effect; see
     * [repeatableAnimation] for that).
     */
    fun <T> infiniteRepeatable(
        animation: DurationBasedAnimationSpec<T> = tween(1000),
        repeatMode: RepeatMode = RepeatMode.Restart
    ): InfiniteRepeatableSpec<T> = androidx.compose.animation.core.infiniteRepeatable(
        animation = animation,
        repeatMode = repeatMode
    )

    // ════════════════════════════════════════════════════════════════════
    // REVEAL / DISMISSAL — a component entering or exiting
    // ════════════════════════════════════════════════════════════════════

    /**
     * Standard fade in transition — the simplest, most common way to reveal
     * an element (dialogs, menus, popovers, snackbars, toasts appearing).
     */
    val fadeInTransition: EnterTransition = fadeIn(
        animationSpec = tween(300)
    )

    /**
     * Standard fade out transition — pairs with [fadeInTransition] for the
     * same set of components dismissing.
     */
    val fadeOutTransition: ExitTransition = fadeOut(
        animationSpec = tween(300)
    )

    /**
     * Scale in transition (for buttons, chips, and other small controls that
     * should draw attention as they appear).
     */
    val scaleInTransition: EnterTransition = scaleIn(
        initialScale = 0.8f,
        animationSpec = spring()
    ) + fadeIn()

    /**
     * Scale out transition — pairs with [scaleInTransition] for the same
     * controls disappearing.
     */
    val scaleOutTransition: ExitTransition = scaleOut(
        targetScale = 0.8f,
        animationSpec = spring()
    ) + fadeOut()

    /**
     * Slide in from bottom transition — for sheets, drawers, and other
     * edge-anchored surfaces entering from off-screen.
     */
    val slideInFromBottomTransition: EnterTransition = slideInVertically(
        initialOffsetY = { it },
        animationSpec = spring()
    ) + fadeIn()

    /**
     * Slide out to bottom transition — pairs with [slideInFromBottomTransition]
     * for the same surfaces dismissing.
     */
    val slideOutToBottomTransition: ExitTransition = slideOutVertically(
        targetOffsetY = { it },
        animationSpec = spring()
    ) + fadeOut()

    /**
     * Animated visibility helper with standard reveal/dismissal transitions —
     * the default choice for "should this composable be shown at all,"
     * as opposed to a value transition within an always-visible component.
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

    // ════════════════════════════════════════════════════════════════════
    // GENERIC FACTORIES — use when no semantic preset above fits
    // ════════════════════════════════════════════════════════════════════

    /**
     * Standard spring animation spec (snappier feel) — generic parameterized
     * fallback; prefer a named semantic preset above when one applies.
     */
    fun <T> standardSpring(
        dampingRatio: Float = Spring.DampingRatioMediumBouncy,
        stiffness: Float = Spring.StiffnessMedium
    ): SpringSpec<T> = spring(
        dampingRatio = dampingRatio,
        stiffness = stiffness
    )

    /**
     * Fast spring animation factory (generic, for quick transitions at any type).
     */
    fun <T> fastSpringSpec(): SpringSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )

    /**
     * Smooth spring animation (for smooth, low-bounce transitions).
     */
    fun <T> smoothSpring(): SpringSpec<T> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    /**
     * Instant tween animation (100ms) — the fastest named tier; near-immediate
     * feedback such as a value snapping into place.
     */
    fun <T> instantTween(): TweenSpec<T> = tween(
        durationMillis = MotionDuration.Instant,
        easing = LinearEasing
    )

    /**
     * Standard tween animation (300ms) — Compose Multiplatform's counterpart
     * to Uber Base's ~300ms "enter/exit" and color-transition timings.
     *
     * [delayMillis] is 0 by default; set it for staggered reveals (e.g. list
     * items entering a few ms apart) instead of building a raw `TweenSpec`
     * with a `delay` — `TweenSpec` is a plain class (no `copy()`), so a
     * post-hoc delay tweak has to go through this parameter.
     */
    fun <T> standardTween(
        durationMillis: Int = MotionDuration.Standard,
        easing: Easing = FastOutSlowInEasing,
        delayMillis: Int = 0
    ): TweenSpec<T> = tween(
        durationMillis = durationMillis,
        delayMillis = delayMillis,
        easing = easing
    )

    /**
     * Fast tween animation (150ms) — quick feedback transitions.
     */
    fun <T> fastTween(): TweenSpec<T> = tween(
        durationMillis = MotionDuration.Fast,
        easing = FastOutLinearInEasing
    )

    /**
     * Slow tween animation (500ms) — deliberate, larger-movement transitions,
     * matching Uber Base's guidance that bigger movements read better slower.
     */
    fun <T> slowTween(): TweenSpec<T> = tween(
        durationMillis = MotionDuration.Slow,
        easing = FastOutSlowInEasing
    )

    /**
     * Emphasized tween animation (650ms) — the slowest named tier, reserved
     * for hero/attention moments that should read as unmistakably deliberate.
     * Use sparingly; most motion should use [standardTween] or [slowTween].
     */
    fun <T> emphasizedTween(): TweenSpec<T> = tween(
        durationMillis = MotionDuration.Emphasized,
        easing = AccelerateDecelerateEasing
    )

    // ════════════════════════════════════════════════════════════════════
    // REVEAL / DISMISSAL (timing-aware) — Uber Base's enter-vs-exit-reason distinction
    // ════════════════════════════════════════════════════════════════════

    /**
     * Tween for an element entering view from off-screen, or snapping to a
     * position after a drag release — [DecelerateEasing] at [MotionDuration.Slow].
     * Reach for this instead of [standardTween]/[slowTween] when the motion is
     * specifically an entrance or a drag-release snap, not a generic transition.
     */
    fun <T> enterSnapTween(durationMillis: Int = MotionDuration.Slow): TweenSpec<T> = tween(
        durationMillis = durationMillis,
        easing = DecelerateEasing
    )

    /**
     * Tween for an element passively exiting *without* direct user interaction
     * — e.g. a snackbar/toast dismissing itself after its timeout elapses.
     * [AccelerateEasing] at a slightly shorter duration than [enterSnapTween]
     * (400ms), per Uber Base's timing guidance for passive exits.
     */
    fun <T> passiveExitTween(): TweenSpec<T> = tween(
        durationMillis = 400,
        easing = AccelerateEasing
    )

    /**
     * Tween for an element exiting because the user *directly* dismissed it
     * (tapped a close button, swiped it away). [ResponsiveAccelerateEasing] at
     * [MotionDuration.Standard] minus 100ms (200ms) — quicker than a passive
     * exit because the user is already expecting the result of their action.
     */
    fun <T> userDismissExitTween(): TweenSpec<T> = tween(
        durationMillis = 200,
        easing = ResponsiveAccelerateEasing
    )

    // ════════════════════════════════════════════════════════════════════
    // NAVIGATION / SURFACE TRANSITION — relationship between two surfaces
    // ════════════════════════════════════════════════════════════════════

    /**
     * Uber Base's "context change" content-swap transition: the outgoing
     * content fades out completely before the incoming content fades in,
     * signaling a clean break between unrelated content rather than a
     * continuous transformation. Use as the `transitionSpec` for
     * `AnimatedContent` when swapping between distant/unrelated content —
     * the canonical example is switching bottom-navigation tabs.
     *
     * Matches Uber Base's spec: 100ms linear fade-out, then a 100ms-delayed
     * 200ms linear fade-in (persistent chrome around the swapped content
     * stays fully opaque throughout, since only the swapped content itself
     * is wrapped in `AnimatedContent`).
     */
    fun <S> contextChangeTransitionSpec(): AnimatedContentTransitionScope<S>.() -> ContentTransform = {
        fadeIn(animationSpec = tween(200, delayMillis = MotionDuration.Instant, easing = LinearEasing)) togetherWith
            fadeOut(animationSpec = tween(MotionDuration.Instant, easing = LinearEasing))
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
