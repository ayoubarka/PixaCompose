package com.pixamob.pixacompose.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.IconButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.actions.PixaIconButton
import com.pixamob.pixacompose.components.overlay.PixaDialog
import com.pixamob.pixacompose.components.surfaces.PixaSheet
import com.pixamob.pixacompose.components.surfaces.SheetExpandability
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.HSV
import com.pixamob.pixacompose.utils.fromHex
import com.pixamob.pixacompose.utils.hsv
import com.pixamob.pixacompose.utils.pixaRipple
import com.pixamob.pixacompose.utils.toHSV
import com.pixamob.pixacompose.utils.toHexString
import kotlin.math.roundToInt

/**
 * PixaColorPicker — reusable color-selection primitive.
 *
 * ### Anatomy
 * Saturation/value field → hue slider → optional alpha slider → optional dropper +
 * live preview + hex/RGB entry row → optional recent-colors row.
 *
 * ### Sizing
 * [SizeVariant] drives field height, slider size, entry-field size and swatch size.
 *
 * ### Presentation
 * This composable is presentation-agnostic — it renders inline content only. Use
 * [ColorPickerDialog] or [ColorPickerSheet] to launch it inside [PixaDialog]/[PixaSheet],
 * or embed [PixaColorPicker] directly in any other container.
 *
 * ### Recent colors
 * Held on [ColorPickerState] (seedable via [rememberColorPickerState], persisted across
 * recomposition/config changes). Nothing is added automatically — call
 * [ColorPickerState.commitToRecents] at the point selection is confirmed (see
 * [ColorPickerDialog]/[ColorPickerSheet] for the pattern) so a color only lands in the
 * row once the user actually picks it, not on every drag frame.
 *
 * ### Customization
 * [eyedropperIcon]/[onEyedropperClick] expose an optional dropper action; PixaCompose has
 * no platform screen-color-sampling API, so actual sampling is the caller's responsibility.
 *
 * @param state Color + recent-colors state, see [rememberColorPickerState]
 * @param modifier Modifier for the picker column
 * @param size Size tier for field/sliders/entry fields/swatches
 * @param enabled Whether the picker responds to input
 * @param showAlpha Show the alpha slider
 * @param showRgbFields Show R/G/B numeric entry alongside hex (default: true, matches spec)
 * @param showRecentColors Show the recent-colors row when [ColorPickerState.recentColors] is non-empty
 * @param showPreview Show the live color preview swatch
 * @param colors Custom color overrides
 * @param eyedropperIcon Optional dropper icon; shown only when [onEyedropperClick] is also set
 * @param onEyedropperClick Dropper action callback — caller owns the actual sampling
 * @param onColorChange Callback fired on every color change (drag-live, not debounced)
 * @param contentDescription Accessibility label for the picker as a whole
 */

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class ColorPickerColors(
    val fieldBorder: Color,
    val checkerLight: Color,
    val checkerDark: Color,
    val previewBorder: Color,
    val swatchBorder: Color,
    val swatchSelectedBorder: Color
)

/**
 * Color + recent-colors state for [PixaColorPicker]. Recent colors are internally
 * remembered (survive recomposition/config change via [rememberColorPickerState]'s Saver)
 * but caller-controlled: seed them via [initialRecentColors], commit new ones explicitly
 * via [commitToRecents] — nothing is added implicitly while dragging.
 */
