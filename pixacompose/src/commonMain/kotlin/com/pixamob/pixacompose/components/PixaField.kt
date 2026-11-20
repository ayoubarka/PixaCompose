package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaTheme

/** True inside a [PixaField] that has an error; inputs read it as their default error state. */
internal val LocalFieldInvalid = compositionLocalOf { false }

/**
 * The label of the [PixaField] around a control, or null outside one. Controls set it as their
 * content description, so a screen reader names a filled field ("Name, Biscuit") and an empty
 * one. An app's own control inside a field can read it the same way.
 */
public val LocalPixaFieldLabel: ProvidableCompositionLocal<String?> = compositionLocalOf { null }

/** Names the control after the surrounding [PixaField]'s label, if there is one. */
@Composable
internal fun Modifier.fieldLabel(): Modifier {
    val label = LocalPixaFieldLabel.current ?: return this
    return semantics { contentDescription = label }
}

/**
 * A form row: [label] above the control in [content], and under it [error] if there is one,
 * otherwise [helper]. Inputs inside take the error look on their own.
 *
 * ```
 * PixaField("Email", error = emailError) { PixaInput(email, { email = it }) }
 * ```
 */
@Composable
public fun PixaField(
    label: String,
    modifier: Modifier = Modifier,
    helper: String? = null,
    error: String? = null,
    content: @Composable () -> Unit,
) {
    val colors = PixaTheme.colors
    Column(modifier, verticalArrangement = Arrangement.spacedBy(PixaTheme.spacing.xs + PixaTheme.spacing.xs / 2)) {
        PixaText(label, style = PixaTheme.typography.label, color = colors.foreground)
        CompositionLocalProvider(LocalFieldInvalid provides (error != null), LocalPixaFieldLabel provides label, content = content)
        when {
            error != null -> PixaText(error, style = PixaTheme.typography.caption, color = colors.destructive)
            helper != null -> PixaText(helper, style = PixaTheme.typography.caption, color = colors.mutedForeground)
        }
    }
}
