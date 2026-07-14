package com.pixamob.pixacompose.components.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.display.IconSource
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.ScreenUtil
import com.pixamob.pixacompose.utils.WindowSizeClass
import com.pixamob.pixacompose.utils.elevationShadow
import com.pixamob.pixacompose.utils.windowSizeClassOf

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Pointer position, combining Uber Base's two axes: vertical side (Top/Bottom = Above/Below)
 * or horizontal side (Start/End = Leading/Trailing), each with a perpendicular alignment
 * (Start/Center/End for vertical sides; Top/Center/Bottom for horizontal sides, reusing the
 * same Start/Center/End suffix vocabulary). Horizontal values ([StartTop]..[EndBottom]) are
 * spec'd wide-viewport-only — see [PixaPopover]'s Adaptive behavior docs.
 */
enum class PopoverPosition {
    TopStart,
    TopCenter,
    TopEnd,
    BottomStart,
    BottomCenter,
    BottomEnd,
    StartTop,
    StartCenter,
    StartBottom,
    EndTop,
    EndCenter,
    EndBottom
}

private val VerticalPopoverPositions = setOf(
    PopoverPosition.TopStart, PopoverPosition.TopCenter, PopoverPosition.TopEnd,
    PopoverPosition.BottomStart, PopoverPosition.BottomCenter, PopoverPosition.BottomEnd
)

/** Modal blocks the background with a scrim; NonModal opens on demand with no scrim. */
enum class PopoverPresentation {
    Modal,
    NonModal
}

/** Single-step-only per spec; not runtime-enforced when paginated, see [PixaPopover] docs. */
enum class PopoverContentAlignment {
    Start,
    Center
}

/** Artwork frame size is spec-fixed and intentionally non-modifiable — no size override param. */
enum class PopoverArtworkStyle {
    Icon,
    Badge
}

@Immutable
@Stable
data class PopoverArtwork(
    val source: IconSource,
    val style: PopoverArtworkStyle = PopoverArtworkStyle.Icon,
    val contentDescription: String? = null
)

/**
 * Spec: "Popover requires a dismiss action with a Close icon button or a text button like
 * 'Got it'. Exception: User-triggered Popovers (often informational)." [None] exists only for
 * that exception — [PixaPopover] defaults to [IconButton] and does not runtime-enforce this,
 * since only the caller knows whether their popover qualifies for the exception.
 */
