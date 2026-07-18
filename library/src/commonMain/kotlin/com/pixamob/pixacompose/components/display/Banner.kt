package com.pixamob.pixacompose.components.display

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextOverflow
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.WindowSizeClass
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * eBay Playbook: "Tall — used on educational and support landing pages where
 * height is not a concern" / "Short — used on dense, performance-focused
 * shopping pages where height is a concern." The two named tiers are
 * confirmed; the exact size delta between them is not (see [resolveAspectRatio]).
 */
enum class BannerHeight {
    Tall,
    Short
}

/**
 * The five confirmed background/content models from the eBay Playbook source.
 * Each variant carries its own required content because the anatomy differs
 * structurally, not just visually (e.g. [MultiDestination] has no single
 * "entire image" tap target, [InsetImage] has a spec-fixed neutral palette).
 */
sealed class BannerBackground {
    /** Full-bleed photography; text overlays with a scrim + gradient + shadow — see [PixaBanner]. */
    data class Image(val painter: Painter) : BannerBackground()

    /** Solid color background with an optional transparent-PNG foreground image, caller-supplied colors. */
    data class Color(
        val background: androidx.compose.ui.graphics.Color,
        val content: androidx.compose.ui.graphics.Color,
        val foregroundImage: Painter? = null
    ) : BannerBackground()

    /** Light neutral background with a rounded-corner inset image; spec-fixed palette (not caller-themed). */
    data class InsetImage(val painter: Painter) : BannerBackground()

    /** Solid color background with a scrollable rail of independently-tappable destinations. */
    data class MultiDestination(
        val background: androidx.compose.ui.graphics.Color,
        val content: androidx.compose.ui.graphics.Color,
        val destinations: List<BannerDestination>
    ) : BannerBackground()

