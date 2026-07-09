---
name: animation
description: How to animate a PixaCompose component using the three layers of utils/AnimationUtils.kt (fixed presets, parameterized factories, ready-made transitions) instead of raw spring()/tween(). Trigger phrases -- "animate this component", "add a transition", "which spring should I use", "AnimationUtils", "add a scale/fade/slide animation", "why is my animation using raw spring/tween".
---

# animation

`CLAUDE.md` already states the hard rule: **never call `spring()`/`tween()` directly in a component** — go through `utils/AnimationUtils.kt`. This is fully enforced today (verified: zero raw `spring()`/`tween()` calls remain across `components/`, 28 files import `AnimationUtils`) — keep it that way in new code.

## The three layers, and when to reach for each

**1. Fixed presets** — pick these first, by semantic name, when the motion matches a known interaction:

| Preset | Use for |
|---|---|
| `indicatorSpring` | progress sweep / width transitions |
| `selectionSpring` | tab/segment selection transitions |
| `thumbSpring` | slider/switch thumb movement (bouncier, high stiffness) |
| `colorSpring` | color transitions specifically typed for `Color` |
| `fastSpring` | quick, snappy, no-bounce transitions |
| `slowSpring` | gentle, deliberate, no-bounce transitions |

**2. Parameterized factories** — reach for these when no preset fits and you need a specific feel or duration:

`standardSpring(dampingRatio, stiffness)`, `fastSpringSpec()`, `smoothSpring()`, `standardTween(durationMillis, easing)`, `fastTween()` (150ms), `slowTween()` (500ms), `repeatableAnimation()`, `infiniteRepeatable()`.

**3. Ready-made transitions** — for `AnimatedVisibility`/enter-exit, don't hand-roll `fadeIn()+spring()`:

`fadeInTransition`/`fadeOutTransition`, `scaleInTransition`/`scaleOutTransition` (0.8f→1f scale + fade), `slideInFromBottomTransition`/`slideOutToBottomTransition`, or the composable wrapper `AnimationUtils.AnimatedVisibilityStandard(visible) { ... }` which defaults to fade in/out.

Top-level `springAnimation()`/`tweenAnimation()` exist as thin delegates for older call sites — call `AnimationUtils.standardSpring()`/`standardTween()` directly in new code instead.

## Real examples from the codebase

**Color transition on state change** — `components/actions/Button.kt:314-318`, animating background on enabled/disabled/loading changes:

```kotlin
val backgroundColor by animateColorAsState(
    targetValue = currentColors.background,
    animationSpec = AnimationUtils.standardTween(150),
    label = "button_bg"
)
```

**Scale-pulse on selection** — `components/inputs/DatePicker.kt:834-838` (repeated at 5 call sites across calendar/wheel/weekday/month variants), animating a day cell's scale when selected:

```kotlin
val scale by animateFloatAsState(
    targetValue = if (isSelected) 1.1f else 1f,
    animationSpec = standardSpring(),   // imported via: import com.pixamob.pixacompose.utils.AnimationUtils.standardSpring
    label = "dayScale"
)
```

Both examples follow the same shape: resolve a target value from state, animate it with `animateXAsState` + an `AnimationUtils` spec, always pass a `label` (helps with animation inspector debugging and is consistent across the codebase).

## Checklist

- [ ] No inline `spring()`/`tween()` — always `AnimationUtils.<preset or factory>`
- [ ] Picked a named preset first; only reached for a factory if no preset matched the interaction
- [ ] `animateXAsState` calls include a `label` string
- [ ] For enter/exit visibility, used a ready-made transition or `AnimatedVisibilityStandard`, not a hand-built `EnterTransition`
