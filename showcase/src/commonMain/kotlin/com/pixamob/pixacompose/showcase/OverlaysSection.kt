package com.pixamob.pixacompose.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.Button
import com.pixamob.pixacompose.components.ButtonVariant
import com.pixamob.pixacompose.components.Dialog
import com.pixamob.pixacompose.components.Input
import com.pixamob.pixacompose.components.Menu
import com.pixamob.pixacompose.components.MenuItem
import com.pixamob.pixacompose.components.Popover
import com.pixamob.pixacompose.components.Separator
import com.pixamob.pixacompose.components.Sheet
import com.pixamob.pixacompose.components.ToastState
import com.pixamob.pixacompose.components.ToastVariant
import com.pixamob.pixacompose.components.Tooltip
import com.pixamob.pixacompose.primitives.Icon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.Text
import com.pixamob.pixacompose.theme.PixaTheme

enum class OverlayDemo { Dialog, Sheet, Popover, Menu, Toast }

/** @param initiallyOpen lets a snapshot test start with one overlay already showing. */
@Composable
fun OverlaysSection(toasts: ToastState, initiallyOpen: OverlayDemo? = null) {
    var open by remember { mutableStateOf(initiallyOpen) }
    val close = { open = null }
    val gap = Arrangement.spacedBy(12.dp)

    LaunchedEffect(Unit) {
        if (initiallyOpen == OverlayDemo.Toast) {
            toasts.show("Changes saved", "Your profile was updated.")
            toasts.show("Upload failed", "Check your connection.", ToastVariant.Destructive)
        }
    }

    Text("Overlays", style = PixaTheme.typography.h3)
    Row(horizontalArrangement = gap) {
        Button(onClick = { open = OverlayDemo.Dialog }, variant = ButtonVariant.Outline) { Text("Dialog") }
        Button(onClick = { open = OverlayDemo.Sheet }, variant = ButtonVariant.Outline) { Text("Sheet") }
        Button(
            onClick = { toasts.show("Changes saved", "Your profile was updated.") },
            variant = ButtonVariant.Outline,
        ) { Text("Toast") }
    }
    Row(horizontalArrangement = gap) {
        Box {
            Button(onClick = { open = OverlayDemo.Popover }, variant = ButtonVariant.Outline) { Text("Popover") }
            Popover(visible = open == OverlayDemo.Popover, onDismissRequest = close) {
                Text("Dimensions", style = PixaTheme.typography.label)
                Text(
                    "Set the width of the layer.",
                    style = PixaTheme.typography.bodySmall,
                    color = PixaTheme.colors.mutedForeground,
                )
                var width by remember { mutableStateOf("100%") }
                Input(width, { width = it })
            }
        }
        Box {
            Button(onClick = { open = OverlayDemo.Menu }, variant = ButtonVariant.Outline) {
                Text("Menu")
                Icon(PixaIcons.ChevronDown, contentDescription = null, size = 16.dp)
            }
            Menu(visible = open == OverlayDemo.Menu, onDismissRequest = close) {
                MenuItem(onClick = close) { Icon(PixaIcons.Plus, null, size = 16.dp); Text("New file") }
                MenuItem(onClick = close) { Icon(PixaIcons.Check, null, size = 16.dp); Text("Mark as done") }
                MenuItem(onClick = close, enabled = false) { Text("Archive") }
                Separator()
                MenuItem(onClick = close) { Icon(PixaIcons.Close, null, size = 16.dp); Text("Delete") }
            }
        }
        Tooltip(tooltip = { Text("Hover or long-press") }) {
            Button(onClick = {}, variant = ButtonVariant.Outline) { Text("Tooltip") }
        }
    }

    Dialog(visible = open == OverlayDemo.Dialog, onDismissRequest = close) {
        Text("Delete project?", style = PixaTheme.typography.h3)
        Text(
            "This removes the project and all of its files. It cannot be undone.",
            style = PixaTheme.typography.bodySmall,
            color = PixaTheme.colors.mutedForeground,
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)) {
            Button(onClick = close, variant = ButtonVariant.Outline) { Text("Cancel") }
            Button(onClick = close, variant = ButtonVariant.Destructive) { Text("Delete") }
        }
    }

    Sheet(visible = open == OverlayDemo.Sheet, onDismissRequest = close) {
        Text("Share", style = PixaTheme.typography.h3)
        Text(
            "Anyone with the link can view this file.",
            style = PixaTheme.typography.bodySmall,
            color = PixaTheme.colors.mutedForeground,
        )
        Button(onClick = close, modifier = Modifier.fillMaxWidth()) { Text("Copy link") }
        Button(onClick = close, modifier = Modifier.fillMaxWidth(), variant = ButtonVariant.Ghost) { Text("Cancel") }
    }
}
