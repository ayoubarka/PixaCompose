package com.pixamob.pixacompose.components.display

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.ButtonColors
import com.pixamob.pixacompose.components.actions.ButtonShape
import com.pixamob.pixacompose.components.actions.ButtonStateColors
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.surfaces.PixaSurfaceCard
import com.pixamob.pixacompose.components.surfaces.SurfaceCardContext
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.theme.baseColor
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.utils.contrastColor
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

/*
 * PixaContentCard — the reusable card family, built on PixaSurfaceCard (components/surfaces/Card.kt).
 * Archetypes below were extracted from real-app card examples cited in
 * justinmind.com/ui-design/cards — grouped by anatomy, not one-per-example, so near-duplicates
 * (every "image + name + price" commerce app, every "image + caption + price" travel app, ...)
 * share one preset:
 *
 *   PixaProductCard  — Amazon-style shopping apps, headphones/shoe/hair-care stores, AliExpress,
 *                       NFT marketplaces, kayaking-gear app (image, price, discount, cart action)
 *   PixaArticleCard  — space-news portal, magazine app, Sky News, streaming service, e-learning
 *                       course cards (image, eyebrow/category, excerpt, metadata)
 *   PixaBookingCard  — hotel/travel booking sites, Skyscanner, Wander, meeting-room booking,
 *                       travel-activity app (image, location/price, rating, CTA)
 *   PixaTaskCard     — Trello (compact kanban task item: label, assignee, due date)
 *   PixaPinCard      — Pinterest (variable-height media tile + overflow menu)
 *   PixaAppCard      — Google Play (compact icon + name + rating tile)
 *   PixaContactCard  — Supperto/Teamup-style contact & conversation rows
 *   PixaStatCard     — Finalytic/Skillex/Savings-app dashboards (KPI, trend, progress)
 *   PixaActionCard   — settings rows, feature-exploration dashboards, mood/session pickers
 *   PixaSelectCard   — travel-activity category toggles, e-learning status filters
 *   PixaMessageCard  — promo/campaign notification card (absorbed from the former MessageCard.kt)
 *
 * Testimonial, pricing, and summary-style cards from the article are documented usage patterns of
 * PixaContentCard's existing slots (see its KDoc) rather than dedicated presets — their anatomy
 * doesn't differ enough from the base to earn a named composable.
 */

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/** Header/body arrangement for [PixaActionCard]. */
enum class ActionCardLayout {
    /** Leading icon/avatar at start, text column filling the rest, optional trailing accessory. */
    Row,

    /** Icon centered above title/description, all text center-aligned. */
    Centered
}

/** Where [MessageCardArtwork] sits relative to [PixaMessageCard]'s text. */
enum class MessageCardArtworkPosition {
    /** Fixed height, full card width, above the text. */
    Top,

    /** Fixed width, height matches text content height. */
    Trailing
}

/** Content-fit mode for [MessageCardArtwork]. */
enum class MessageCardArtworkFit {
    Center,
    ScaleToFill,
    AspectFill,
    AspectFit,
    /** Aspect fill with a caller-supplied [MessageCardArtwork.alignment] (e.g. top/bottom crop bias). */
    AspectFillPositioned
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Media/header-image anatomy for [PixaContentCard]. Bleeds to the card's edges; clipped to the
 * card's top corners (or all corners when [overlayContent] is the card's only content).
 */
@Immutable
@Stable
data class PixaCardMedia(
    val source: PixaImageSource,
    val contentDescription: String? = null,
    val height: Dp = HierarchicalSize.Image.Large,
    val contentScale: ContentScale = ContentScale.Crop,
    /** True renders eyebrow/title/subtitle as a scrim-backed overlay at the media's bottom edge
     * (video/gallery cards) instead of below it. */
    val overlayContent: Boolean = false,
    /** Free-form content positioned within the media bounds (duration chip, play glyph, sale badge, overflow menu). */
    val accessory: (@Composable BoxScope.() -> Unit)? = null
)

/** A single labeled action rendered via [PixaButton] in [PixaContentCard]'s actions row. */
@Immutable
@Stable
data class PixaCardAction(
    val label: String,
    val onClick: () -> Unit,
    val variant: ButtonVariant = ButtonVariant.Filled,
    val enabled: Boolean = true
)