    /** Solid color background, no image — "dense, performance-focused shopping pages... no relevant image." */
    data class Loyalty(
        val background: androidx.compose.ui.graphics.Color,
        val content: androidx.compose.ui.graphics.Color
    ) : BannerBackground()
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

/** One entry in [BannerBackground.MultiDestination] — "images and text strings each link separately." */
data class BannerDestination(
    val image: Painter,
    val label: String,
    val onClick: () -> Unit
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER / RESOLVERS
// ════════════════════════════════════════════════════════════════════════════

/** eBay Playbook Image Background asset canvas: 1740×600. */
private const val ImageBannerRatio = 1740f / 600f

/** eBay Playbook Color/Inset Background asset canvas: 840×600 (also used for MultiDestination, which has no dedicated container canvas in the source). */
private const val ColorBannerRatio = 840f / 600f

/** No asset canvas given for Loyalty (image-less) in the source; approximated flatter than Color/Inset since it targets "dense" pages. */
private const val LoyaltyBannerRatio = 2f

/**
 * The source confirms two named height tiers exist and their use-cases, but
 * never states the pixel/ratio delta between them — this multiplier is an
 * approximation, not a confirmed value.
 */
private const val TallRatioMultiplier = 0.7f

/**
 * Resolves the banner's aspect ratio so height stays "relative to the width
 * of the viewport" per the source, rather than a fixed dp. See the ratio
 * constants above for which parts are asset-confirmed vs. approximated.
 */
private fun resolveAspectRatio(background: BannerBackground, height: BannerHeight): Float {
    val base = when (background) {
        is BannerBackground.Image -> ImageBannerRatio
        is BannerBackground.Color -> ColorBannerRatio
        is BannerBackground.InsetImage -> ColorBannerRatio
        is BannerBackground.MultiDestination -> ColorBannerRatio
        is BannerBackground.Loyalty -> LoyaltyBannerRatio
    }
    return if (height == BannerHeight.Tall) base * TallRatioMultiplier else base
}

/**
 * [BannerBackground.Color]/[MultiDestination]/[Loyalty] take literal caller
 * colors rather than [AppTheme.colors] tokens — the source requires banners
 * to hold a fixed, brand-approved palette that does **not** shift with the
 * app's light/dark scheme ("in dark mode, banners do not change color").
 * [InsetImage] approximates the source's fixed neutral-gray/black ("N200"
 * background, black text) with the closest neutral theme tokens, since the
 * exact N200 hex isn't given by the source — a known, flagged approximation
 * that will drift with the theme, unlike the literal-color variants.
 */
private fun BannerBackground.contentColor(colors: ColorPalette): androidx.compose.ui.graphics.Color = when (this) {
    is BannerBackground.Image -> Color.White // "legibility of white text" over photography — fixed, not themed
    is BannerBackground.Color -> content
    is BannerBackground.InsetImage -> colors.baseContentTitle
    is BannerBackground.MultiDestination -> content
    is BannerBackground.Loyalty -> content
}

private fun BannerBackground.surfaceColor(colors: ColorPalette): androidx.compose.ui.graphics.Color = when (this) {
    is BannerBackground.Image -> Color.Transparent
    is BannerBackground.Color -> background
    is BannerBackground.InsetImage -> colors.baseSurfaceSubtle
    is BannerBackground.MultiDestination -> background
    is BannerBackground.Loyalty -> background
}

/** eBay Playbook: "black scrim in #000000 at 5% opacity over the entire photo." */
private val ImageScrimColor = Color.Black.copy(alpha = 0.05f)

/** eBay Playbook: "radial gradient in #030819 at 70% opacity centered behind the text area." */
private val ImageGradientCenterColor = Color(red = 0x03 / 255f, green = 0x08 / 255f, blue = 0x19 / 255f, alpha = 0.70f)

/**
 * eBay Playbook: "a 100% opacity drop shadow in #000000" on text over
 * photography. Color/opacity are confirmed exact values; offset/blur are not
 * specified by the source and use small, conservative defaults.
 */
private val ImageTextShadow = Shadow(color = Color.Black, offset = Offset(0f, 1f), blurRadius = 3f)

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL BANNER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Text stack shared by every variant: overline → headline → body → action →
 * disclaimer. Text position (left-anchored) and the gradient's focal point
 * follow the source's photo safe-zone note ("right-of-center" reserved for
 * imagery), which implies text sits opposite it — not explicitly stated as
 * such by the source, so treated as an assumption.
 */
@Composable
private fun BannerTextStack(
    headline: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    contentColor: Color,
    overline: String?,
    body: String?,
    disclaimer: String?,
    onDisclaimerClick: (() -> Unit)?,
    textShadow: Shadow?,
    modifier: Modifier = Modifier
) {
    val typography = AppTheme.typography
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
    ) {
        // Recommended max 33 characters per the source; not enforced at runtime.
        overline?.let {
            BasicText(text = it, style = typography.overline.copy(color = contentColor, shadow = textShadow), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        // Recommended max 33 characters per the source; not enforced at runtime.
        BasicText(text = headline, style = typography.headlineBold.copy(color = contentColor, shadow = textShadow), maxLines = 2, overflow = TextOverflow.Ellipsis)
        // Recommended max 65 characters per the source; hidden on mobile per the source ("hidden on mobile").
        if (body != null && AppTheme.windowSizeClass != WindowSizeClass.Compact) {
            BasicText(text = body, style = typography.bodyRegular.copy(color = contentColor, shadow = textShadow), maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Compact))
        // Source: "single action button," label "1 - 4 words," sentence case, no ending punctuation — caller's responsibility.
        PixaButton(
            onClick = onActionClick,
            text = actionLabel,
            variant = ButtonVariant.OnBrand,
            size = SizeVariant.Small
        )
        // Recommended max 65 characters per the source; a separate link target from the primary action.
        disclaimer?.let { text ->
            BasicText(
                text = text,
                style = typography.footnoteRegular.copy(color = contentColor.copy(alpha = 0.8f), shadow = textShadow),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = if (onDisclaimerClick != null) {
                    Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDisclaimerClick
                        )
                        .semantics { role = Role.Button }
                } else Modifier
            )
        }
    }
}

@Composable
private fun DestinationChip(destination: BannerDestination, contentColor: Color) {
    Column(
        modifier = Modifier
            .width(HierarchicalSize.Avatar.Massive)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = destination.onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)
    ) {
        PixaImage(
            painter = destination.image,
            contentDescription = destination.label,
            contentScale = ContentScale.Fit,
            ratio = PixaImageRatio.Square,
            modifier = Modifier.fillMaxWidth()
        )
        BasicText(
            text = destination.label,
            style = AppTheme.typography.captionRegular.copy(color = contentColor),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** eBay Playbook: "dot indicators are always the color of the text." */
@Composable
private fun CarouselDots(count: Int, activeIndex: Int, dotColor: Color, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)) {
        repeat(count) { index ->
            Box(
                modifier = Modifier
                    .size(HierarchicalSize.Spacing.Small)
                    .clip(CircleShape)
                    .background(dotColor.copy(alpha = if (index == activeIndex) 1f else 0.4f))
            )
        }
    }
}

/** eBay Playbook: back/forward/pause-play controls, "90% white background," bottom-right, ≥600dp only. */
@Composable
private fun CarouselControls(
    isPlaying: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onTogglePlaying: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)) {
        listOf(
            Triple("‹", "Previous banner", onPrevious),
            Triple(if (isPlaying) "❚❚" else "▶", if (isPlaying) "Pause" else "Play", onTogglePlaying),
            Triple("›", "Next banner", onNext)
        ).forEach { (glyph, description, action) ->
            Box(
                modifier = Modifier
                    .size(HierarchicalSize.TouchTarget.Small)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = action
                    )
                    .semantics {
                        contentDescription = description
                        role = Role.Button
                    },
                contentAlignment = Alignment.Center
            ) {
                BasicText(text = glyph, style = AppTheme.typography.labelMedium.copy(color = Color.Black))
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaBanner — a full-bleed, expressive promo/marketing banner, per the
 * [eBay Playbook Banner](https://playbook.ebay.com/design-system/components/banner)
 * spec. Distinct from [com.pixamob.pixacompose.components.feedback.PixaSystemBanner],
 * which is for system status, not promotions.
 *
 * ### Anatomy
 * [overline] → [headline] → [body] (hidden on Compact widths) → single action
 * button ([actionLabel]/[onActionClick]) → [disclaimer]. [background] supplies
 * the image/color layer and, for [BannerBackground.MultiDestination], the
 * destination rail.
 *
 * ### Variants
 * [BannerBackground.Image] (full-bleed photo, text overlaid with a scrim +
 * gradient + shadow), `.Color` (solid background + optional foreground PNG),
 * `.InsetImage` (fixed neutral background, rounded inset image), `.MultiDestination`
 * (solid background + independently-tappable destination rail), `.Loyalty`
 * (solid background, no image).
 *
 * ### Sizing
 * Responsive by construction — [Modifier.aspectRatio] ties height to measured
 * width, matching the source ("height... is relative to the width of the
 * viewport"). [height] ([BannerHeight.Tall]/`.Short`) adjusts that ratio; see
 * [resolveAspectRatio] for which parts are asset-confirmed vs. approximated.
 *
 * ### Interactive areas
 * [onClick] is the "entire image links to primary destination" tap target —
 * applies to every variant except [BannerBackground.MultiDestination], whose
 * destinations link independently instead. [disclaimer] links separately via
 * [onDisclaimerClick].
 *
 * ### Usage notes
 * - "Disperse banners throughout the page" / "don't stack banners directly
 *   on top of each other" — a page-composition rule, not enforced here.
 * - Overline/headline recommended max 33 characters, body/disclaimer max 65,
 *   button label 1–4 words — content-authoring guidance from the source, not
 *   runtime-enforced truncation.
 * - In dark mode, banner colors do not change — see [BannerBackground.contentColor]
 *   for why `Color`/`MultiDestination`/`Loyalty` take literal colors, not theme tokens.
 *
 * @param headline Required headline text (recommended max 33 characters)
 * @param actionLabel Required single action button label (1–4 words, sentence case, no ending punctuation)
 * @param onActionClick Action button callback
 * @param background Variant + content (see [BannerBackground])
 * @param modifier Modifier for the banner
 * @param onClick Whole-banner primary tap target; not wired for [BannerBackground.MultiDestination]
 * @param overline Optional overline text, e.g. a program badge (recommended max 33 characters)
 * @param body Optional body text, hidden below [WindowSizeClass.Compact] widths (recommended max 65 characters)
 * @param disclaimer Optional disclaimer text, links separately from [onClick] (recommended max 65 characters)
 * @param onDisclaimerClick Disclaimer tap callback
 * @param height [BannerHeight.Tall] or `.Short` (default)
 * @param contentDescription Accessibility description for the banner
 */
@Composable
fun PixaBanner(
    headline: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    background: BannerBackground,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    overline: String? = null,
    body: String? = null,
    disclaimer: String? = null,
    onDisclaimerClick: (() -> Unit)? = null,
    height: BannerHeight = BannerHeight.Short,
    contentDescription: String? = null
) {
    val colors = AppTheme.colors
    val contentColor = background.contentColor(colors)
    val surfaceColor = background.surfaceColor(colors)
    val aspectRatio = resolveAspectRatio(background, height)
    val isCompact = AppTheme.windowSizeClass == WindowSizeClass.Compact

    val containerModifier = modifier
        .fillMaxWidth()
        .aspectRatio(aspectRatio)
        .background(surfaceColor)
        .then(
            if (onClick != null && background !is BannerBackground.MultiDestination) {
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
            } else Modifier
        )
        .semantics {
            this.contentDescription = contentDescription ?: headline
        }

    when (background) {
        is BannerBackground.Image -> {
            Box(modifier = containerModifier) {
                PixaImage(
                    painter = background.painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    ratio = PixaImageRatio.Original,
                    modifier = Modifier.fillMaxSize()
                )
                // "Black scrim... plus an additional radial gradient... centered behind the text area."
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ImageScrimColor)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(ImageGradientCenterColor, Color.Transparent),
                                center = Offset.Zero,
                                radius = 800f
                            )
                        )
                )
                BannerTextStack(
                    headline = headline,
                    actionLabel = actionLabel,
                    onActionClick = onActionClick,
                    contentColor = contentColor,
                    overline = overline,
                    body = body,
                    disclaimer = disclaimer,
                    onDisclaimerClick = onDisclaimerClick,
                    textShadow = ImageTextShadow,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(HierarchicalSize.Padding.Huge)
                        .fillMaxWidth(if (isCompact) 1f else 0.6f)
                )
            }
        }

        is BannerBackground.Loyalty, is BannerBackground.InsetImage, is BannerBackground.Color -> {
            val foregroundImage = (background as? BannerBackground.Color)?.foregroundImage
                ?: (background as? BannerBackground.InsetImage)?.painter

            if (isCompact) {
                Column(modifier = containerModifier.padding(HierarchicalSize.Padding.Large)) {
                    BannerTextStack(
                        headline = headline, actionLabel = actionLabel, onActionClick = onActionClick,
                        contentColor = contentColor, overline = overline, body = body,
                        disclaimer = disclaimer, onDisclaimerClick = onDisclaimerClick, textShadow = null,
                        modifier = Modifier.weight(1f)
                    )
                    // "On small screens, images appear below text."
                    foregroundImage?.let {
                        PixaImage(
                            painter = it,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            shape = if (background is BannerBackground.InsetImage) AppTheme.shapes.rounded.forVariant(SizeVariant.Large) else RectangleShape,
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        )
                    }
                }
            } else {
                Row(modifier = containerModifier.padding(HierarchicalSize.Padding.Huge), verticalAlignment = Alignment.CenterVertically) {
                    BannerTextStack(
                        headline = headline, actionLabel = actionLabel, onActionClick = onActionClick,
                        contentColor = contentColor, overline = overline, body = body,
                        disclaimer = disclaimer, onDisclaimerClick = onDisclaimerClick, textShadow = null,
                        modifier = Modifier.weight(1f)
                    )
                    // "On larger screens, images appear right of text."
                    foregroundImage?.let {
                        PixaImage(
                            painter = it,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            shape = if (background is BannerBackground.InsetImage) AppTheme.shapes.rounded.forVariant(SizeVariant.Large) else RectangleShape,
                            modifier = Modifier.fillMaxHeight().weight(1f)
                        )
                    }
                }
            }
        }

        is BannerBackground.MultiDestination -> {
            Column(
                modifier = containerModifier.padding(HierarchicalSize.Padding.Large),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                BannerTextStack(
                    headline = headline, actionLabel = actionLabel, onActionClick = onActionClick,
                    contentColor = contentColor, overline = overline, body = body,
                    disclaimer = disclaimer, onDisclaimerClick = onDisclaimerClick, textShadow = null
                )
                // "Multi-destination images and text strings each link separately."
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium),
                    contentPadding = PaddingValues(top = HierarchicalSize.Spacing.Small)
                ) {
                    items(background.destinations) { destination ->
                        DestinationChip(destination = destination, contentColor = contentColor)
                    }
                }
            }
        }
    }
}

