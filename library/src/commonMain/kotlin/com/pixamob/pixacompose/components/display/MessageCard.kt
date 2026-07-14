package com.pixamob.pixacompose.components.display

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.Dimension
import com.pixamob.pixacompose.components.actions.ButtonColors
import com.pixamob.pixacompose.components.actions.ButtonShape
import com.pixamob.pixacompose.components.actions.ButtonStateColors
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.theme.baseColor
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.utils.contrastColor
import com.pixamob.pixacompose.utils.toDp

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/** Where the artwork sits relative to the text content, per Uber Base's two supported layouts. */
enum class MessageCardArtworkPosition {
    /** Fixed height (132dp default), full card width, stacked above the text content. */
    Top,

    /** Fixed width (112dp default), height matches the text content's resolved height. */
    Trailing
}

/**
 * Content-fit mode for [MessageCardArtwork], mirroring Uber Base's five fit behaviors.
 * Maps onto Compose's [ContentScale] via [toContentScale].
 */
enum class MessageCardArtworkFit {
    /** Centered, no stretch. */
    Center,

    /** Stretches to fill the container, ignoring aspect ratio. */
    ScaleToFill,

    /** Fills the shortest dimension, cropping excess (default). */
    AspectFill,

    /** Fills the longest dimension, preserving aspect ratio, may letterbox. */
    AspectFit,

    /** Aspect fill with a caller-supplied [MessageCardArtwork.alignment] (e.g. top/bottom crop bias). */
    AspectFillPositioned
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class MessageCardArtwork(
    val source: PixaImageSource,
    val contentDescription: String? = null,
    val position: MessageCardArtworkPosition = MessageCardArtworkPosition.Trailing,
    val fit: MessageCardArtworkFit = MessageCardArtworkFit.AspectFill,
    /** Only consulted when [fit] is [MessageCardArtworkFit.AspectFillPositioned]. */
    val alignment: Alignment = Alignment.Center,
    /** Trailing width or top height override; null uses the spec default for [position]. */
    val size: Dp? = null
)

@Immutable
@Stable
private data class MessageCardColors(
    val text: Color,
    val border: Color?,
    val ctaColors: ButtonStateColors,
    val dismissBackground: Color,
    val dismissContent: Color,
    val pressOverlay: Color
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

// Uber spec defaults with no matching HierarchicalSize.Image/Card tier (that ladder jumps
// 80dp -> 120dp, straddling both without landing near either) — kept local and overridable
// via MessageCardArtwork.size per the tokenization rule's one-off-value exception.
private val MessageCardTrailingArtworkWidth = 112.dp
private val MessageCardTopArtworkHeight = 132.dp

/**
 * Resolves every color decision the spec ties to background luminance:
 * - text: white on background luminance >= ~600-shade darkness, black on <= ~400-shade (else white)
 * - border: 2dp opaque on light backgrounds, none on dark (spec leaves dark unspecified)
 * - CTA: white chip on colored/dark cards, Gray200 chip on near-white cards; content auto-contrasts
 * - dismiss chip: white-60%-opacity backing on light cards, black-40%-opacity on dark cards
 * - press overlay: 8% black on light cards, 20% white on dark cards
 *
 * [Color.contrastColor] (a perceptual-luminance threshold, see utils/ColorUtils.kt) stands in for
 * the spec's raw primitive-shade thresholds since callers pass a resolved [Color], not a shade index.
 */
@Composable
private fun resolveMessageCardColors(backgroundColor: Color, ctaTonal: Boolean): MessageCardColors {
    val textColor = backgroundColor.contrastColor()
    val isLight = textColor == Color.Black

    val border = if (isLight) AppTheme.colors.baseBorderDefault else null

    // "White cards" (near-white background) get a Gray200-equivalent CTA chip instead of white,
    // since an all-white button would disappear on an all-white card. Luminance > 0.9 approximates
    // "white/off-white" more tightly than the general light/dark split used for text color, because
    // the spec has no exact numeric definition of "white" vs. merely "light".
    val isNearWhiteCard = isLight && backgroundColor.luminance() > 0.9f
    val ctaBackground = if (isNearWhiteCard) baseColor[200]!! else baseColor[50]!!
    val ctaContent = ctaBackground.contrastColor()

    val ctaColors = if (!ctaTonal) {
        // Ghost/tertiary CTA: text-only, tinted with the same auto-contrast color as the body text.
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

    val dismissBackground = if (isLight) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.4f)
    val pressOverlay = if (isLight) Color.Black.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.20f)

    return MessageCardColors(
        text = textColor,
        border = border,
        ctaColors = ctaColors,
        dismissBackground = dismissBackground,
        dismissContent = textColor,
        pressOverlay = pressOverlay
    )
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL COMPONENT
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun MessageCardDismissButton(
    onDismiss: () -> Unit,
    colors: MessageCardColors,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(HierarchicalSize.TouchTarget.Small)
            .clip(CircleShape)
            .background(colors.dismissBackground)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onDismiss
            )
            .semantics {
                contentDescription = "Dismiss"
                role = Role.Button
            },
        contentAlignment = Alignment.Center
    ) {
        // No dedicated close-icon asset exists in the library yet (Alert.kt's dismiss button
        // has the same gap); a glyph avoids pulling in a new icon dependency for one character.
        BasicText(
            text = "×",
            style = AppTheme.typography.titleBold.copy(color = colors.dismissContent, textAlign = TextAlign.Center)
        )
    }
}