@Immutable
@Stable
data class MessageCardArtwork(
    val source: PixaImageSource,
    val contentDescription: String? = null,
    val position: MessageCardArtworkPosition = MessageCardArtworkPosition.Trailing,
    val fit: MessageCardArtworkFit = MessageCardArtworkFit.AspectFill,
    val alignment: Alignment = Alignment.Center,
    /** Trailing width or top height override; null uses the default for [position]. */
    val size: Dp? = null
)

@Immutable
@Stable
private data class MessageCardColors(
    val text: Color,
    val border: Color?,
    val ctaColors: ButtonStateColors
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER (color/size resolvers)
// ════════════════════════════════════════════════════════════════════════════

private fun MessageCardArtworkFit.toContentScale(): ContentScale = when (this) {
    MessageCardArtworkFit.Center -> ContentScale.None
    MessageCardArtworkFit.ScaleToFill -> ContentScale.FillBounds
    MessageCardArtworkFit.AspectFill, MessageCardArtworkFit.AspectFillPositioned -> ContentScale.Crop
    MessageCardArtworkFit.AspectFit -> ContentScale.Fit
}

private val MessageCardTrailingArtworkWidth = 112.dp
private val MessageCardTopArtworkHeight = 132.dp

/**
 * Text/border/CTA colors tied to background luminance: white or black text by perceptual
 * contrast, a light border only on light backgrounds, and a CTA chip that avoids disappearing
 * on near-white cards.
 */
@Composable
private fun resolveMessageCardColors(backgroundColor: Color, ctaTonal: Boolean): MessageCardColors {
    val textColor = backgroundColor.contrastColor()
    val isLight = textColor == Color.Black

    val border = if (isLight) AppTheme.colors.baseBorderDefault else null

    // "White cards" get a Gray200-equivalent CTA chip instead of white, since an all-white button
    // would disappear on an all-white card.
    val isNearWhiteCard = isLight && backgroundColor.luminance() > 0.9f
    val ctaBackground = if (isNearWhiteCard) baseColor[200]!! else baseColor[50]!!
    val ctaContent = ctaBackground.contrastColor()

    val ctaColors = if (!ctaTonal) {
        ButtonStateColors(
            default = ButtonColors(background = Color.Transparent, content = textColor),
            disabled = ButtonColors(background = Color.Transparent, content = textColor.copy(alpha = 0.4f))
        )
    } else {
        ButtonStateColors(
            default = ButtonColors(background = ctaBackground, content = ctaContent),
            disabled = ButtonColors(background = ctaBackground.copy(alpha = 0.4f), content = ctaContent.copy(alpha = 0.4f))
        )
    }

    return MessageCardColors(text = textColor, border = border, ctaColors = ctaColors)
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun CardMediaBlock(
    media: PixaCardMedia,
    shape: Shape,
    overlayShape: Shape,
    headerContent: (@Composable ColumnScope.() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth().height(media.height)) {
        PixaImage(
            source = media.source,
            contentDescription = media.contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = media.contentScale,
            shape = if (media.overlayContent) overlayShape else shape
        )

        if (media.overlayContent && headerContent != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        // Scrim is always dark by design so overlay text stays legible; not a
                        // repeatable token, just the minimum contrast this one gradient needs.
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)))
                    )
            )
            Column(
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(HierarchicalSize.Spacing.Medium),
                content = headerContent
            )
        }

        media.accessory?.let { accessory ->
            Box(modifier = Modifier.fillMaxSize()) { accessory() }
        }
    }
}

@Composable
private fun CardMetadataRow(items: List<String>, contentColor: Color, modifier: Modifier = Modifier) {
    if (items.isEmpty()) return
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)) {
        items.forEachIndexed { index, item ->
            if (index > 0) BasicText(text = "•", style = AppTheme.typography.captionRegular.copy(color = contentColor))
            BasicText(text = item, style = AppTheme.typography.captionRegular.copy(color = contentColor))
        }
    }
}