@Stable
class ColorPickerState(
    initialColor: Color = Color.hsv(217f, 0.71f, 0.95f),
    initialRecentColors: List<Color> = emptyList(),
    val maxRecentColors: Int = 8
) {
    var color by mutableStateOf(initialColor)
        private set

    private val _recentColors = mutableStateListOf<Color>().apply {
        addAll(initialRecentColors.take(maxRecentColors))
    }
    val recentColors: List<Color> get() = _recentColors

    val hsv: HSV get() = color.toHSV()
    val hex: String get() = color.toHexString(includeAlpha = false)

    fun selectColor(newColor: Color) {
        color = newColor
    }

    fun setHue(hue: Float) {
        val current = hsv
        color = Color.hsv(hue, current.saturation, current.value, color.alpha)
    }

    fun setSaturationValue(saturation: Float, value: Float) {
        val current = hsv
        color = Color.hsv(current.hue, saturation, value, color.alpha)
    }

    fun setAlpha(alpha: Float) {
        color = color.copy(alpha = alpha.coerceIn(0f, 1f))
    }

    /** Adds [colorToCommit] (defaults to the current color) to the front of [recentColors], deduping and capping at [maxRecentColors]. */
    fun commitToRecents(colorToCommit: Color = color) {
        _recentColors.removeAll { it == colorToCommit }
        _recentColors.add(0, colorToCommit)
        while (_recentColors.size > maxRecentColors) {
            _recentColors.removeAt(_recentColors.lastIndex)
        }
    }

    companion object {
        fun Saver(maxRecentColors: Int): Saver<ColorPickerState, *> = Saver(
            save = { state ->
                listOf(state.color.value.toLong(), state.recentColors.map { it.value.toLong() })
            },
            restore = { saved ->
                @Suppress("UNCHECKED_CAST")
                val list = saved as List<Any>
                ColorPickerState(
                    initialColor = Color((list[0] as Long).toULong()),
                    initialRecentColors = (list[1] as List<*>).map { Color((it as Long).toULong()) },
                    maxRecentColors = maxRecentColors
                )
            }
        )
    }
}

@Composable
fun rememberColorPickerState(
    initialColor: Color = Color.hsv(217f, 0.71f, 0.95f),
    initialRecentColors: List<Color> = emptyList(),
    maxRecentColors: Int = 8
): ColorPickerState = rememberSaveable(saver = ColorPickerState.Saver(maxRecentColors)) {
    ColorPickerState(initialColor, initialRecentColors, maxRecentColors)
}

