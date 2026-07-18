package com.pixamob.pixacompose.components.display

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.IconButtonVariant
import com.pixamob.pixacompose.components.actions.PixaIconButton
import com.pixamob.pixacompose.components.feedback.PageControlColorScheme
import com.pixamob.pixacompose.components.feedback.PagerIndicatorStyle
import com.pixamob.pixacompose.components.feedback.PixaPagerIndicator
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.WindowSizeClass
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * eBay Playbook: arrow visibility scales with screen size — hidden on small
 * screens (touch/keyboard reveal instead), hover-revealed on medium/large,
 * always visible on x-large. [Adaptive] approximates that table onto Pixa's
 * 3-tier [WindowSizeClass] (exact px breakpoints don't carry over 1:1 to a
 * mobile-first multiplatform target); the other values let a caller pin one
 * behavior explicitly.
 */
enum class CarouselArrowVisibility {
    Hidden,
    HoverVisible,
    AlwaysVisible,
    Adaptive
}

/**
 * Ant Design confirms both a scroll (`scrollx`) and a `fade` transition effect.
 * [Fade] cross-fades the current/target page instead of translating them.
 */
enum class CarouselTransitionEffect {
    Scroll,
    Fade
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Optional structured slide metadata (eBay Playbook item-tile content model:
 * visual/content slot + title + description). Only used by the
 * `items: List<PixaCarouselItem>` convenience overload — the primary
 * [PixaCarousel] API takes arbitrary per-index composables and doesn't
 * require this shape at all.
 */
@Immutable
data class PixaCarouselItem(
    val content: @Composable () -> Unit,
    val title: String? = null,
    val description: String? = null
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER (resolvers)
// ════════════════════════════════════════════════════════════════════════════

private fun resolveArrowVisibility(
    visibility: CarouselArrowVisibility,
    windowSizeClass: WindowSizeClass
): CarouselArrowVisibility = if (visibility == CarouselArrowVisibility.Adaptive) {
    when (windowSizeClass) {
        WindowSizeClass.Compact -> CarouselArrowVisibility.Hidden
        WindowSizeClass.Medium -> CarouselArrowVisibility.HoverVisible
        WindowSizeClass.Expanded -> CarouselArrowVisibility.AlwaysVisible
    }
} else {
    visibility
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL CAROUSEL
// ════════════════════════════════════════════════════════════════════════════

/** Renders [item]'s content slot followed by its optional title/description. */
@Composable
private fun DefaultCarouselSlide(item: PixaCarouselItem) {
    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)) {
        item.content()
        if (item.title != null) {
            BasicText(
                text = item.title,
                style = AppTheme.typography.titleBold.copy(color = AppTheme.colors.baseContentTitle),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (item.description != null) {
            BasicText(
                text = item.description,
                style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Ghost icon button rotated 180° for the "next" direction — mirrors Accordion's single-asset convention. */
@Composable
private fun CarouselArrowButton(
    icon: Painter,
    direction: Int,
    onClick: () -> Unit,
    size: SizeVariant,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    PixaIconButton(
        icon = icon,
        onClick = onClick,
        variant = IconButtonVariant.Filled,
        size = size,
        contentDescription = contentDescription,
        modifier = modifier.graphicsLayer { rotationZ = if (direction < 0) 0f else 180f }
    )
}

private fun targetPage(pagerState: PagerState, itemCount: Int, infiniteScroll: Boolean, delta: Int): Int {
    val next = pagerState.currentPage + delta
    return if (infiniteScroll) next else next.coerceIn(0, (itemCount - 1).coerceAtLeast(0))
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * ### Purpose
 * A generic, slot-based carousel: a horizontally scrollable window over
 * [itemCount] slides, each rendered by [itemContent]. Any composable content
 * is valid per slide — this is not an image carousel (see [PixaBannerCarousel]
 * in `Banner.kt` for the full-bleed promo-banner variant, which is a
 * separate, unrelated component).
 *
 * ### Anatomy
 * eBay Playbook anatomy: optional group `title` + "see all" link → scrollable
 * content → pagination controls. The group title/see-all pair sits at the
 * *container* level (one heading describes the whole carousel), while
 * per-slide title/description belongs to individual items — see
 * [PixaCarouselItem] and its convenience overload below. Rendered via
 * [PixaSectionHeading] with a [SectionHeadingTrailing.TextButton] when both
 * [title] and [onSeeAllClick] are supplied.
 *
 * ### Interaction
 * - Swipe/drag/fling: native [HorizontalPager] behavior.
 * - Pointer: tap indicator dots (via [PixaPagerIndicator]) or arrow buttons.
 * - Keyboard: focus the carousel, then Left/Right arrow keys page (RTL-aware).
 * - Hover: arrow visibility can be hover-revealed, see [CarouselArrowVisibility].
 *
 * ### Sizing / adaptive behavior
 * No dedicated [SizeVariant] ladder for the carousel frame itself — [size]
 * only sizes the indicator dots and arrow buttons. [arrowVisibility] defaults
 * to [CarouselArrowVisibility.Adaptive], approximating eBay's breakpoint-based
 * arrow-visibility table onto [AppTheme.windowSizeClass].
 *
 * ### Customization
 * - [transitionEffect]: scroll (default) or fade, per Ant Design's `effect` prop.
 * - [autoPlay] / [autoPlayIntervalMillis]: confirmed Ant Design feature, off by default.
 * - [infiniteScroll]: confirmed Ant Design feature (`infinite`), off by default in
 *   Pixa — wraparound correctness is opt-in rather than assumed.
 * - [adaptiveHeight]: confirmed Ant Design feature (`adaptiveHeight`). Pixa's
 *   implementation resizes the pager to the current slide's measured height on
 *   settle via `animateContentSize` — an approximation, not a live per-frame
 *   height morph during the drag itself (Foundation's `HorizontalPager` doesn't
 *   expose that).
 * - Arrow icons are caller-supplied (no bundled icon set in this repo, see
 *   `Accordion.kt`'s chevron convention) — arrows only render when [arrowIcon]
 *   is non-null.
 *
 * @param itemCount total number of slides.
 * @param itemContent slide content for a given zero-based index.
 * @param title optional container-level heading describing the whole group.
 * @param onSeeAllClick optional "see all" trailing action next to [title].
 * @param arrowIcon a single chevron-left [Painter], rotated 180° for "next"; omit to hide arrows entirely.
 */
@Composable
fun PixaCarousel(
    itemCount: Int,
    modifier: Modifier = Modifier,
    title: String? = null,
    seeAllLabel: String = "See all",
    onSeeAllClick: (() -> Unit)? = null,
    size: SizeVariant = SizeVariant.Medium,
    slideSpacing: Dp = HierarchicalSize.Spacing.Medium,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showIndicators: Boolean = true,
    indicatorStyle: PagerIndicatorStyle = PagerIndicatorStyle.Circle,
    arrowIcon: Painter? = null,
    arrowVisibility: CarouselArrowVisibility = CarouselArrowVisibility.Adaptive,
    transitionEffect: CarouselTransitionEffect = CarouselTransitionEffect.Scroll,
    adaptiveHeight: Boolean = false,
    infiniteScroll: Boolean = false,
    autoPlay: Boolean = false,
    autoPlayIntervalMillis: Long = 3000L,
    onPageChanged: ((Int) -> Unit)? = null,
    itemContent: @Composable (index: Int) -> Unit
) {
    if (itemCount <= 0) return

    val pageCount = if (infiniteScroll) Int.MAX_VALUE else itemCount
    val initialPage = if (infiniteScroll) (Int.MAX_VALUE / 2) - ((Int.MAX_VALUE / 2) % itemCount) else 0
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { pageCount })
    val coroutineScope = rememberCoroutineScope()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val windowSizeClass = AppTheme.windowSizeClass
    val resolvedArrowVisibility = resolveArrowVisibility(arrowVisibility, windowSizeClass)
    val currentIndex = ((pagerState.currentPage % itemCount) + itemCount) % itemCount

    LaunchedEffect(currentIndex) { onPageChanged?.invoke(currentIndex) }

    LaunchedEffect(autoPlay, pagerState.currentPage) {
        if (!autoPlay || itemCount <= 1) return@LaunchedEffect
        delay(autoPlayIntervalMillis)
        val next = targetPage(pagerState, itemCount, infiniteScroll, delta = 1)
        pagerState.animateScrollToPage(next, animationSpec = AnimationUtils.standardTween())
    }

    fun goTo(delta: Int) {
        val next = targetPage(pagerState, itemCount, infiniteScroll, delta)
        coroutineScope.launch { pagerState.animateScrollToPage(next, animationSpec = AnimationUtils.standardTween()) }
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
        if (title != null) {
            PixaSectionHeading(
                heading = title,
                trailing = if (onSeeAllClick != null) {
                    SectionHeadingTrailing.TextButton(seeAllLabel, onSeeAllClick)
                } else {
                    SectionHeadingTrailing.None
                },
                size = size
            )
        }

        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()
        val arrowsShown = arrowIcon != null && when (resolvedArrowVisibility) {
            CarouselArrowVisibility.Hidden -> false
            CarouselArrowVisibility.AlwaysVisible -> true
            CarouselArrowVisibility.HoverVisible -> isHovered
            CarouselArrowVisibility.Adaptive -> false // resolved above, never reaches here
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .hoverable(interactionSource)
                .focusable(interactionSource = interactionSource)
                .onKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                    when (event.key) {
                        Key.DirectionLeft -> { goTo(if (isRtl) 1 else -1); true }
                        Key.DirectionRight -> { goTo(if (isRtl) -1 else 1); true }
                        else -> false
                    }
                }
        ) {
            HorizontalPager(
                state = pagerState,
                pageSpacing = slideSpacing,
                contentPadding = contentPadding,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (adaptiveHeight) Modifier.wrapContentHeight().animateContentSize(AnimationUtils.standardSpring()) else Modifier)
            ) { page ->
                val index = ((page % itemCount) + itemCount) % itemCount
                val slideModifier = if (transitionEffect == CarouselTransitionEffect.Fade) {
                    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                    Modifier.graphicsLayer { alpha = 1f - pageOffset.coerceIn(0f, 1f) }
                } else {
                    Modifier
                }
                Box(modifier = slideModifier) { itemContent(index) }
            }

            if (arrowsShown) {
                CarouselArrowButton(
                    icon = arrowIcon!!,
                    direction = -1,
                    onClick = { goTo(-1) },
                    size = size,
                    contentDescription = "Previous slide",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(HierarchicalSize.Spacing.Small)
                )
                CarouselArrowButton(
                    icon = arrowIcon,
                    direction = 1,
                    onClick = { goTo(1) },
                    size = size,
                    contentDescription = "Next slide",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(HierarchicalSize.Spacing.Small)
                )
            }
        }

        if (showIndicators && itemCount > 1) {
            PixaPagerIndicator(
                pageCount = itemCount,
                currentPage = currentIndex,
                style = indicatorStyle,
                colorScheme = PageControlColorScheme.Default,
                onPageSelected = { target ->
                    val delta = target - currentIndex
                    if (delta != 0) goTo(delta)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Convenience overload for structured slides ([PixaCarouselItem]: content slot
 * + optional title + optional description) instead of a raw index lambda.
 */
@Composable
fun PixaCarousel(
    items: List<PixaCarouselItem>,
    modifier: Modifier = Modifier,
    title: String? = null,
    seeAllLabel: String = "See all",
    onSeeAllClick: (() -> Unit)? = null,
    size: SizeVariant = SizeVariant.Medium,
    slideSpacing: Dp = HierarchicalSize.Spacing.Medium,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showIndicators: Boolean = true,
    indicatorStyle: PagerIndicatorStyle = PagerIndicatorStyle.Circle,
    arrowIcon: Painter? = null,
    arrowVisibility: CarouselArrowVisibility = CarouselArrowVisibility.Adaptive,
    transitionEffect: CarouselTransitionEffect = CarouselTransitionEffect.Scroll,
    adaptiveHeight: Boolean = false,
    infiniteScroll: Boolean = false,
    autoPlay: Boolean = false,
    autoPlayIntervalMillis: Long = 3000L,
    onPageChanged: ((Int) -> Unit)? = null
) {
    PixaCarousel(
        itemCount = items.size,
        modifier = modifier,
        title = title,
        seeAllLabel = seeAllLabel,
        onSeeAllClick = onSeeAllClick,
        size = size,
        slideSpacing = slideSpacing,
        contentPadding = contentPadding,
        showIndicators = showIndicators,
        indicatorStyle = indicatorStyle,
        arrowIcon = arrowIcon,
        arrowVisibility = arrowVisibility,
        transitionEffect = transitionEffect,
        adaptiveHeight = adaptiveHeight,
        infiniteScroll = infiniteScroll,
        autoPlay = autoPlay,
        autoPlayIntervalMillis = autoPlayIntervalMillis,
        onPageChanged = onPageChanged
    ) { index -> DefaultCarouselSlide(items[index]) }
}
