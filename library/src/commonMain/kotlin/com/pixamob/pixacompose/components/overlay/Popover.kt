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
 * Pointer position relative to anchor. Vertical (Top/Bottom) or horizontal (Start/End) side,
 * each with perpendicular alignment (Start/Center/End for vertical; Top/Center/Bottom for horizontal).
 * Horizontal positions are wide-viewport-only.
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

/** Single-step-only; not enforced when paginated. */
enum class PopoverContentAlignment {
    Start,
    Center
}

/** Artwork frame size is fixed — no override param. */
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
 * Dismiss action style. [IconButton] (default), [TextButton] with a custom label, or [None]
 * for informational popovers only.
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
        scrim = Color.Black.copy(alpha = 0.5f)
    )
}

/** Narrow-viewport width constraints. */
private val PopoverNarrowMinWidth = 272.dp
private val PopoverNarrowEdgeMargin = 32.dp

/** Badge artwork frame size (fixed, no override). */
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
        cornerRadius = HierarchicalSize.Radius.Huge,
        elevation = HierarchicalSize.Shadow.Medium,
        offset = HierarchicalSize.Spacing.Small,
        borderWidth = HierarchicalSize.Border.Compact,
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
 * Contextual overlay anchored to a triggering element.
 *
 * ### Purpose
 * Instructional or informational content, larger than a tooltip. Supports images and multi-step
 * content. Use a Dialog for long content or error messaging; use Toast/Snackbar for ephemeral feedback.
 *
 * ### Anatomy
 * [heading] (required, max 2 lines) + [body] or custom [content] + pointer triangle +
 * required [dismissAction] + optional [artwork] (Icon 28dp / Badge 56dp) + optional pagination.
 *
 * ### States
 * Closed/open via [visible].
 *
 * ### Sizing
 * 16dp corner radius, 1dp inside border. Narrow: min 272dp, max screen−32dp. Wide: content-sized.
 *
 * ### Usage notes
 * - Do not substitute for a Dialog or Toast/Snackbar
 * - One popover at a time; no links in heading
 * - [fallbackPosition] is caller-driven (no auto-collision detection)
 * - Esc/Tab: mobile uses [dismissOnOutsideClick] + back gesture instead
 *
 * @param visible Popover visibility
 * @param onDismiss Close callback
 * @param heading Required heading (max 2 lines)
 * @param body Optional body text (ignored if [content] is set)
 * @param content Optional custom content replacing [body]
 * @param artwork Optional Icon or Badge artwork
 * @param position Preferred pointer position
 * @param fallbackPosition Caller-driven fallback (no auto-collision)
 * @param presentation Modal adds scrim
 * @param contentAlignment Left or center text
 * @param dismissAction IconButton, TextButton, or None
 * @param pageCount Total pages (1 hides pagination)
 * @param currentPage 0-based page index
 * @param onPageChange Prev/next callback
 * @param dismissOnOutsideClick Tap-outside dismissal
 * @param colors Custom color overrides
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