@Composable
private fun RowScope.CardActionsRow(primary: PixaCardAction?, secondary: PixaCardAction?) {
    secondary?.let {
        PixaButton(
            text = it.label,
            onClick = it.onClick,
            variant = ButtonVariant.Outlined,
            enabled = it.enabled,
            size = SizeVariant.Small,
            modifier = Modifier.weight(1f)
        )
    }
    primary?.let {
        PixaButton(
            text = it.label,
            onClick = it.onClick,
            variant = it.variant,
            enabled = it.enabled,
            size = SizeVariant.Small,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MessageCardArtworkContent(artwork: MessageCardArtwork, shape: Shape, modifier: Modifier = Modifier) {
    PixaImage(
        source = artwork.source,
        contentDescription = artwork.contentDescription,
        modifier = modifier,
        contentScale = artwork.fit.toContentScale(),
        shape = shape,
        alignment = if (artwork.fit == MessageCardArtworkFit.AspectFillPositioned) artwork.alignment else Alignment.Center
    )
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * The card family's base anatomy — composable slots over `PixaSurfaceCard`, for building a rich
 * card without hand-rolling layout: `media` (top or scrim overlay) → header row (`leading` +
 * `eyebrow`/`title`/`subtitle` + `trailing`) → `body` → `metadata` (auto-joined with "•") →
 * `status` → `primaryAction`/`secondaryAction` row → `footer`. Every region after `media` is
 * independently optional and collapses (no leftover spacing) when omitted.
 *
 * Title/subtitle/body/metadata typography is fixed to one token ladder so every card in the
 * family stays visually consistent — use the `leading`/`eyebrow`/`trailing`/`status`/`footer`
 * slots (drop in `PixaIcon`, `PixaAvatar`, `PixaTag`, `PixaStarRating`, `PixaLinearIndicator`, ...)
 * for anything more custom. Every named preset below (`PixaProductCard`, `PixaArticleCard`, etc.)
 * is a thin wrapper over this — reach for it directly when a preset doesn't fit.
 *
 * @param media Optional header image/artwork; see [PixaCardMedia]
 * @param metadata Optional caption-row items, auto-joined with "•"
 * @param primaryAction / @param secondaryAction Optional action buttons (secondary always outlined)
 * @param footer Optional trailing free-form slot (e.g. an avatar + author row for a quote card)
 * @param semanticsRole Overrides the auto `Role.Button` applied when [onClick] is set
 */
@Composable
fun PixaContentCard(
    modifier: Modifier = Modifier,
    context: SurfaceCardContext = SurfaceCardContext.Isolated,
    media: PixaCardMedia? = null,
    leading: (@Composable () -> Unit)? = null,
    eyebrow: (@Composable () -> Unit)? = null,
    title: String? = null,
    subtitle: String? = null,
    titleMaxLines: Int? = null,
    trailing: (@Composable () -> Unit)? = null,
    body: String? = null,
    bodyMaxLines: Int? = null,
    metadata: List<String>? = null,
    status: (@Composable () -> Unit)? = null,
    primaryAction: PixaCardAction? = null,
    secondaryAction: PixaCardAction? = null,
    footer: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    selected: Boolean = false,
    isLoading: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    backgroundColor: Color = AppTheme.colors.baseSurfaceDefault,
    cornerRadius: Dp = HierarchicalSize.Radius.Massive,
    elevation: ComponentElevation? = null,
    contentPadding: Dp = HierarchicalSize.Spacing.Large,
    semanticsRole: Role? = null
) {
    val shape = RoundedCornerShape(cornerRadius)
    val topShape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
    val hasHeaderContent = leading != null || eyebrow != null || title != null || subtitle != null || trailing != null
    val overlayTextColor = Color.White // see CardMediaBlock: scrim is always dark by construction

    PixaSurfaceCard(
        modifier = modifier.fillMaxWidth(),
        context = context,
        onClick = onClick,
        enabled = enabled,
        selected = selected,
        isLoading = isLoading,
        onDismiss = onDismiss,
        backgroundColor = backgroundColor,
        cornerRadius = cornerRadius,
        elevation = elevation ?: if (context == SurfaceCardContext.Isolated) ComponentElevation.High else ComponentElevation.None,
        contentPadding = 0.dp,
        semanticsRole = semanticsRole
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            media?.let { cardMedia ->
                CardMediaBlock(
                    media = cardMedia,
                    shape = shape,
                    overlayShape = topShape,
                    headerContent = if (cardMedia.overlayContent && hasHeaderContent) {
                        {
                            eyebrow?.invoke()
                            title?.let {
                                BasicText(
                                    text = it,
                                    style = AppTheme.typography.titleBold.copy(color = overlayTextColor),
                                    maxLines = titleMaxLines ?: Int.MAX_VALUE,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            subtitle?.let {
                                BasicText(
                                    text = it,
                                    style = AppTheme.typography.bodyRegular.copy(color = overlayTextColor.copy(alpha = 0.9f))
                                )
                            }
                        }
                    } else null
                )
            }

            val showHeaderInBody = hasHeaderContent && media?.overlayContent != true
            val needsBodyPadding = showHeaderInBody || body != null || metadata != null || status != null ||
                primaryAction != null || secondaryAction != null || footer != null

            if (needsBodyPadding) {
                Column(modifier = Modifier.fillMaxWidth().padding(contentPadding)) {
                    if (showHeaderInBody) {
                        Row(verticalAlignment = Alignment.Top) {
                            leading?.let {
                                it()
                                Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                eyebrow?.let {
                                    it()
                                    Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Nano))
                                }
                                title?.let {
                                    BasicText(
                                        text = it,
                                        style = AppTheme.typography.titleBold.copy(color = AppTheme.colors.baseContentTitle),
                                        maxLines = titleMaxLines ?: Int.MAX_VALUE,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                subtitle?.let {
                                    BasicText(
                                        text = it,
                                        style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentCaption)
                                    )
                                }
                            }
                            trailing?.let {
                                Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))
                                it()
                            }
                        }
                    }

                    body?.let {
                        if (showHeaderInBody) Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Compact))
                        BasicText(
                            text = it,
                            style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody),
                            maxLines = bodyMaxLines ?: Int.MAX_VALUE,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    metadata?.let {
                        if (showHeaderInBody || body != null) Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                        CardMetadataRow(items = it, contentColor = AppTheme.colors.baseContentCaption)
                    }

                    status?.let {
                        if (showHeaderInBody || body != null || metadata != null) {
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                        }
                        it()
                    }

                    if (primaryAction != null || secondaryAction != null) {
                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
                        ) {
                            CardActionsRow(primary = primaryAction, secondary = secondaryAction)
                        }
                    }

                    footer?.let {
                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))
                        it()
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Commerce card. Real examples: shopping/headphones/shoe/hair-care apps, AliExpress deal cards,
 * kayaking-gear app, NFT marketplace listings.
 */
@Composable
fun PixaProductCard(
    media: PixaCardMedia,
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: (@Composable () -> Unit)? = null,
    metadata: List<String>? = null,
    trailing: (@Composable () -> Unit)? = null,
    status: (@Composable () -> Unit)? = null,
    primaryAction: PixaCardAction? = null,
    onClick: (() -> Unit)? = null,
    isLoading: Boolean = false
) = PixaContentCard(
    modifier = modifier,
    media = media,
    eyebrow = eyebrow,
    title = title,
    titleMaxLines = 2,
    metadata = metadata,
    trailing = trailing,
    status = status,
    primaryAction = primaryAction,
    onClick = onClick,
    isLoading = isLoading
)

/**
 * Editorial/media card. Real examples: space-exploration news portal, magazine app, Sky News,
 * streaming-service show cards, e-learning course cards (status as [eyebrow]).
 */
@Composable
fun PixaArticleCard(
    media: PixaCardMedia,
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: (@Composable () -> Unit)? = null,
    body: String? = null,
    metadata: List<String>? = null,
    primaryAction: PixaCardAction? = null,
    onClick: (() -> Unit)? = null,
    isLoading: Boolean = false
) = PixaContentCard(
    modifier = modifier,
    media = media,
    eyebrow = eyebrow,
    title = title,
    titleMaxLines = 2,
    body = body,
    bodyMaxLines = 3,
    metadata = metadata,
    primaryAction = primaryAction,
    onClick = onClick,
    isLoading = isLoading
)

/**
 * Travel/booking card. Real examples: hotel booking site, travel booking site, Skyscanner, Wander,
 * meeting-room booking app, travel-activity app.
 */
@Composable
fun PixaBookingCard(
    media: PixaCardMedia,
    title: String,
    modifier: Modifier = Modifier,
    metadata: List<String>? = null,
    status: (@Composable () -> Unit)? = null,
    primaryAction: PixaCardAction? = null,
    onClick: (() -> Unit)? = null,
    isLoading: Boolean = false
) = PixaContentCard(
    modifier = modifier,
    media = media,
    title = title,
    titleMaxLines = 2,
    metadata = metadata,
    status = status,
    primaryAction = primaryAction,
    onClick = onClick,
    isLoading = isLoading
)

/**
 * Compact kanban task item. Real example: Trello (board columns of draggable task cards).
 * Reordering/dragging is the caller's responsibility (e.g. a `LazyColumn` reorder modifier) — this
 * preset only owns the card's own anatomy, not board/column layout.
 */
@Composable
fun PixaTaskCard(
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: (@Composable () -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    metadata: List<String>? = null,
    onClick: (() -> Unit)? = null,
    isLoading: Boolean = false
) = PixaContentCard(
    modifier = modifier,
    context = SurfaceCardContext.Feed,
    leading = leading,
    eyebrow = eyebrow,
    title = title,
    titleMaxLines = 2,
    metadata = metadata,
    onClick = onClick,
    isLoading = isLoading,
    contentPadding = HierarchicalSize.Spacing.Medium
)

/**
 * Variable-height media tile. Real example: Pinterest (masonry pin grid + overflow menu).
 * Caller controls the masonry effect by varying [media]'s height per item in a staggered grid.
 */
@Composable
fun PixaPinCard(
    media: PixaCardMedia,
    modifier: Modifier = Modifier,
    title: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    isLoading: Boolean = false
) = PixaContentCard(
    modifier = modifier,
    media = media,
    title = title,
    titleMaxLines = 1,
    trailing = trailing,
    onClick = onClick,
    isLoading = isLoading,
    contentPadding = HierarchicalSize.Spacing.Small
)

/**
 * Compact icon + name + rating tile. Real example: Google Play (horizontal-scrolling app rows).
 */
@Composable
fun PixaAppCard(
    leading: @Composable () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    status: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    isLoading: Boolean = false
) = PixaContentCard(
    modifier = modifier,
    context = SurfaceCardContext.Feed,
    leading = leading,
    title = title,
    titleMaxLines = 1,
    status = status,
    onClick = onClick,
    isLoading = isLoading,
    contentPadding = HierarchicalSize.Spacing.Small
)

/**
 * Contact/conversation row. Real examples: Supperto (messaging: contacts, conversations),
 * Teamup (candidate/hiring rows).
 */
@Composable
fun PixaContactCard(
    leading: @Composable () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    metadata: List<String>? = null,
    onClick: (() -> Unit)? = null,
    isLoading: Boolean = false
) = PixaContentCard(
    modifier = modifier,
    context = SurfaceCardContext.Feed,
    leading = leading,
    title = title,
    subtitle = subtitle,
    trailing = trailing,
    metadata = metadata,
    onClick = onClick,
    isLoading = isLoading,
    contentPadding = HierarchicalSize.Spacing.Medium
)

/**
 * Single-KPI dashboard card: icon, label, value, optional trend and/or progress bar. Real
 * examples: Finalytic (payment/transaction stats), Skillex (weekly-progress dashboard), Savings
 * app (goal progress — pass a progress bar + avatar group via [progress]/`footer`, or use
 * [PixaContentCard] directly for the avatar-group footer). For a grouped multi-stat summary, place
 * several `PixaStatCard`s in a `Row` rather than reaching for a dedicated "summary" composable.
 */
@Composable
fun PixaStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trend: (@Composable () -> Unit)? = null,
    progress: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    isLoading: Boolean = false
) = PixaContentCard(
    modifier = modifier,
    leading = leading,
    trailing = trend,
    title = value,
    subtitle = label,
    status = progress,
    onClick = onClick,
    isLoading = isLoading
)

/**
 * Icon/avatar + text card. Real examples: settings rows, feature-exploration dashboards, mood-based
 * activity pickers, tap-to-start session cards. [ActionCardLayout.Row] reads as a leading label
 * (settings, navigation); [ActionCardLayout.Centered] reads as a focal icon (feature callouts, CTAs).
 * For a *selectable* option (not a one-shot navigation tap), use [PixaSelectCard] instead.
 */
@Composable
fun PixaActionCard(
    modifier: Modifier = Modifier,
    layout: ActionCardLayout = ActionCardLayout.Row,
    leading: (@Composable () -> Unit)? = null,
    title: String? = null,
    subtitle: String? = null,
    body: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    primaryAction: PixaCardAction? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    if (layout == ActionCardLayout.Row) {
        PixaContentCard(
            modifier = modifier,
            context = SurfaceCardContext.Feed,
            leading = leading,
            title = title,
            subtitle = subtitle,
            body = body,
            trailing = trailing,
            primaryAction = primaryAction,
            onClick = onClick,
            enabled = enabled,
            isLoading = isLoading,
            contentPadding = HierarchicalSize.Spacing.Medium
        )
    } else {
        PixaContentCard(
            modifier = modifier,
            footer = if (leading != null || title != null || body != null) {
                {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        leading?.let {
                            it()
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))
                        }
                        title?.let {
                            BasicText(
                                text = it,
                                style = AppTheme.typography.titleBold.copy(color = AppTheme.colors.baseContentTitle, textAlign = TextAlign.Center)
                            )
                        }
                        body?.let {
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Compact))
                            BasicText(
                                text = it,
                                style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody, textAlign = TextAlign.Center)
                            )
                        }
                        primaryAction?.let {
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))
                            PixaButton(text = it.label, onClick = it.onClick, variant = it.variant, enabled = it.enabled)
                        }
                    }
                }
            } else null,
            onClick = onClick,
            enabled = enabled,
            isLoading = isLoading
        )
    }
}

