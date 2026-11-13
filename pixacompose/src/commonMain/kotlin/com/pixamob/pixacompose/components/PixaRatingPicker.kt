package com.pixamob.pixacompose.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.LocalPixaContentColor
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.theme.PixaTheme

private const val SelectedRatingScale = 1.1f

/**
 * A row of square buttons for a rating from 1 to [max], such as mood or severity. Each button
 * shows [item] for its value: the number by default, or a [PixaRatingFace] for a mood scale.
 * [value] 0 means nothing is chosen yet.
 *
 * ```
 * PixaRatingPicker(mood, { mood = it }, Modifier.fillMaxWidth()) { PixaRatingFace(it) }
 * ```
 */
@Composable
public fun PixaRatingPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    max: Int = 5,
    color: Color = PixaTheme.colors.primary,
    enabled: Boolean = true,
    item: @Composable (value: Int) -> Unit = { PixaText("$it") },
) {
    val colors = PixaTheme.colors
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(PixaTheme.spacing.sm)) {
        for (step in 1..max) {
            val selected = value == step
            val scale by animateFloatAsState(if (selected) SelectedRatingScale else 1f, PixaTheme.motion.state.spec())
            PixaSurface(
                selected = selected,
                onClick = { onValueChange(step) },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .semantics { contentDescription = "$step of $max" },
                enabled = enabled,
                role = Role.RadioButton,
                color = if (selected) tintFill(color) else colors.card,
                contentColor = if (selected) color else colors.cardForeground,
                shape = PixaTheme.shapes.md,
                border = if (selected) selectedBorder(color) else outlinedBorder(),
                shadow = PixaTheme.decoration.controlShadow,
                contentAlignment = Alignment.Center,
            ) {
                ProvideContentStyle(if (selected) color else colors.cardForeground, PixaTheme.typography.h2) { item(step) }
            }
        }
    }
}

/**
 * A line-drawn face for [value] on a scale of 1 to [max]: a frown at 1, a straight mouth in the
 * middle and a smile at [max]. Drawn in the content color, so it needs no emoji font.
 */
@Composable
public fun PixaRatingFace(
    value: Int,
    modifier: Modifier = Modifier,
    max: Int = 5,
    size: Dp = 28.dp,
    color: Color = Color.Unspecified,
) {
    val tint = color.takeOrElse { LocalPixaContentColor.current }
    // -1 is the deepest frown, 1 the widest smile.
    val mood = if (max <= 1) 1f else ((value - 1).toFloat() / (max - 1)) * 2f - 1f
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val stroke = w / 14f
        drawCircle(tint, radius = w / 2f - stroke / 2f, style = Stroke(stroke))
        drawCircle(tint, radius = stroke * 0.8f, center = Offset(w * 0.36f, w * 0.40f))
        drawCircle(tint, radius = stroke * 0.8f, center = Offset(w * 0.64f, w * 0.40f))
        val mouthY = w * 0.66f - mood * w * 0.04f
        val mouth = Path().apply {
            moveTo(w * 0.32f, mouthY)
            quadraticTo(w * 0.5f, mouthY + mood * w * 0.18f, w * 0.68f, mouthY)
        }
        drawPath(mouth, tint, style = Stroke(stroke, cap = StrokeCap.Round))
    }
}