/**
 * PixaBannerCarousel — wraps multiple [PixaBanner]s per the source's carousel
 * rules. Below [WindowSizeClass.Compact]'s 600dp threshold (matching the
 * source's ">599px" cutoff), banners stack vertically with no controls —
 * "we do not use carousels" on small screens. At [WindowSizeClass.Medium]/
 * `.Expanded`, banners auto-scroll behind dot indicators and back/forward/
 * pause-play controls.
 *
 * @param banners Ordered list of banner content — each entry renders as one [PixaBanner]
 * @param modifier Modifier for the carousel
 * @param autoScrollIntervalMs Auto-advance interval; not specified by the source, defaults to a conservative 5s
 */
@Composable
fun PixaBannerCarousel(
    banners: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier,
    autoScrollIntervalMs: Long = 5000L
) {
    if (banners.isEmpty()) return

    if (AppTheme.windowSizeClass == WindowSizeClass.Compact) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)) {
            banners.forEach { it() }
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { banners.size })
    var isPlaying by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isPlaying, pagerState.currentPage) {
        if (!isPlaying || banners.size <= 1) return@LaunchedEffect
        delay(autoScrollIntervalMs)
        val nextPage = (pagerState.currentPage + 1) % banners.size
        pagerState.animateScrollToPage(nextPage, animationSpec = AnimationUtils.standardTween(600))
    }

    Box(modifier = modifier) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
            banners[page]()
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(HierarchicalSize.Padding.Medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CarouselDots(count = banners.size, activeIndex = pagerState.currentPage, dotColor = Color.White)
        }
        CarouselControls(
            isPlaying = isPlaying,
            onPrevious = {
                val target = (pagerState.currentPage - 1 + banners.size) % banners.size
                coroutineScope.launch { pagerState.animateScrollToPage(target, animationSpec = AnimationUtils.standardTween(600)) }
            },
            onNext = {
                val target = (pagerState.currentPage + 1) % banners.size
                coroutineScope.launch { pagerState.animateScrollToPage(target, animationSpec = AnimationUtils.standardTween(600)) }
            },
            onTogglePlaying = { isPlaying = !isPlaying },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(HierarchicalSize.Padding.Medium)
        )
    }
}