/**
 * Selectable/toggle utility card — owns the toggle interaction model via `Role.Checkbox`/
 * `Role.RadioButton` plus `PixaSurfaceCard`'s `selected` accent border, unlike [PixaActionCard]'s
 * plain navigation tap. Real examples: travel-activity category toggles, e-learning status filters.
 *
 * @param multiSelect true -> `Role.Checkbox` (independent toggles); false -> `Role.RadioButton`
 * (mutually exclusive choice, caller owns the exclusivity)
 */
@Composable
fun PixaSelectCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    title: String? = null,
    subtitle: String? = null,
    isSelected: Boolean = false,
    multiSelect: Boolean = true,
    enabled: Boolean = true,
    isLoading: Boolean = false
) = PixaContentCard(
    modifier = modifier,
    context = SurfaceCardContext.Feed,
    leading = leading,
    title = title,
    subtitle = subtitle,
    onClick = onClick,
    enabled = enabled,
    selected = isSelected,
    isLoading = isLoading,
    contentPadding = HierarchicalSize.Spacing.Medium,
    semanticsRole = if (multiSelect) Role.Checkbox else Role.RadioButton
)

/**
 * Promo/campaign notification card: heading, paragraph, optional artwork, CTA whose color adapts
 * to the card's own background luminance. Kept as a distinct preset rather than folded into
 * [PixaContentCard]'s generic slots because of that background-adaptive CTA styling and the
 * top-or-trailing artwork layout, neither of which fit the rest of the family's fixed slot order.
 *
 * @param ctaTonal Filled chip CTA when true; text-only when false
 * @param backgroundColor Accepts any [Color] — message cards lean on the primitive color palette directly
 */