@Immutable
@Stable
private data class ColorPickerSizeConfig(
    /** Height of the 2D saturation/value field — no existing [HierarchicalSize] category spans
     * a primary selection surface this large (Container tops out at 80dp), so this is a
     * dedicated, component-local ladder rather than a forced fit. */
    val fieldHeight: Dp,
    val fieldThumbSize: Dp,
    val fieldThumbBorder: Dp,
    /** Mirrors PixaSlider's private per-size thumb ladder so the alpha-track checkerboard
     * lines up with the slider band it sits behind (Slider.kt's config isn't exposed). */
    val sliderBandHeight: Dp,
    val spacing: Dp,
    val swatchSize: Dp,
    val fieldRadius: Dp
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getColorPickerTheme(): ColorPickerColors {
    val colors = AppTheme.colors
    return ColorPickerColors(
        fieldBorder = colors.baseBorderSubtle,
        checkerLight = colors.baseSurfaceDefault,
        checkerDark = colors.baseSurfaceElevated,
        previewBorder = colors.baseBorderDefault,
        swatchBorder = colors.baseBorderDefault,
        swatchSelectedBorder = colors.brandBorderDefault
    )
}

private fun getColorPickerSizeConfig(size: SizeVariant): ColorPickerSizeConfig = when (size) {
    SizeVariant.None, SizeVariant.Nano, SizeVariant.Compact, SizeVariant.Small -> ColorPickerSizeConfig(
        fieldHeight = 160.dp,
        fieldThumbSize = HierarchicalSize.Icon.Small,
        fieldThumbBorder = HierarchicalSize.Border.Medium,
        sliderBandHeight = HierarchicalSize.Icon.Compact,
        spacing = HierarchicalSize.Spacing.Small,
        swatchSize = HierarchicalSize.Container.Compact,
        fieldRadius = HierarchicalSize.Radius.Large
    )
    SizeVariant.Medium -> ColorPickerSizeConfig(
        fieldHeight = 200.dp,
        fieldThumbSize = HierarchicalSize.Icon.Medium,
        fieldThumbBorder = HierarchicalSize.Border.Medium,
        sliderBandHeight = HierarchicalSize.Icon.Small,
        spacing = HierarchicalSize.Spacing.Medium,
        swatchSize = HierarchicalSize.Container.Small,
        fieldRadius = HierarchicalSize.Radius.Large
    )
    SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> ColorPickerSizeConfig(
        fieldHeight = 240.dp,
        fieldThumbSize = HierarchicalSize.Icon.Large,
        fieldThumbBorder = HierarchicalSize.Border.Large,
        sliderBandHeight = HierarchicalSize.Icon.Medium,
        spacing = HierarchicalSize.Spacing.Large,
        swatchSize = HierarchicalSize.Container.Medium,
        fieldRadius = HierarchicalSize.Radius.Huge
    )
}

/** Full-spectrum stops for the hue slider's [Brush.horizontalGradient]. Stateless — safe as a top-level constant. */
private val HueTrackBrush = Brush.horizontalGradient(
    listOf(0f, 60f, 120f, 180f, 240f, 300f, 360f).map { Color.hsv(it, 1f, 1f) }
)

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL <COLOR PICKER>
// ════════════════════════════════════════════════════════════════════════════

/** Tiled two-tone transparency backdrop, tokenized via theme surface colors rather than hardcoded gray/white. */
private fun Modifier.pixaCheckerboard(tileSize: Dp, light: Color, dark: Color): Modifier = drawBehind {
    val tilePx = tileSize.toPx()
    if (tilePx <= 0f) return@drawBehind
    val cols = (size.width / tilePx).toInt() + 1
    val rows = (size.height / tilePx).toInt() + 1
    for (row in 0 until rows) {
        for (col in 0 until cols) {
            drawRect(
                color = if ((row + col) % 2 == 0) light else dark,
                topLeft = Offset(col * tilePx, row * tilePx),
                size = Size(tilePx, tilePx)
            )
        }
    }
}

private fun handleFieldKeyEvent(
    event: KeyEvent,
    enabled: Boolean,
    saturation: Float,
    value: Float,
    onChange: (saturation: Float, value: Float) -> Unit
): Boolean {
    if (!enabled || event.type != KeyEventType.KeyDown) return false
    val step = 0.02f
    val (ds, dv) = when (event.key) {
        Key.DirectionRight -> step to 0f
        Key.DirectionLeft -> -step to 0f
        Key.DirectionUp -> 0f to step
        Key.DirectionDown -> 0f to -step
        else -> return false
    }
    onChange((saturation + ds).coerceIn(0f, 1f), (value + dv).coerceIn(0f, 1f))
    return true
}

/**
 * 2D saturation (x) / value (y) selection surface for a fixed [hue]. Layered from three
 * stacked backgrounds — hue fill, white→transparent horizontal, transparent→black vertical —
 * the same construction the reference spec and prior art use; [Color.White]/[Color.Black] here
 * are HSV-cube endpoints (S=0, V=0), not themed surfaces, so they're intentionally not tokens.
 */
@Composable
private fun SaturationValueField(
    hue: Float,
    saturation: Float,
    value: Float,
    onChange: (saturation: Float, value: Float) -> Unit,
    enabled: Boolean,
    sizeConfig: ColorPickerSizeConfig,
    themeColors: ColorPickerColors,
    modifier: Modifier = Modifier
) {
    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    val interactionSource = remember { MutableInteractionSource() }
    val density = LocalDensity.current
    val shape = RoundedCornerShape(sizeConfig.fieldRadius)

    fun updateFromOffset(offset: Offset) {
        if (boxSize.width == 0 || boxSize.height == 0) return
        val s = (offset.x / boxSize.width).coerceIn(0f, 1f)
        val v = (1f - offset.y / boxSize.height).coerceIn(0f, 1f)
        onChange(s, v)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.hsv(hue, 1f, 1f))
            .background(Brush.horizontalGradient(listOf(Color.White, Color.Transparent)))
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
            .border(HierarchicalSize.Border.Compact, themeColors.fieldBorder, shape)
            .onSizeChanged { boxSize = it }
            .then(
                if (enabled) {
                    Modifier
                        .pointerInput(Unit) { detectTapGestures { updateFromOffset(it) } }
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ -> change.consume(); updateFromOffset(change.position) }
                        }
                } else Modifier
            )
            .focusable(enabled = enabled, interactionSource = interactionSource)
            .onKeyEvent { handleFieldKeyEvent(it, enabled, saturation, value, onChange) }
            .semantics {
                contentDescription = "Saturation and brightness"
                stateDescription = "Saturation ${(saturation * 100).roundToInt()}%, brightness ${(value * 100).roundToInt()}%"
            }
    ) {
        if (boxSize.width > 0 && boxSize.height > 0) {
            val thumbPx = with(density) { sizeConfig.fieldThumbSize.toPx() }
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (saturation * boxSize.width - thumbPx / 2f).roundToInt(),
                            ((1f - value) * boxSize.height - thumbPx / 2f).roundToInt()
                        )
                    }
                    .size(sizeConfig.fieldThumbSize)
                    .clip(CircleShape)
                    .background(Color.hsv(hue, saturation, value))
                    .border(sizeConfig.fieldThumbBorder, Color.White, CircleShape)
                    .border(HierarchicalSize.Border.Compact, Color.Black.copy(alpha = 0.35f), CircleShape)
            )
        }
    }
}

