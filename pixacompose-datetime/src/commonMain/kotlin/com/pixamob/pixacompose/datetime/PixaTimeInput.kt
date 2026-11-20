package com.pixamob.pixacompose.datetime

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.PixaBorder
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaShadow
import com.pixamob.pixacompose.theme.PixaTheme
import kotlinx.datetime.LocalTime

/** Words the time input shows or reads out. Pass translated ones for other languages. */
@Immutable
public class PixaTimeLabels(
    public val hours: String,
    public val minutes: String,
    public val am: String,
    public val pm: String,
) {
    public companion object {
        public val English: PixaTimeLabels = PixaTimeLabels("Hours", "Minutes", "AM", "PM")
    }
}

/**
 * A field for a time of day, made of an hour and a minute segment and, in 12-hour mode, an
 * AM/PM segment. Typing fills a segment and moves on to the next; a digit that cannot start a
 * two-digit value ("7" in the hour) completes it at once. Up and down arrow keys step a segment,
 * backspace in an empty one goes back, and a tap on AM/PM switches it.
 *
 * [onValueChange] gets null while a segment is empty, and a time once both have a value.
 */
@Composable
public fun PixaTimeInput(
    value: LocalTime?,
    onValueChange: (LocalTime?) -> Unit,
    modifier: Modifier = Modifier,
    use24Hour: Boolean = true,
    enabled: Boolean = true,
    isError: Boolean = false,
    labels: PixaTimeLabels = PixaTimeLabels.English,
) {
    val colors = PixaTheme.colors
    var hour by remember { mutableStateOf(value?.let { hourText(it.hour, use24Hour) }.orEmpty()) }
    var minute by remember { mutableStateOf(value?.minute?.pad().orEmpty()) }
    var pm by remember { mutableStateOf((value?.hour ?: 0) >= 12) }

    fun current(): LocalTime? {
        val h = hour.toIntOrNull() ?: return null
        val m = minute.toIntOrNull() ?: return null
        val h24 = if (use24Hour) h else (h % 12) + if (pm) 12 else 0
        return LocalTime(h24.coerceIn(0, 23), m.coerceIn(0, 59))
    }
    fun emit() = onValueChange(current())

    // Follow a value set from outside without disturbing a segment that is being typed in.
    LaunchedEffect(value, use24Hour) {
        if (value != current()) {
            hour = value?.let { hourText(it.hour, use24Hour) }.orEmpty()
            minute = value?.minute?.pad().orEmpty()
            if (value != null) pm = value.hour >= 12
        }
    }

    val hourFocus = remember { FocusRequester() }
    val minuteFocus = remember { FocusRequester() }
    val hourSource = remember { MutableInteractionSource() }
    val minuteSource = remember { MutableInteractionSource() }
    val hourFocused by hourSource.collectIsFocusedAsState()
    val minuteFocused by minuteSource.collectIsFocusedAsState()
    val hourRange = if (use24Hour) 0..23 else 1..12

    PixaSurface(
        modifier = modifier
            .defaultMinSize(minHeight = 40.dp)
            .graphicsLayer { alpha = if (enabled) 1f else 0.5f },
        color = colors.background,
        contentColor = colors.foreground,
        shape = PixaTheme.shapes.md,
        border = PixaTheme.decoration.borderWidth.takeIf { it > 0.dp }
            ?.let { PixaBorder(it, if (isError) colors.destructive else colors.input) },
        shadow = if (enabled) PixaTheme.decoration.controlShadow else PixaShadow.None,
        focused = hourFocused || minuteFocused,
        contentAlignment = Alignment.CenterStart,
    ) {
        // A time reads hours first in every language, so the segments never swap sides.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier.padding(horizontal = PixaTheme.spacing.sm, vertical = PixaTheme.spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(PixaTheme.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Segment(
                    text = hour,
                    placeholder = "--",
                    range = hourRange,
                    description = labels.hours,
                    enabled = enabled,
                    source = hourSource,
                    modifier = Modifier.focusRequester(hourFocus),
                    onTextChange = { hour = it; emit() },
                    onFilled = { minuteFocus.requestFocus() },
                    onBackOut = {},
                    onLeave = { hour = complete(hour, hourRange); emit() },
                )
                PixaText(":", style = PixaTheme.typography.body, color = colors.mutedForeground)
                Segment(
                    text = minute,
                    placeholder = "--",
                    range = 0..59,
                    description = labels.minutes,
                    enabled = enabled,
                    source = minuteSource,
                    modifier = Modifier.focusRequester(minuteFocus),
                    onTextChange = { minute = it; emit() },
                    onFilled = {},
                    onBackOut = { hourFocus.requestFocus() },
                    onLeave = { minute = complete(minute, 0..59); emit() },
                )
                if (!use24Hour) {
                    PixaSurface(
                        onClick = { pm = !pm; emit() },
                        enabled = enabled,
                        role = Role.Switch,
                        color = Color.Transparent,
                        contentColor = colors.foreground,
                        shape = PixaTheme.shapes.sm,
                    ) {
                        PixaText(
                            if (pm) labels.pm else labels.am,
                            Modifier.padding(horizontal = PixaTheme.spacing.xs, vertical = 2.dp),
                            style = PixaTheme.typography.body,
                        )
                    }
                }
            }
        }
    }
}