@Composable
fun PixaMessageCard(
    modifier: Modifier = Modifier,
    heading: String? = null,
    paragraph: String? = null,
    headingStyle: TextStyle = AppTheme.typography.titleBold,
    paragraphStyle: TextStyle = AppTheme.typography.bodyRegular,
    headingMaxLines: Int? = null,
    paragraphMaxLines: Int? = null,
    ctaText: String? = null,
    onCtaClick: (() -> Unit)? = null,
    ctaTonal: Boolean = true,
    ctaShape: ButtonShape = ButtonShape.Default,
    artwork: MessageCardArtwork? = null,
    onDismiss: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    backgroundColor: Color = AppTheme.colors.brandSurfaceDefault,
    cornerRadius: Dp = HierarchicalSize.Radius.Medium,
    elevation: ComponentElevation = ComponentElevation.High,
    contentPadding: Dp = HierarchicalSize.Spacing.Medium
) {
    val colors = resolveMessageCardColors(backgroundColor, ctaTonal)
    val shape = RoundedCornerShape(cornerRadius)
    val accessibleLabel = listOfNotNull(heading, paragraph, ctaText).joinToString(". ").ifEmpty { null }

    PixaSurfaceCard(
        modifier = modifier.fillMaxWidth().semantics { accessibleLabel?.let { contentDescription = it } },
        context = SurfaceCardContext.Isolated,
        onClick = onClick,
        enabled = enabled,
        isLoading = isLoading,
        onDismiss = onDismiss,
        backgroundColor = backgroundColor,
        cornerRadius = cornerRadius,
        elevation = elevation,
        borderColor = colors.border ?: Color.Transparent,
        borderWidth = HierarchicalSize.Border.Medium,
        contentPadding = 0.dp
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (artworkRef, textColumn) = createRefs()

            if (artwork != null) {
                val artworkModifier = when (artwork.position) {
                    MessageCardArtworkPosition.Trailing -> Modifier.constrainAs(artworkRef) {
                        width = Dimension.value(artwork.size ?: MessageCardTrailingArtworkWidth)
                        height = Dimension.fillToConstraints
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }

                    MessageCardArtworkPosition.Top -> Modifier.constrainAs(artworkRef) {
                        width = Dimension.fillToConstraints
                        height = Dimension.value(artwork.size ?: MessageCardTopArtworkHeight)
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                }
                MessageCardArtworkContent(artwork = artwork, shape = shape, modifier = artworkModifier)
            }

            Column(
                modifier = Modifier.constrainAs(textColumn) {
                    width = Dimension.fillToConstraints
                    top.linkTo(
                        if (artwork?.position == MessageCardArtworkPosition.Top) artworkRef.bottom else parent.top,
                        margin = contentPadding
                    )
                    bottom.linkTo(parent.bottom, margin = contentPadding)
                    start.linkTo(parent.start, margin = contentPadding)
                    end.linkTo(
                        if (artwork?.position == MessageCardArtworkPosition.Trailing) artworkRef.start else parent.end,
                        margin = contentPadding
                    )
                }
            ) {
                if (heading != null) {
                    BasicText(
                        text = heading,
                        style = headingStyle.copy(color = colors.text),
                        maxLines = headingMaxLines ?: Int.MAX_VALUE,
                        overflow = if (headingMaxLines != null) TextOverflow.Ellipsis else TextOverflow.Clip
                    )
                }

                if (paragraph != null) {
                    if (heading != null) Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Compact))
                    BasicText(
                        text = paragraph,
                        style = paragraphStyle.copy(color = colors.text),
                        maxLines = paragraphMaxLines ?: Int.MAX_VALUE,
                        overflow = if (paragraphMaxLines != null) TextOverflow.Ellipsis else TextOverflow.Clip
                    )
                }

                if (ctaText != null && onCtaClick != null) {
                    // Spec: 4dp top margin for tertiary (text-only) CTAs, 12dp for secondary/pill
                    // CTAs — both land exactly on existing Spacing.Compact/Medium tokens.
                    val ctaTopMargin = if (ctaTonal) HierarchicalSize.Spacing.Medium else HierarchicalSize.Spacing.Compact
                    if (heading != null || paragraph != null) Spacer(modifier = Modifier.height(ctaTopMargin))
                    PixaButton(
                        text = ctaText,
                        onClick = onCtaClick,
                        variant = ButtonVariant.Filled,
                        shape = ctaShape,
                        enabled = enabled,
                        customColors = colors.ctaColors
                    )
                }
            }
        }
    }
}