@Composable
private fun PreviewSwatch(color: Color, sizeConfig: ColorPickerSizeConfig, themeColors: ColorPickerColors) {
    val shape = RoundedCornerShape(HierarchicalSize.Radius.Medium)
    Box(
        modifier = Modifier
            .size(sizeConfig.swatchSize)
            .clip(shape)
            .pixaCheckerboard(HierarchicalSize.Spacing.Compact, themeColors.checkerLight, themeColors.checkerDark)
            .background(color)
            .border(HierarchicalSize.Border.Compact, themeColors.previewBorder, shape)
            .semantics { contentDescription = "Current color preview" }
    )
}

@Composable
private fun RgbField(
    label: String,
    channelValue: Int,
    enabled: Boolean,
    size: SizeVariant,
    modifier: Modifier = Modifier,
    onChange: (Int) -> Unit
) {
    var text by remember(channelValue) { mutableStateOf(channelValue.toString()) }
    PixaTextField(
        value = text,
        onValueChange = { newValue ->
            val digitsOnly = newValue.filter { it.isDigit() }.take(3)
            text = digitsOnly
            digitsOnly.toIntOrNull()?.let { if (it in 0..255) onChange(it) }
        },
        label = label,
        size = size,
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        modifier = modifier
    )
}

@Composable
private fun ManualEntryRow(
    color: Color,
    onColorChange: (Color) -> Unit,
    showRgbFields: Boolean,
    enabled: Boolean,
    size: SizeVariant,
    modifier: Modifier = Modifier
) {
    var hexText by remember(color) { mutableStateOf(color.toHexString(includeAlpha = false).removePrefix("#")) }
    var isHexError by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
    ) {
        PixaTextField(
            value = hexText,
            onValueChange = { newValue ->
                hexText = newValue.uppercase()
                val parsed = Color.fromHex("#$hexText")
                isHexError = parsed == null && hexText.isNotEmpty()
                if (parsed != null) onColorChange(parsed.copy(alpha = color.alpha))
            },
            label = "HEX",
            isError = isHexError,
            size = size,
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Done),
            modifier = Modifier.weight(2f)
        )
        if (showRgbFields) {
            RgbField("R", (color.red * 255).roundToInt(), enabled, size, Modifier.weight(1f)) {
                onColorChange(color.copy(red = it / 255f))
            }
            RgbField("G", (color.green * 255).roundToInt(), enabled, size, Modifier.weight(1f)) {
                onColorChange(color.copy(green = it / 255f))
            }
            RgbField("B", (color.blue * 255).roundToInt(), enabled, size, Modifier.weight(1f)) {
                onColorChange(color.copy(blue = it / 255f))
            }
        }
    }
}

