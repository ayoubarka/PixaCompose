package com.pixamob.pixacompose.charts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.decoration.Decoration
import com.patrykandpatrick.vico.compose.cartesian.decoration.HorizontalBox
import com.patrykandpatrick.vico.compose.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.component.LineComponent
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.pixamob.pixacompose.theme.PixaTheme

/** How strongly a band's color shows behind the data. */
private const val BandAlpha = 0.14f

/** [guides] as Vico decorations: solid lines and soft bands in the theme's colors. */
@Composable
internal fun rememberDecorations(guides: List<PixaChartGuide>, formatValue: (Double) -> String): List<Decoration> {
    if (guides.isEmpty()) return emptyList()
    val colors = PixaTheme.colors
    // On a card-colored chip so it reads over bars and lines.
    val label = rememberTextComponent(
        style = PixaTheme.typography.caption.copy(color = colors.foreground),
        padding = Insets(PixaTheme.spacing.xs, 2.dp),
        margins = Insets(PixaTheme.spacing.xs),
        background = rememberShapeComponent(fill = Fill(colors.card), shape = PixaTheme.shapes.sm),
    )
    // Remembered so the chart is not rebuilt on every recomposition.
    return remember(guides, colors, label, formatValue) { guides.map { guide ->
        when (guide) {
            is PixaChartGuide.Line -> {
                val color = guide.color.takeIf { it != Color.Unspecified } ?: colors.foreground
                val y = guide.y.toDouble()
                HorizontalLine(
                    y = { y },
                    // Solid, so it stands apart from the dashed grid.
                    line = LineComponent(Fill(color), thickness = 2.dp),
                    labelComponent = label,
                    label = { guide.label ?: formatValue(y) },
                )
            }
            is PixaChartGuide.Band -> {
                val color = guide.color.takeIf { it != Color.Unspecified } ?: colors.primary
                val range = guide.from.toDouble()..guide.to.toDouble()
                HorizontalBox(
                    y = { range },
                    box = ShapeComponent(Fill(color.copy(alpha = color.alpha * BandAlpha))),
                    labelComponent = guide.label?.let { label },
                    label = { guide.label.orEmpty() },
                )
            }
        }
    } }
}