@Composable
private fun MessageCardArtworkContent(
    artwork: MessageCardArtwork,
    shape: Shape,
    modifier: Modifier = Modifier
) {
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
 * PixaMessageCard - campaign/engagement preview card, migrated from Uber Base's Message Card spec.
 *
 * Purpose: "a snapshot-like preview of a message intended to encourage users to click or tap to
 * view more details" — for engagement messaging, brand campaigns, safety initiatives, partnerships,
 * feature launches, loyalty rewards, and promotions. Not for system messaging (use an Alert/Banner)
 * or ephemeral feedback like "Copied to clipboard" (use a Toast/Snackbar) — this component carries
 * a campaign/advertising aesthetic and risks "banner blindness" if used for those instead.
 *
 * Anatomy: heading, paragraph, CTA button, artwork (top or trailing), and dismiss button — every
 * part is independently optional ("all parts... can be configured on and off").
 *
 * States: enabled/disabled/pressed. Pressed shows an 8%-black (light card) or 20%-white (dark card)
 * overlay across the whole tap target. No hover/focus-ring modeling (touch-first target platforms).
 *
 * Sizing: [artwork] defaults to 112dp trailing width / 132dp top height per spec, both overridable
 * via [MessageCardArtwork.size]. [cornerRadius]/[elevation] reuse existing `HierarchicalSize` tokens;
 * the spec's exact "16px blur" isn't expressible through Compose's built-in shadow (elevation-driven,
 * not blur-driven) — [elevation] defaults to [ComponentElevation.High] (4dp) as the closest stand-in
 * for the spec's 4px Y-offset, while the 12%-black shadow color is applied exactly.
 *
 * Adaptive behavior: out of scope — the spec's breakpoint guidance is about grid column spans at the
 * screen/layout level (4-column small, gridded medium, 3-column large), not the card's own internal
 * sizing, so it doesn't map onto `WindowSizeClass`/`AppTheme.adaptiveSizeVariant`. Callers control
 * column span via their own layout; the card fills whatever width it's given.
 *
 * Customization: [backgroundColor] intentionally accepts any [Color], not just semantic
 * `AppTheme.colors.*` tokens — per spec, "message cards leverage more colors in the primitive color
 * palette... locally contained to the message component," so pulling directly from this library's
 * `baseColor`/`brandColor`/etc. primitive maps (see theme/Color.kt) is the sanctioned pattern here,
 * not a violation of the project's usual color-token rule.
 *
 * Usage notes: headline/paragraph default to wrapping; truncating them is technically supported via
 * [headingMaxLines]/[paragraphMaxLines] but discouraged per spec since enlarged-text accessibility
 * users may lose content. The whole card is one tap target when [onClick] is set — nested [ctaText]/
 * [onDismiss] controls remain independently tappable on top of it.
 *
 * @param modifier Modifier for the card
 * @param heading Optional heading text
 * @param paragraph Optional supporting body text
 * @param headingStyle Heading text style
 * @param paragraphStyle Paragraph text style
 * @param headingMaxLines Optional truncation override; null wraps freely (spec default/recommendation)
 * @param paragraphMaxLines Optional truncation override; null wraps freely (spec default/recommendation)
 * @param ctaText Optional CTA label; CTA is omitted entirely when null
 * @param onCtaClick Required alongside [ctaText] to render the CTA
 * @param ctaTonal Filled chip CTA (secondary/pill per spec) when true; text-only tertiary CTA when false
 * @param ctaShape [ButtonShape.Default] or [ButtonShape.Pill] per spec's secondary/pill CTA styles
 * @param artwork Optional artwork; position/fit/size configured via [MessageCardArtwork]
 * @param onDismiss Optional dismiss handler; dismiss button is omitted entirely when null
 * @param onClick Optional tap handler for the whole card surface
 * @param enabled Disabled state (renders a scrim, disables tap targets)
 * @param isLoading Shows a skeleton placeholder via the underlying [PixaCard]
 * @param backgroundColor Card background; see Customization above for the primitive-palette allowance
 * @param cornerRadius Corner radius; spec doesn't state a value, defaults to the shared card radius token
 * @param elevation Shadow elevation; see Sizing above for the blur-vs-elevation caveat
 * @param contentPadding Padding around heading/paragraph/CTA; artwork and dismiss bleed to the edges
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

    PixaCard(
        modifier = modifier.fillMaxWidth(),
        variant = BaseCardVariant.Elevated,
        onClick = null,
        enabled = enabled,
        isLoading = isLoading,
        padding = SizeVariant.None,
        cornerRadius = cornerRadius,
        backgroundColor = backgroundColor,
        borderConfig = CardBorderConfig(color = colors.border ?: Color.Transparent, width = HierarchicalSize.Border.Medium),
        shadowConfig = CardShadowConfig(elevation = elevation.toDp(), color = Color.Black.copy(alpha = 0.12f))
    ) {
        val (tapOverlay, artworkRef, textColumn, dismissRef) = createRefs()

        if (onClick != null && enabled) {
            val interactionSource = remember { MutableInteractionSource() }
            val pressed by interactionSource.collectIsPressedAsState()
            Box(
                modifier = Modifier
                    .constrainAs(tapOverlay) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                    .background(if (pressed) colors.pressOverlay else Color.Transparent)
                    .semantics {
                        role = Role.Button
                        accessibleLabel?.let { contentDescription = it }
                    }
            )
        }

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
            modifier = Modifier
                .constrainAs(textColumn) {
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
                // Spec: 4dp top margin for tertiary (text-only) CTAs, 12dp for secondary/pill CTAs —
                // both land exactly on existing Spacing.Compact/Medium tokens.
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

        if (onDismiss != null) {
            MessageCardDismissButton(
                onDismiss = onDismiss,
                colors = colors,
                modifier = Modifier.constrainAs(dismissRef) {
                    top.linkTo(parent.top, margin = HierarchicalSize.Spacing.Small)
                    end.linkTo(parent.end, margin = HierarchicalSize.Spacing.Small)
                }
            )
        }
    }
}
