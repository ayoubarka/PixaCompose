package com.pixamob.pixacompose.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.pixaRipple

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

// No custom types — sizing uses the shared [SizeVariant] ladder.

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaSwatchSelector — a horizontally scrollable row of selectable color circles.
 *
 * Use this when the user needs to pick from a preset palette (e.g. pet urine colour,
 * stool colour, category colour). For arbitrary color selection, use [PixaColorPicker].
 *
 * ### Anatomy
 * Circular swatches laid out left-to-right with a fixed gap. The selected swatch
 * shows a ring around it; unselected swatches have no ring.
 *
 * ### Sizing
 * [SizeVariant.Small] → 32dp swatch  
 * [SizeVariant.Medium] → 48dp swatch  
 * [SizeVariant.Large] → 64dp swatch
 *
 * @param colors Preset colors to display
 * @param selected Currently selected color, or null for none
 * @param onSelected Callback invoked when a swatch is tapped
 * @param modifier Modifier for the row
 * @param size Size preset (Default: [SizeVariant.Medium])
 */
@Composable
fun PixaSwatchSelector(
    colors: List<Color>,
    selected: Color?,
    onSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium
) {
    val swatchSize = when (size) {
        SizeVariant.Small, SizeVariant.Compact -> HierarchicalSize.Container.Small
        SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> HierarchicalSize.Container.Large
        else -> HierarchicalSize.Container.Medium
    }
    val ringWidth = HierarchicalSize.Border.Large
    val ringPadding = HierarchicalSize.Spacing.Compact

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
    ) {
        colors.forEach { color ->
            val isSelected = color == selected
            Box(
                modifier = Modifier
                    .size(swatchSize + if (isSelected) ringPadding * 2 else 0.dp)
                    .clip(CircleShape)
                    .then(
                        if (isSelected) {
                            Modifier.border(ringWidth, AppTheme.colors.brandContentDefault, CircleShape)
                                .padding(ringPadding)
                        } else {
                            Modifier
                        }
                    )
                    .clip(CircleShape)
                    .background(color)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = pixaRipple(bounded = true),
                        role = Role.Button,
                        onClick = { onSelected(color) }
                    )
                    .semantics {
                        contentDescription = "Color swatch"
                        this.selected = isSelected
                    }
            )
        }
    }
}
