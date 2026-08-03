package com.pixamob.pixacompose.components.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.util.lerp as lerpFloat
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import kotlin.math.abs

// ════════════════════════════════════════════════════════════════════════════
// GEOMETRY
// ════════════════════════════════════════════════════════════════════════════

/**
 * Builds the bar's outline: a rounded rectangle whose TOP edge dips into one
 * smooth concave notch centred at [cx]. As [cx] animates between slots the notch
 * travels along the edge, reading as a single liquid deformation (old notch
 * closing while the new one opens).
 *
 * All values are pixels. [barTop]..[bottom] is the bar body; the notch valley
 * reaches [notchDepth] below [barTop], [notchHalf] wide on each side, with
 * [shoulder] controlling how softly the edge eases into the dip.
 */
private fun buildNotchedBarPath(
    path: Path,
    width: Float,
    barTop: Float,
    bottom: Float,
    corner: Float,
    cx: Float,
    notchHalf: Float,
    notchDepth: Float,
    shoulder: Float,
) {
    val valleyY = barTop + notchDepth
    path.reset()
    path.moveTo(corner, barTop)
    // Top edge up to the left shoulder of the notch.
    path.lineTo(cx - notchHalf, barTop)
    // Down into the valley, flat at the bottom, then back up to the right shoulder.
    path.cubicTo(cx - notchHalf + shoulder, barTop, cx - shoulder, valleyY, cx, valleyY)
    path.cubicTo(cx + shoulder, valleyY, cx + notchHalf - shoulder, barTop, cx + notchHalf, barTop)
    // Top edge to the top-right corner.
    path.lineTo(width - corner, barTop)
    path.quadraticTo(width, barTop, width, barTop + corner)
    // Right, bottom, left edges with rounded corners.
    path.lineTo(width, bottom - corner)
    path.quadraticTo(width, bottom, width - corner, bottom)
    path.lineTo(corner, bottom)
    path.quadraticTo(0f, bottom, 0f, bottom - corner)
    path.lineTo(0f, barTop + corner)
    path.quadraticTo(0f, barTop, corner, barTop)
    path.close()
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC - Main component
// ════════════════════════════════════════════════════════════════════════════

/**
 * Liquid bottom navigation bar — a floating rounded bar whose top edge carries a
 * single fluid notch, with a solid indicator "ball" nested in it above the active
 * tab. Selecting a tab slides the ball to the new slot while the notch morphs
 * along the edge, all in one spring.
 *
 * ### Anatomy
 * Rounded bar ([barColor]) with a subtle drop shadow → one concave notch on the
 * top edge at the active slot → a [barColor] indicator ball whose lower third
 * nests in the notch → a row of evenly-spaced filled icons with a label that
 * shows only on the active tab.
 *
 * ### Colours (theme-mapped)
 * Bar + ball = [barColor] (surface). Active icon/label = [activeColor] (brand).
 * Inactive icons = [inactiveColor]. The reference's royal blue maps to the app's
 * brand colour; its white bar maps to the app surface.
 *
 * ### Animation
 * A single medium-bouncy spring (~450ms, slight overshoot/wobble) drives every
 * property in parallel — no stagger: ball slide, notch morph, icon colour
 * crossfade, icon scale and label crossfade, all interpolated from each slot's
 * distance to the animated position.
 *
 * ### Sizing
 * [size] drives icon size, bar height ([HierarchicalSize.BottomNav]) and the ball
 * (≈ 2× icon). Works with 2–5 evenly distributed items.
 *
 * @param items Navigation items (2+); [NavItem.title] is the label
 * @param selectedIndex 0-based active index
 * @param onItemSelected Callback receiving the tapped index
 * @param size Icon/bar/ball size preset
 * @param cornerRadius Bar corner radius
 * @param barColor Bar + indicator ball colour
 * @param activeColor Active icon/label colour
 * @param inactiveColor Inactive icon colour
 */
@Composable
fun PixaLiquidNavBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium,
    cornerRadius: Dp = HierarchicalSize.Radius.Massive,
    barColor: Color = AppTheme.colors.baseSurfaceDefault,
    activeColor: Color = AppTheme.colors.brandContentDefault,
    inactiveColor: Color = AppTheme.colors.baseContentBody.copy(alpha = 0.5f),
) {
    require(items.size >= 2) {
        "PixaLiquidNavBar requires at least 2 items. Provided: ${items.size}"
    }
    require(selectedIndex in items.indices) {
        "selectedIndex $selectedIndex is out of bounds for ${items.size} items (0-${items.lastIndex})"
    }

    val density = LocalDensity.current
    val itemCount = items.size

    // Dimensions.
    val iconSize = HierarchicalSize.Icon.forVariant(size)
    val barBodyHeight = HierarchicalSize.BottomNav.forVariant(size)
    val ballRadius = iconSize                     // diameter ≈ 2× icon
    val ballNest = ballRadius * 0.66f             // how far the ball dips below the bar top
    val ballAbove = ballRadius - ballNest         // ball centre above the bar top
    val protrusion = ballAbove + ballRadius       // ball extent above the bar top
    val totalHeight = protrusion + barBodyHeight + HierarchicalSize.Spacing.Small
    val contentTopPadding = ballNest + HierarchicalSize.Spacing.Compact

    // Pixel values for the canvas / path.
    val barBodyPx = with(density) { barBodyHeight.toPx() }
    val ballRadiusPx = with(density) { ballRadius.toPx() }
    val ballAbovePx = with(density) { ballAbove.toPx() }
    val notchDepthPx = with(density) { ballNest.toPx() }
    val notchHalfPx = with(density) { (ballRadius * 1.3f).toPx() }
    val shoulderPx = with(density) { (ballRadius * 0.5f).toPx() }
    val cornerPx = with(density) { cornerRadius.toPx() }

    // One spring drives the whole transition (medium-bouncy, slight overshoot).
    val animatedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = AnimationUtils.standardSpring(stiffness = Spring.StiffnessMediumLow),
        label = "liquidNavIndex"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(totalHeight)
    ) {
        // Bar + notch + indicator ball.
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val slotWidth = w / itemCount
            val cx = slotWidth * (animatedIndex + 0.5f)
            val barTop = this.size.height - barBodyPx
            val ballCenterY = barTop - ballAbovePx

            val path = Path()
            buildNotchedBarPath(
                path = path,
                width = w,
                barTop = barTop,
                bottom = this.size.height,
                corner = cornerPx,
                cx = cx,
                notchHalf = notchHalfPx,
                notchDepth = notchDepthPx,
                shoulder = shoulderPx,
            )

            // Subtle drop shadow: a few translucent copies fanned downward.
            listOf(2f, 5f, 9f, 14f).forEach { dy ->
                translate(top = dy) {
                    drawPath(path, color = Color.Black.copy(alpha = 0.045f))
                }
            }
            drawPath(path, color = barColor)
            // Indicator ball — pure fill, no border/shadow.
            drawCircle(color = barColor, radius = ballRadiusPx, center = Offset(cx, ballCenterY))
        }

        // Icons + labels over the bar body.
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(barBodyHeight)
                .padding(top = contentTopPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                // 1 at the active slot, easing to 0 one slot away — drives the
                // parallel colour/scale/label crossfade as the ball passes.
                val fraction = (1f - abs(animatedIndex - index)).coerceIn(0f, 1f)
                LiquidNavSlot(
                    item = item,
                    fraction = fraction,
                    selected = index == selectedIndex,
                    iconSize = iconSize,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    onClick = { onItemSelected(index) },
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
        }
    }
}

/**
 * One slot: filled icon (colour/scale crossfaded by [fraction]) with a label that
 * fades in only near the active position. Label space is always reserved so icons
 * stay aligned across slots.
 */
@Composable
private fun LiquidNavSlot(
    item: NavItem,
    fraction: Float,
    selected: Boolean,
    iconSize: Dp,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint = lerp(inactiveColor, activeColor, fraction)
    val iconScale = lerpFloat(1f, 1.12f, fraction)

    Box(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = item.enabled,
                role = Role.Tab,
                onClick = onClick
            )
            .semantics {
                role = Role.Tab
                this.selected = selected
                contentDescription = item.contentDescription ?: item.title
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PixaIcon(
                painter = if (fraction > 0.5f) item.iconSelected else item.iconUnselected,
                contentDescription = null,
                modifier = Modifier.size(iconSize).scale(iconScale),
                tint = tint
            )
            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Nano))
            BasicText(
                text = item.title,
                style = AppTheme.typography.labelMedium.copy(
                    color = activeColor.copy(alpha = fraction),
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
