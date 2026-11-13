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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.PixaButton
import com.pixamob.pixacompose.components.PixaButtonVariant
import com.pixamob.pixacompose.components.PixaDialog
import com.pixamob.pixacompose.components.PixaField
import com.pixamob.pixacompose.components.PixaInput
import com.pixamob.pixacompose.components.PixaMenu
import com.pixamob.pixacompose.components.PixaMenuItem
import com.pixamob.pixacompose.components.PixaPopover
import com.pixamob.pixacompose.components.PixaSeparator
import com.pixamob.pixacompose.components.PixaSheet
import com.pixamob.pixacompose.components.PixaToastState
import com.pixamob.pixacompose.components.PixaToastVariant
import com.pixamob.pixacompose.components.PixaTooltip
import com.pixamob.pixacompose.primitives.PixaIcon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaTheme

enum class OverlayDemo { Dialog, Sheet, FormSheet, Popover, Menu, Toast }

/** @param initiallyOpen lets a snapshot test start with one overlay already showing. */
@Composable
fun OverlaysSection(toasts: PixaToastState, initiallyOpen: OverlayDemo? = null) {
    var open by remember { mutableStateOf(initiallyOpen) }
    val close = { open = null }
    val gap = Arrangement.spacedBy(12.dp)

    LaunchedEffect(Unit) {
        if (initiallyOpen == OverlayDemo.Toast) {
            toasts.show("Changes saved", "Your profile was updated.", PixaToastVariant.Success)
            toasts.show("Reminder moved", "It is now due on Friday.")
            toasts.show("Upload failed", "Check your connection.", PixaToastVariant.Destructive)
        }
    }

    PixaText("Overlays", style = PixaTheme.typography.h3)
    Row(horizontalArrangement = gap) {
        PixaButton(onClick = { open = OverlayDemo.Dialog }, variant = PixaButtonVariant.Outline) { PixaText("Dialog") }
        PixaButton(onClick = { open = OverlayDemo.Sheet }, variant = PixaButtonVariant.Outline) { PixaText("Sheet") }
        PixaButton(
            onClick = { toasts.show("Changes saved", "Your profile was updated.", PixaToastVariant.Success) },
            variant = PixaButtonVariant.Outline,
        ) { PixaText("Toast") }
    }
    Row(horizontalArrangement = gap) {
        PixaButton(onClick = { open = OverlayDemo.FormSheet }, variant = PixaButtonVariant.Outline) { PixaText("Form sheet") }
    }
    Row(horizontalArrangement = gap) {
        Box {
            PixaButton(onClick = { open = OverlayDemo.Popover }, variant = PixaButtonVariant.Outline) { PixaText("Popover") }
            PixaPopover(visible = open == OverlayDemo.Popover, onDismissRequest = close) {
                PixaText("Dimensions", style = PixaTheme.typography.label)
                PixaText(
                    "Set the width of the layer.",
                    style = PixaTheme.typography.bodySmall,
                    color = PixaTheme.colors.mutedForeground,
                )
                var width by remember { mutableStateOf("100%") }
                PixaInput(width, { width = it })
            }
        }
        Box {
            PixaButton(onClick = { open = OverlayDemo.Menu }, variant = PixaButtonVariant.Outline) {
                PixaText("Menu")
                PixaIcon(PixaIcons.ChevronDown, contentDescription = null, size = 16.dp)
            }
            PixaMenu(visible = open == OverlayDemo.Menu, onDismissRequest = close) {
                PixaMenuItem(onClick = close) { PixaIcon(PixaIcons.Plus, null, size = 16.dp); PixaText("New file") }
                PixaMenuItem(onClick = close) { PixaIcon(PixaIcons.Check, null, size = 16.dp); PixaText("Mark as done") }
                PixaMenuItem(onClick = close, enabled = false) { PixaText("Archive") }
                PixaSeparator()
                PixaMenuItem(onClick = close) { PixaIcon(PixaIcons.Close, null, size = 16.dp); PixaText("Delete") }
            }
        }
        PixaTooltip(tooltip = { PixaText("Hover or long-press") }) {
            PixaButton(onClick = {}, variant = PixaButtonVariant.Outline) { PixaText("Tooltip") }
        }
    }

    PixaDialog(visible = open == OverlayDemo.Dialog, onDismissRequest = close) {
        PixaText("Delete project?", style = PixaTheme.typography.h3)
        PixaText(
            "This removes the project and all of its files. It cannot be undone.",
            style = PixaTheme.typography.bodySmall,
            color = PixaTheme.colors.mutedForeground,
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)) {
            PixaButton(onClick = close, variant = PixaButtonVariant.Outline) { PixaText("Cancel") }
            PixaButton(onClick = close, variant = PixaButtonVariant.Destructive) { PixaText("Delete") }
        }
    }

    PixaSheet(visible = open == OverlayDemo.FormSheet, onDismissRequest = close) {
        PixaText("Add weight", style = PixaTheme.typography.h3)
        var weight by remember { mutableStateOf("") }
        PixaField(label = "Weight in kg") {
            PixaInput(
                weight,
                { weight = it },
                placeholder = "4.6",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
        }
        var note by remember { mutableStateOf("") }
        PixaField(label = "Note") { PixaInput(note, { note = it }, placeholder = "After breakfast") }
        PixaButton(onClick = close, modifier = Modifier.fillMaxWidth()) { PixaText("Save") }
    }

    PixaSheet(visible = open == OverlayDemo.Sheet, onDismissRequest = close) {
        PixaText("Share", style = PixaTheme.typography.h3)
        PixaText(
            "Anyone with the link can view this file.",
            style = PixaTheme.typography.bodySmall,
            color = PixaTheme.colors.mutedForeground,
        )
        PixaButton(onClick = close, modifier = Modifier.fillMaxWidth()) { PixaText("Copy link") }
        PixaButton(onClick = close, modifier = Modifier.fillMaxWidth(), variant = PixaButtonVariant.Ghost) { PixaText("Cancel") }
    }
}