@Composable
private fun RecentColorsRow(
    colors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    enabled: Boolean,
    sizeConfig: ColorPickerSizeConfig,
    themeColors: ColorPickerColors,
    modifier: Modifier = Modifier
) {
    if (colors.isEmpty()) return
    val shape = RoundedCornerShape(HierarchicalSize.Radius.Small)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(sizeConfig.spacing)
    ) {
        colors.forEach { swatchColor ->
            val isSelected = swatchColor == selectedColor
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(sizeConfig.swatchSize)
                    .clip(shape)
                    .pixaCheckerboard(HierarchicalSize.Spacing.Compact, themeColors.checkerLight, themeColors.checkerDark)
                    .background(swatchColor)
                    .border(
                        width = if (isSelected) HierarchicalSize.Border.Large else HierarchicalSize.Border.Nano,
                        color = if (isSelected) themeColors.swatchSelectedBorder else themeColors.swatchBorder,
                        shape = shape
                    )
                    .clickable(
                        enabled = enabled,
                        interactionSource = interactionSource,
                        indication = pixaRipple()
                    ) { onColorSelected(swatchColor) }
                    .semantics {
                        role = Role.Button
                        contentDescription = "Recent color ${swatchColor.toHexString(false)}"
                        selected = isSelected
                    }
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

@Composable
fun PixaColorPicker(
    state: ColorPickerState = rememberColorPickerState(),
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    showAlpha: Boolean = true,
    showRgbFields: Boolean = true,
    showRecentColors: Boolean = true,
    showPreview: Boolean = true,
    colors: ColorPickerColors? = null,
    eyedropperIcon: Painter? = null,
    onEyedropperClick: (() -> Unit)? = null,
    onColorChange: ((Color) -> Unit)? = null,
    contentDescription: String = "Color picker"
) {
    val themeColors = colors ?: getColorPickerTheme()
    val sizeConfig = getColorPickerSizeConfig(size)
    val hsv = state.hsv

    LaunchedEffect(state.color) { onColorChange?.invoke(state.color) }

    Column(
        modifier = modifier.semantics { this.contentDescription = contentDescription },
        verticalArrangement = Arrangement.spacedBy(sizeConfig.spacing)
    ) {
        SaturationValueField(
            hue = hsv.hue,
            saturation = hsv.saturation,
            value = hsv.value,
            onChange = state::setSaturationValue,
            enabled = enabled,
            sizeConfig = sizeConfig,
            themeColors = themeColors,
            modifier = Modifier.fillMaxWidth().height(sizeConfig.fieldHeight)
        )

        PixaSlider(
            value = hsv.hue,
            onValueChange = state::setHue,
            valueRange = 0f..360f,
            enabled = enabled,
            size = size,
            variant = SliderVariant.Filled,
            gradientBrush = HueTrackBrush,
            thumbColorOverride = Color.hsv(hsv.hue, 1f, 1f)
        )

        if (showAlpha) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sizeConfig.sliderBandHeight)
                    .clip(RoundedCornerShape(HierarchicalSize.Radius.Full))
                    .pixaCheckerboard(HierarchicalSize.Spacing.Compact, themeColors.checkerLight, themeColors.checkerDark)
            ) {
                PixaSlider(
                    value = state.color.alpha,
                    onValueChange = state::setAlpha,
                    valueRange = 0f..1f,
                    enabled = enabled,
                    size = size,
                    variant = SliderVariant.Filled,
                    gradientBrush = Brush.horizontalGradient(listOf(state.color.copy(alpha = 0f), state.color.copy(alpha = 1f))),
                    thumbColorOverride = state.color
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(sizeConfig.spacing)
        ) {
            if (eyedropperIcon != null && onEyedropperClick != null) {
                PixaIconButton(
                    icon = eyedropperIcon,
                    onClick = onEyedropperClick,
                    variant = IconButtonVariant.Filled,
                    size = size,
                    enabled = enabled,
                    contentDescription = "Pick color from screen"
                )
            }
            if (showPreview) {
                PreviewSwatch(color = state.color, sizeConfig = sizeConfig, themeColors = themeColors)
            }
            ManualEntryRow(
                color = state.color,
                onColorChange = state::selectColor,
                showRgbFields = showRgbFields,
                enabled = enabled,
                size = size,
                modifier = Modifier.weight(1f)
            )
        }

        if (showRecentColors && state.recentColors.isNotEmpty()) {
            RecentColorsRow(
                colors = state.recentColors,
                selectedColor = state.color,
                onColorSelected = state::selectColor,
                enabled = enabled,
                sizeConfig = sizeConfig,
                themeColors = themeColors,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/** Thin [PixaDialog] wrapper around [PixaColorPicker]. Commits the picked color to recents on confirm. */
@Composable
fun ColorPickerDialog(
    onDismissRequest: () -> Unit,
    onColorConfirmed: (Color) -> Unit,
    state: ColorPickerState = rememberColorPickerState(),
    title: String = "Choose color",
    confirmText: String = "Select",
    dismissText: String = "Cancel",
    size: SizeVariant = SizeVariant.Medium,
    showAlpha: Boolean = true,
    showRgbFields: Boolean = true,
    showRecentColors: Boolean = true,
    eyedropperIcon: Painter? = null,
    onEyedropperClick: (() -> Unit)? = null
) {
    PixaDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        confirmText = confirmText,
        dismissText = dismissText,
        onConfirm = { state.commitToRecents(); onColorConfirmed(state.color) },
        onDismiss = onDismissRequest,
        size = size,
        content = {
            PixaColorPicker(
                state = state,
                size = size,
                showAlpha = showAlpha,
                showRgbFields = showRgbFields,
                showRecentColors = showRecentColors,
                eyedropperIcon = eyedropperIcon,
                onEyedropperClick = onEyedropperClick
            )
        }
    )
}

/** Thin [PixaSheet] wrapper around [PixaColorPicker]. Fixed height (no snap points) — the picker's own content already scrolls the available space. */
@Composable
fun ColorPickerSheet(
    onDismissRequest: () -> Unit,
    state: ColorPickerState = rememberColorPickerState(),
    title: String = "Choose color",
    doneText: String = "Done",
    size: SizeVariant = SizeVariant.Medium,
    showAlpha: Boolean = true,
    showRgbFields: Boolean = true,
    showRecentColors: Boolean = true,
    eyedropperIcon: Painter? = null,
    onEyedropperClick: (() -> Unit)? = null,
    onColorConfirmed: ((Color) -> Unit)? = null
) {
    PixaSheet(
        onDismissRequest = onDismissRequest,
        title = title,
        expandability = SheetExpandability.Fixed
    ) {
        PixaColorPicker(
            state = state,
            size = size,
            showAlpha = showAlpha,
            showRgbFields = showRgbFields,
            showRecentColors = showRecentColors,
            eyedropperIcon = eyedropperIcon,
            onEyedropperClick = onEyedropperClick,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))
        PixaButton(
            text = doneText,
            onClick = { state.commitToRecents(); onColorConfirmed?.invoke(state.color); onDismissRequest() },
            variant = ButtonVariant.Filled,
            size = size,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
