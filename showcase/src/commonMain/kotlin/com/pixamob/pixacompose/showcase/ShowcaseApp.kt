package com.pixamob.pixacompose.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.PixaButton
import com.pixamob.pixacompose.components.PixaButtonSize
import com.pixamob.pixacompose.components.PixaButtonVariant
import com.pixamob.pixacompose.components.PixaCard
import com.pixamob.pixacompose.components.PixaToastHost
import com.pixamob.pixacompose.components.rememberPixaToastState
import com.pixamob.pixacompose.primitives.PixaIcon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.styles.PixaStyles
import com.pixamob.pixacompose.theme.PixaStyle
import com.pixamob.pixacompose.theme.PixaTheme

@Composable
fun ShowcaseApp(
    initialStyle: PixaStyle = PixaStyles.Minimal,
    initialDark: Boolean = false,
    /** Lets a snapshot test put keyboard focus on the card's button to show the focus ring. */
    focusDemo: FocusRequester = remember { FocusRequester() },
    /** Tells the host app which theme is showing, so it can match the system bar icons. */
    onDarkChange: (Boolean) -> Unit = {},
) {
    var style by remember { mutableStateOf(initialStyle) }
    var dark by remember { mutableStateOf(initialDark) }

    LaunchedEffect(dark) { onDarkChange(dark) }

    val toasts = rememberPixaToastState()
    PixaTheme(style = style, darkTheme = dark) {
        Box(Modifier.fillMaxSize().background(PixaTheme.colors.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            PixaText("PixaCompose", style = PixaTheme.typography.h1)
            PixaText(
                "Same components, different style object.",
                style = PixaTheme.typography.bodySmall,
                color = PixaTheme.colors.mutedForeground,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PixaStyles.all.forEach { candidate ->
                    PixaButton(
                        onClick = { style = candidate },
                        variant = if (candidate == style) PixaButtonVariant.Primary else PixaButtonVariant.Outline,
                        size = PixaButtonSize.Sm,
                    ) { PixaText(candidate.name) }
                }
            }
            PixaButton(onClick = { dark = !dark }, variant = PixaButtonVariant.Ghost, size = PixaButtonSize.Sm) {
                PixaText(if (dark) "Switch to light" else "Switch to dark")
            }

            PixaText("Button", style = PixaTheme.typography.h3)
            PixaButtonVariant.entries.forEach { variant ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PixaButton(onClick = {}, variant = variant) { PixaText(variant.name) }
                    PixaButton(onClick = {}, variant = variant, enabled = false) { PixaText("Disabled") }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaButtonSize.entries.forEach { size ->
                    PixaButton(onClick = {}, size = size) { PixaText(size.name) }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaButton(onClick = {}) {
                    PixaIcon(PixaIcons.Plus, contentDescription = null)
                    PixaText("Add item")
                }
                PixaButton(onClick = {}, variant = PixaButtonVariant.Outline) {
                    PixaText("Next")
                    PixaIcon(PixaIcons.ChevronRight, contentDescription = null)
                }
                PixaButton(onClick = {}, variant = PixaButtonVariant.Secondary) {
                    PixaIcon(PixaIcons.Close, contentDescription = "Close")
                }
            }

            PixaText("Icon", style = PixaTheme.typography.h3)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf(
                    PixaIcons.Check, PixaIcons.Close, PixaIcons.Plus, PixaIcons.Minus,
                    PixaIcons.ChevronLeft, PixaIcons.ChevronDown, PixaIcons.ChevronRight,
                ).forEach { PixaIcon(it, contentDescription = null, size = 24.dp) }
                PixaIcon(PixaIcons.Check, contentDescription = null, size = 24.dp, tint = PixaTheme.colors.destructive)
            }

            PixaText("Card", style = PixaTheme.typography.h3)
            var count by remember { mutableIntStateOf(0) }
            PixaCard(modifier = Modifier.fillMaxWidth()) {
                PixaText("Counter", style = PixaTheme.typography.h3)
                PixaText(
                    "Pressed $count times.",
                    style = PixaTheme.typography.bodySmall,
                    color = PixaTheme.colors.mutedForeground,
                )
                PixaButton(onClick = { count++ }, modifier = Modifier.focusRequester(focusDemo)) { PixaText("Press me") }
            }

            // Colored tiles: the colors are the app's choice, here taken from the theme.
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaCard(Modifier.weight(1f), color = PixaTheme.colors.primary, contentColor = PixaTheme.colors.primaryForeground) {
                    PixaText("Steps", style = PixaTheme.typography.caption)
                    PixaText("8,752", style = PixaTheme.typography.h2)
                }
                PixaCard(Modifier.weight(1f), color = PixaTheme.colors.secondary, contentColor = PixaTheme.colors.secondaryForeground) {
                    PixaText("Sleep", style = PixaTheme.typography.caption)
                    PixaText("7h 20m", style = PixaTheme.typography.h2)
                }
            }

            OverlaysSection(toasts)

            ControlsSection()

            FormsSection()

            ImageSection()

            ChartsSection()

            DateSection()

            NavigationSection()
        }
        PixaToastHost(toasts, Modifier.align(Alignment.BottomCenter).safeDrawingPadding())
        }
    }
}