/** One two-digit segment. The caret stays at the end; the highlight shows which one is active. */
@Composable
private fun Segment(
    text: String,
    placeholder: String,
    range: IntRange,
    description: String,
    enabled: Boolean,
    source: MutableInteractionSource,
    modifier: Modifier,
    onTextChange: (String) -> Unit,
    onFilled: () -> Unit,
    onBackOut: () -> Unit,
    onLeave: () -> Unit,
) {
    val colors = PixaTheme.colors
    val focused by source.collectIsFocusedAsState()
    fun step(by: Int) {
        val size = range.last - range.first + 1
        val next = ((text.toIntOrNull() ?: if (by > 0) range.last else range.first) - range.first + by + size) % size + range.first
        onTextChange(next.pad())
    }
    BasicTextField(
        value = TextFieldValue(text, TextRange(text.length)),
        onValueChange = { new ->
            val typed = enter(text, new.text, range)
            if (typed != null) {
                onTextChange(typed.first)
                if (typed.second) onFilled()
            }
        },
        modifier = modifier
            .semantics { contentDescription = description }
            // The caller reads its live state here: focus can leave in the middle of an edit,
            // before this segment recomposes with the new text.
            .onFocusChanged { state -> if (!state.isFocused) onLeave() }
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (event.key) {
                    Key.DirectionUp -> { step(1); true }
                    Key.DirectionDown -> { step(-1); true }
                    Key.Backspace -> if (text.isEmpty()) { onBackOut(); true } else false
                    else -> false
                }
            },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Next),
        interactionSource = source,
        // The segment draws its own text, so the inner field is deliberately not drawn.
        decorationBox = {
            PixaSurface(
                modifier = Modifier.widthIn(min = 28.dp),
                color = if (focused) colors.accent else Color.Transparent,
                contentColor = if (focused) colors.accentForeground else colors.foreground,
                shape = PixaTheme.shapes.sm,
                contentAlignment = Alignment.Center,
            ) {
                PixaText(
                    text.ifEmpty { placeholder },
                    Modifier.padding(horizontal = PixaTheme.spacing.xs, vertical = 2.dp),
                    style = PixaTheme.typography.body,
                    color = if (text.isEmpty() && !focused) colors.mutedForeground else Color.Unspecified,
                )
            }
        },
    )
}

/**
 * Applies typing to a segment. Returns the new text and whether it is complete, or null to
 * ignore the edit. A third digit starts the segment over, so retyping needs no deleting.
 */
private fun enter(old: String, typed: String, range: IntRange): Pair<String, Boolean>? {
    val digits = typed.filter(Char::isDigit)
    if (digits.length != typed.length) return null
    if (digits.length <= old.length) return digits to false
    var text = if (old.length >= 2) digits.takeLast(1) else digits.takeLast(2)
    if (text.length == 1 && text.toInt() * 10 > range.last) return text.toInt().coerceIn(range).pad() to true
    if (text.length == 2) {
        text = text.toInt().coerceIn(range).pad()
        return text to true
    }
    return text to false
}

/** Leaving a half-typed segment completes it: "7" becomes "07". */
private fun complete(text: String, range: IntRange): String =
    if (text.length == 1) text.toInt().coerceIn(range).pad() else text

private fun hourText(hour24: Int, use24Hour: Boolean): String =
    (if (use24Hour) hour24 else (hour24 % 12).let { if (it == 0) 12 else it }).pad()

private fun Int.pad(): String = toString().padStart(2, '0')