sealed class PopoverDismissAction {
    data object IconButton : PopoverDismissAction()
    data class TextButton(val label: String) : PopoverDismissAction()
    data object None : PopoverDismissAction()
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class PopoverColors(
    val background: Color,
    val border: Color,
    val heading: Color,
    val content: Color,
    val dismissContent: Color,
    val scrim: Color
)

@Immutable
@Stable
data class PopoverSizeConfig(
    val padding: Dp,
    val cornerRadius: Dp,
    val elevation: Dp,
    val offset: Dp,
    val borderWidth: Dp,
    val pointerSize: Dp,
    val minWidth: Dp?,
    val maxWidth: Dp?,
    val headingStyle: TextStyle,
    val bodyStyle: TextStyle
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER (color/size resolvers)
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getPopoverTheme(): PopoverColors {
    val colors = AppTheme.colors
    return PopoverColors(
        background = colors.baseSurfaceDefault,
        border = colors.baseBorderSubtle,
        heading = colors.baseContentTitle,
        content = colors.baseContentBody,
        dismissContent = colors.baseContentBody,
        // Matches Dialog.kt/Drawer.kt's exact rgba(0,0,0,0.5) modal scrim literal — the spec's
        // named value; no semantic AppTheme.colors scrim token exists yet in this codebase.
        scrim = Color.Black.copy(alpha = 0.5f)
    )
}

/**
 * Narrow-viewport width constraints per spec: minWidth = 320-48 = 272dp, maxWidth =
 * screenWidth - 32dp. Neither 272dp nor the 32dp margin land on an existing HierarchicalSize
 * tier (Spacing tops out at Huge=24dp/Massive=48dp, straddling 32dp), so both are kept as local
 * spec-derived constants rather than forcing a mismatched token. Wide viewports are left
 * unconstrained ("defaults to content") per spec.
 */
private val PopoverNarrowMinWidth = 272.dp
private val PopoverNarrowEdgeMargin = 32.dp

// Spec: Badge artwork frame is a fixed 56dp, "sizes are non-modifiable." No HierarchicalSize
// tier lands on 56dp (Badge tops at Huge=28dp) — kept local and intentionally not exposed as an
// override param, matching the spec's own constraint.
private val PopoverArtworkBadgeSize = 56.dp

@Composable
private fun getPopoverSizeConfig(): PopoverSizeConfig {
    val typography = AppTheme.typography
    val screenWidth = ScreenUtil.getScreenWidth()
    val windowSizeClass = windowSizeClassOf(screenWidth)
    val minWidth: Dp?
    val maxWidth: Dp?
    if (windowSizeClass == WindowSizeClass.Compact) {
        minWidth = PopoverNarrowMinWidth
        maxWidth = screenWidth - PopoverNarrowEdgeMargin
    } else {
        minWidth = null
        maxWidth = null
    }
    return PopoverSizeConfig(
        padding = HierarchicalSize.Spacing.Medium,
        cornerRadius = HierarchicalSize.Radius.Huge, // spec: "automatically applies large (16px) if unspecified"
        elevation = HierarchicalSize.Shadow.Medium,
        offset = HierarchicalSize.Spacing.Small,
        borderWidth = HierarchicalSize.Border.Compact, // spec: "Weight: 1px; Align: Inside"
        pointerSize = HierarchicalSize.Spacing.Small,
        minWidth = minWidth,
        maxWidth = maxWidth,
        headingStyle = typography.titleBold,
        bodyStyle = typography.bodyRegular
    )
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/** Draws the caret/pointer triangle on the popover's edge closest to its anchor. */
private fun DrawScope.drawPopoverPointer(position: PopoverPosition, color: Color) {
    val w = size.width
    val h = size.height
    val path = Path()
    when (position) {
        PopoverPosition.BottomStart, PopoverPosition.BottomCenter, PopoverPosition.BottomEnd -> {
            // Popover is below its anchor: pointer on the popover's top edge, tip pointing up.
            path.moveTo(0f, h)
            path.lineTo(w / 2, 0f)
            path.lineTo(w, h)
        }
        PopoverPosition.TopStart, PopoverPosition.TopCenter, PopoverPosition.TopEnd -> {
            // Popover is above its anchor: pointer on the popover's bottom edge, tip pointing down.
            path.moveTo(0f, 0f)
            path.lineTo(w / 2, h)
            path.lineTo(w, 0f)
        }
        PopoverPosition.EndTop, PopoverPosition.EndCenter, PopoverPosition.EndBottom -> {
            // Popover is to the end/trailing side: pointer on its leading edge, tip pointing start.
            path.moveTo(w, 0f)
            path.lineTo(0f, h / 2)
            path.lineTo(w, h)
        }
        PopoverPosition.StartTop, PopoverPosition.StartCenter, PopoverPosition.StartBottom -> {
            // Popover is to the start/leading side: pointer on its trailing edge, tip pointing end.
            path.moveTo(0f, 0f)
            path.lineTo(w, h / 2)
            path.lineTo(0f, h)
        }
    }
    path.close()
    drawPath(path, color)
}

/** Cross-axis alignment for [VerticalPopoverPositions] (Top and Bottom values) within their shared Column. */
private fun PopoverPosition.horizontalPointerAlignment(): Alignment.Horizontal = when (this) {
    PopoverPosition.TopStart, PopoverPosition.BottomStart -> Alignment.Start
    PopoverPosition.TopCenter, PopoverPosition.BottomCenter -> Alignment.CenterHorizontally
    PopoverPosition.TopEnd, PopoverPosition.BottomEnd -> Alignment.End
    else -> Alignment.CenterHorizontally
}

/** Cross-axis alignment for horizontal positions (Start and End values) within their shared Row. */
private fun PopoverPosition.verticalPointerAlignment(): Alignment.Vertical = when (this) {
    PopoverPosition.StartTop, PopoverPosition.EndTop -> Alignment.Top
    PopoverPosition.StartCenter, PopoverPosition.EndCenter -> Alignment.CenterVertically
    PopoverPosition.StartBottom, PopoverPosition.EndBottom -> Alignment.Bottom
    else -> Alignment.CenterVertically
}

/** True when the pointer renders before (above/leading) the card in its Row/Column. */
private fun PopoverPosition.pointerLeadsCard(): Boolean = this in setOf(
    PopoverPosition.BottomStart, PopoverPosition.BottomCenter, PopoverPosition.BottomEnd,
    PopoverPosition.EndTop, PopoverPosition.EndCenter, PopoverPosition.EndBottom
)

@Composable
private fun PopoverDismissIconButton(onClick: () -> Unit, contentColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(HierarchicalSize.TouchTarget.Small)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // No dedicated close-icon asset exists in this library yet (same gap noted in
        // Alert.kt/MessageCard.kt's dismiss buttons) — a glyph avoids a one-off icon dependency.
        BasicText(text = "×", style = AppTheme.typography.titleBold.copy(color = contentColor))
    }
}

@Composable
private fun PopoverPaginationRow(
    pageCount: Int,
    currentPage: Int,
    onPageChange: (Int) -> Unit,
    colors: PopoverColors,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)
    ) {
        Box(
            modifier = Modifier
                .size(HierarchicalSize.TouchTarget.Nano)
                .clip(CircleShape)
                .clickable(
                    enabled = currentPage > 0,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onPageChange(currentPage - 1) }
                ),
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                text = "‹",
                style = AppTheme.typography.titleBold.copy(
                    color = if (currentPage > 0) colors.content else colors.content.copy(alpha = 0.3f)
                )
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Nano)) {
            repeat(pageCount) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentPage) HierarchicalSize.Badge.Small else HierarchicalSize.Badge.Nano)
                        .clip(CircleShape)
                        .background(if (index == currentPage) AppTheme.colors.brandSurfaceDefault else AppTheme.colors.baseBorderDefault)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(HierarchicalSize.TouchTarget.Nano)
                .clip(CircleShape)
                .clickable(
                    enabled = currentPage < pageCount - 1,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onPageChange(currentPage + 1) }
                ),
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                text = "›",
                style = AppTheme.typography.titleBold.copy(
                    color = if (currentPage < pageCount - 1) colors.content else colors.content.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
private fun PopoverCardContent(
    heading: String,
    body: String?,
    content: (@Composable () -> Unit)?,
    artwork: PopoverArtwork?,
    contentAlignment: PopoverContentAlignment,
    dismissAction: PopoverDismissAction,
    pageCount: Int,
    currentPage: Int,
    onPageChange: ((Int) -> Unit)?,
    onDismiss: () -> Unit,
    colors: PopoverColors,
    sizeConfig: PopoverSizeConfig,
    modifier: Modifier = Modifier
) {
    val textAlign = if (contentAlignment == PopoverContentAlignment.Center) TextAlign.Center else TextAlign.Start

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.Top) {
            if (artwork != null) {
                PixaIcon(
                    source = artwork.source,
                    contentDescription = artwork.contentDescription,
                    customSize = if (artwork.style == PopoverArtworkStyle.Badge) PopoverArtworkBadgeSize else HierarchicalSize.Icon.Large,
                    modifier = Modifier.padding(end = HierarchicalSize.Spacing.Small)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                BasicText(
                    text = heading,
                    style = sizeConfig.headingStyle.copy(color = colors.heading, textAlign = textAlign),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (content != null) {
                    Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Compact))
                    content()
                } else if (body != null) {
                    Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Compact))
                    BasicText(
                        text = body,
                        style = sizeConfig.bodyStyle.copy(color = colors.content, textAlign = textAlign)
                    )
                }
            }

            if (dismissAction is PopoverDismissAction.IconButton) {
                PopoverDismissIconButton(
                    onClick = onDismiss,
                    contentColor = colors.dismissContent,
                    modifier = Modifier.padding(start = HierarchicalSize.Spacing.Small)
                )
            }
        }

        val showPagination = pageCount > 1 && onPageChange != null
        val showTextDismiss = dismissAction is PopoverDismissAction.TextButton

        if (showPagination || showTextDismiss) {
            Row(
                modifier = Modifier.padding(top = HierarchicalSize.Spacing.Small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showPagination) {
                    PopoverPaginationRow(
                        pageCount = pageCount,
                        currentPage = currentPage,
                        onPageChange = { onPageChange?.invoke(it) },
                        colors = colors
                    )
                }
                if (showTextDismiss) {
                    PixaButton(
                        text = (dismissAction as PopoverDismissAction.TextButton).label,
                        onClick = onDismiss,
                        variant = ButtonVariant.Ghost
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaPopover - contextual overlay anchored to a triggering element, migrated from Uber Base's
 * Popover spec.
 *
 * Purpose: instructional (teaching feature usage) or informational (explaining what a feature
 * does) contextual content, opened on interaction and left open until explicitly dismissed.
 * Distinct from a tooltip (smaller/lightweight, brief hints) by being larger, more interactive,
 * and supporting images/multi-step content; use a Dialog instead for long content or error
 * messaging; use a Toast/Snackbar for ephemeral feedback.
 *
 * Anatomy: [heading] (required, max 2 lines), [body] text or custom [content] (one or the other),
 * a pointer triangle drawn toward the anchor, a required [dismissAction], optional [artwork]
 * (Icon 28dp or Badge 56dp, non-modifiable per spec), and optional pagination via
 * [pageCount]/[currentPage]/[onPageChange].
 *
 * States: closed/open (via [visible]); enabled/disabled buttons within are the caller's concern
 * (this component doesn't model a disabled popover itself, matching spec's own decomposition).
 *
 * Sizing: 16dp corner radius and 1dp inside border by default, both per spec's stated fallback
 * values, plus breakpoint-aware min/max width (see Adaptive behavior).
 *
 * Adaptive behavior: the spec's own <600px breakpoint maps directly onto this library's existing
 * `WindowSizeClass.Compact` threshold (also 600dp) — narrow viewports get `minWidth=272dp`,
 * `maxWidth=screenWidth-32dp`. Wide viewports are left content-sized, matching "not specified;
 * defaults to content." [position]'s horizontal values ([PopoverPosition.StartTop]..[EndBottom])
 * are spec'd wide-viewport-only; the caller decides [position], so this isn't runtime-blocked.
 *
 * Customization: [colors]/[artwork]/[dismissAction]/pagination are all opt-in; heading size/color
 * are intentionally not exposed as override params (spec: "heading size/color not customizable").
 *
 * Usage notes: don't substitute for a Dialog (data collection, multiple actions, long content) or
 * for ephemeral feedback (use a Toast/Snackbar instead). Show one popover at a time. No links in
 * [heading]. [fallbackPosition] is a caller-supplied approximation of the spec's automatic
 * collision-aware position fallback — this library has no anchor-bounds-tracking
 * `PopupPositionProvider` anywhere yet (neither `Tooltip.kt` nor `Menu.kt` implement one either),
 * so true automatic flip-on-collision is out of scope for this migration; pass an explicit
 * fallback if you need one. Likewise Esc/Tab dismissal is a desktop/web keyboard concept without
 * a target-platform equivalent here (Android/iOS only) — [dismissOnOutsideClick] plus the
 * platform back gesture cover the equivalent dismissal path instead.
 *
 * @param visible Whether the popover is shown
 * @param onDismiss Callback when the popover should close
 * @param heading Required heading text, max 2 lines
 * @param modifier Modifier for the popover surface
 * @param body Optional default body text; ignored when [content] is non-null
 * @param content Optional custom content, replacing [body]
 * @param artwork Optional artwork (Icon/Badge, see Anatomy)
 * @param position Preferred pointer position/alignment
 * @param fallbackPosition See Usage notes — caller-driven fallback, no automatic collision detection
 * @param presentation [PopoverPresentation.Modal] adds a scrim; [PopoverPresentation.NonModal] (default) does not
 * @param contentAlignment Left/center text alignment; spec restricts this to single-step popovers
 * @param dismissAction Required dismiss affordance; see [PopoverDismissAction]
 * @param pageCount Total pages; 1 (default) hides pagination chrome entirely
 * @param currentPage Zero-based current page index
 * @param onPageChange Invoked with the new page index when prev/next is tapped
 * @param dismissOnOutsideClick Tap-outside-to-dismiss; spec calls this "supplementary, not primary"
 * @param colors Custom colors; defaults to the theme resolver
 */
@Composable
fun PixaPopover(
    visible: Boolean,
    onDismiss: () -> Unit,
    heading: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    content: (@Composable () -> Unit)? = null,
    artwork: PopoverArtwork? = null,
    position: PopoverPosition = PopoverPosition.BottomCenter,
    fallbackPosition: PopoverPosition? = null,
    presentation: PopoverPresentation = PopoverPresentation.NonModal,
    contentAlignment: PopoverContentAlignment = PopoverContentAlignment.Start,
    dismissAction: PopoverDismissAction = PopoverDismissAction.IconButton,
    pageCount: Int = 1,
    currentPage: Int = 0,
    onPageChange: ((Int) -> Unit)? = null,
    dismissOnOutsideClick: Boolean = true,
    colors: PopoverColors? = null
) {
    val themeColors = colors ?: getPopoverTheme()
    val sizeConfig = getPopoverSizeConfig()
    val resolvedPosition = fallbackPosition ?: position

    val popupAlignment = when (resolvedPosition) {
        PopoverPosition.TopStart, PopoverPosition.TopCenter, PopoverPosition.TopEnd -> Alignment.TopCenter
        PopoverPosition.BottomStart, PopoverPosition.BottomCenter, PopoverPosition.BottomEnd -> Alignment.BottomCenter
        PopoverPosition.StartTop, PopoverPosition.StartCenter, PopoverPosition.StartBottom -> Alignment.CenterStart
        PopoverPosition.EndTop, PopoverPosition.EndCenter, PopoverPosition.EndBottom -> Alignment.CenterEnd
    }

    val popupOffset = when (resolvedPosition) {
        PopoverPosition.TopStart, PopoverPosition.TopCenter, PopoverPosition.TopEnd ->
            IntOffset(0, -sizeConfig.offset.value.toInt())
        PopoverPosition.BottomStart, PopoverPosition.BottomCenter, PopoverPosition.BottomEnd ->
            IntOffset(0, sizeConfig.offset.value.toInt())
        PopoverPosition.StartTop, PopoverPosition.StartCenter, PopoverPosition.StartBottom ->
            IntOffset(-sizeConfig.offset.value.toInt(), 0)
        PopoverPosition.EndTop, PopoverPosition.EndCenter, PopoverPosition.EndBottom ->
            IntOffset(sizeConfig.offset.value.toInt(), 0)
    }

    val transformOrigin = when (resolvedPosition) {
        PopoverPosition.TopStart, PopoverPosition.TopCenter, PopoverPosition.TopEnd -> TransformOrigin(0.5f, 1f)
        PopoverPosition.BottomStart, PopoverPosition.BottomCenter, PopoverPosition.BottomEnd -> TransformOrigin(0.5f, 0f)
        PopoverPosition.StartTop, PopoverPosition.StartCenter, PopoverPosition.StartBottom -> TransformOrigin(1f, 0.5f)
        PopoverPosition.EndTop, PopoverPosition.EndCenter, PopoverPosition.EndBottom -> TransformOrigin(0f, 0.5f)
    }

    if (!visible) return

    Popup(
        alignment = popupAlignment,
        offset = popupOffset,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true, dismissOnClickOutside = dismissOnOutsideClick)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(AnimationUtils.standardTween()) + scaleIn(
                animationSpec = AnimationUtils.standardTween(),
                transformOrigin = transformOrigin
            ),
            exit = fadeOut(AnimationUtils.fastTween()) + scaleOut(
                animationSpec = AnimationUtils.fastTween(),
                transformOrigin = transformOrigin
            )
        ) {
            Box {
                if (presentation == PopoverPresentation.Modal) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(themeColors.scrim)
                            .clickable(
                                enabled = dismissOnOutsideClick,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onDismiss
                            )
                    )
                }

                val shape = RoundedCornerShape(sizeConfig.cornerRadius)
                val pointerLeadsCard = resolvedPosition.pointerLeadsCard()

                val pointerModifier = Modifier
                    .size(sizeConfig.pointerSize)
                    .drawBehind { drawPopoverPointer(resolvedPosition, themeColors.background) }

                val cardModifier = modifier
                    .then(
                        if (sizeConfig.minWidth != null) {
                            Modifier.widthIn(min = sizeConfig.minWidth, max = sizeConfig.maxWidth ?: Dp.Infinity)
                        } else {
                            Modifier
                        }
                    )
                    .elevationShadow(sizeConfig.elevation, shape)
                    .clip(shape)
                    .background(themeColors.background)
                    .border(width = sizeConfig.borderWidth, color = themeColors.border, shape = shape)
                    .padding(sizeConfig.padding)

                val card = @Composable {
                    PopoverCardContent(
                        heading = heading,
                        body = body,
                        content = content,
                        artwork = artwork,
                        contentAlignment = contentAlignment,
                        dismissAction = dismissAction,
                        pageCount = pageCount,
                        currentPage = currentPage,
                        onPageChange = onPageChange,
                        onDismiss = onDismiss,
                        colors = themeColors,
                        sizeConfig = sizeConfig,
                        modifier = cardModifier
                    )
                }

                if (resolvedPosition in VerticalPopoverPositions) {
                    Column(horizontalAlignment = resolvedPosition.horizontalPointerAlignment()) {
                        if (pointerLeadsCard) Box(pointerModifier)
                        card()
                        if (!pointerLeadsCard) Box(pointerModifier)
                    }
                } else {
                    Row(verticalAlignment = resolvedPosition.verticalPointerAlignment()) {
                        if (pointerLeadsCard) Box(pointerModifier)
                        card()
                        if (!pointerLeadsCard) Box(pointerModifier)
                    }
                }
            }
        }
    }
}
