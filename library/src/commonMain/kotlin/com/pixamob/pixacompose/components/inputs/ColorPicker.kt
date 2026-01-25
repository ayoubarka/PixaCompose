package com.pixamob.pixacompose.components.inputs
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.pixamob.pixacompose.theme.AppTheme






import com.pixamob.pixacompose.utils.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlin.math.*

// =========================================================================
// Configuration
// =========================================================================

/**
 * ColorPicker Mode - Different methods for selecting colors
 */
enum class ColorPickerMode {
    /** Grid of predefined colors */
    Grid,
    /** HSV color wheel with saturation/value selector */
    Wheel,
    /** RGB sliders */
    RGB,
    /** HSV sliders */
    HSV,
    /** HSL sliders */
    HSL,
}


// ============================================================================
// State Management
// ============================================================================

/**
 * ColorPicker State
 *
 * Manages the current color selection, mode, and history.
 * Use [rememberColorPickerState] to create and remember this state.
 *
 * @param initialColor The initial color to display
 * @param maxHistorySize Maximum number of colors to keep in history
 */
@Stable
class ColorPickerState(
    initialColor: Color = Color.White,
    private val maxHistorySize: Int = 12
) {
    private val _currentColor = mutableStateOf(initialColor)
    private val _previousColor = mutableStateOf(initialColor)
    private val _colorHistory = mutableStateListOf<Color>()
    private val _mode = mutableStateOf(ColorPickerMode.Grid)

    /** Current selected color */
    var currentColor: Color
        get() = _currentColor.value
        internal set(value) {
            if (_currentColor.value != value) {
                _previousColor.value = _currentColor.value
                _currentColor.value = value
            }
        }

    /** Previous color before current selection */
    val previousColor: Color get() = _previousColor.value

    /** List of recently selected colors */
    val colorHistory: List<Color> get() = _colorHistory

    /** Current picker mode */
    var mode: ColorPickerMode
        get() = _mode.value
        set(value) { _mode.value = value }

    /** HSV representation of current color */
    val hsv: HSV get() = currentColor.toHSV()

    /** HSL representation of current color */
    val hsl: HSL get() = currentColor.toHSL()

    /** Hex string of current color */
    val hexString: String get() = currentColor.toHexString(true)

    /**
     * Updates the current color
     */
    fun updateColor(color: Color) {
        currentColor = color
    }

    /**
     * Adds current color to history
     */
    fun commitToHistory() {
        if (_colorHistory.isEmpty() || _colorHistory.first() != currentColor) {
            _colorHistory.add(0, currentColor)
            if (_colorHistory.size > maxHistorySize) {
                _colorHistory.removeAt(_colorHistory.lastIndex)
            }
        }
    }

    /**
     * Sets color from hex string
     */
    fun setFromHex(hex: String) {
        Color.fromHex(hex)?.let { updateColor(it) }
    }

    companion object {
        /**
         * Saver for ColorPickerState to survive configuration changes
         */
        fun Saver(): Saver<ColorPickerState, *> = Saver(
            save = { state ->
                listOf(
                    state.currentColor.value.toLong(),
                    state.previousColor.value.toLong(),
                    state.mode.ordinal,
                    state.colorHistory.map { it.value.toLong() }
                )
            },
            restore = { saved ->
                @Suppress("UNCHECKED_CAST", "USELESS_CAST")
                val list = saved as List<Any>
                val state = ColorPickerState(
                    initialColor = Color((list[0] as Long).toULong())
                )
                state._previousColor.value = Color((list[1] as Long).toULong())
                state._mode.value = ColorPickerMode.entries[list[2] as Int]
                (list[3] as List<*>).forEach { colorValue ->
                    state._colorHistory.add(Color(colorValue as Long))
                }
                state
            }
        )
    }
}

/**
 * Remember ColorPickerState across recompositions
 */
@Composable
fun rememberColorPickerState(
    initialColor: Color = Color.White,
    maxHistorySize: Int = 12
): ColorPickerState {
    return rememberSaveable(saver = ColorPickerState.Saver()) {
        ColorPickerState(initialColor, maxHistorySize)
    }
}

// ============================================================================
// Main ColorPicker Component
// ============================================================================

/**
 * ColorPicker Component
 *
 * A comprehensive color picker with multiple selection modes and advanced features
 * like alpha channel, brightness control, and color history.
 *
 * This is a pure content component - wrap it in your own Dialog/BottomSheet/Popup as needed.
 *
 * ## Features
 * - Multiple picker modes: Grid, Wheel, RGB/HSV/HSL sliders
 * - Alpha channel support with transparency preview
 * - Brightness control for Wheel mode
 * - Color comparison (current vs previous)
 * - Recent colors history
 * - Hex code input/display
 * - Smooth animations and 60fps performance
 * - Full accessibility support
 *
 * ## Usage Examples
 *
 * ### Basic Inline Picker
 * ```kotlin
 * val state = rememberColorPickerState(initialColor = Color.Blue)
 * ColorPicker(
 *     state = state,
 *     mode = ColorPickerMode.Grid,
 *     onColorChanged = { color -> println("Selected: $color") }
 * )
 * ```
 *
 * ### In a Dialog
 * ```kotlin
 * var showPicker by remember { mutableStateOf(false) }
 * val state = rememberColorPickerState()
 *
 * if (showPicker) {
 *     AlertDialog(
 *         onDismissRequest = { showPicker = false },
 *         title = { Text("Pick a Color") },
 *         text = {
 *             ColorPicker(
 *                 state = state,
 *                 mode = ColorPickerMode.Wheel,
 *                 onColorChanged = { /* handle color */ }
 *             )
 *         },
 *         confirmButton = {
 *             Button(onClick = {
 *                 state.commitToHistory()
 *                 showPicker = false
 *             }) { Text("Select") }
 *         }
 *     )
 * }
 * ```
 *
 * ### In a Bottom Sheet
 * ```kotlin
 * BottomSheet(onDismiss = { /* close */ }) {
 *     ColorPicker(
 *         state = state,
 *         mode = ColorPickerMode.RGB,
 *         showAlpha = true
 *     )
 *     Button(onClick = {
 *         state.commitToHistory()
 *         // close sheet
 *     }) { Text("Select") }
 * }
 * ```
 *
 * @param modifier Modifier for the color picker container
 * @param state State holder for color picker
 * @param mode Picker mode (Grid, Wheel, RGB, HSV, HSL)
 * @param showAlpha Whether to show alpha slider
 * @param showBrightness Whether to show brightness slider (Wheel mode only)
 * @param showHistory Whether to show recent colors
 * @param showHexInput Whether to show hex input field
 * @param showModeSelector Whether to show mode selector tabs
 * @param customPalette Custom color palette for Grid mode (defaults to Material 3 colors)
 * @param enabled Whether the picker is enabled or disabled
 * @param contentDescription Content description for accessibility
 * @param onColorChanged Callback when color changes (debounced for performance)
 */
@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun PixaColorPicker(
    modifier: Modifier = Modifier,
    state: ColorPickerState = rememberColorPickerState(),
    mode: ColorPickerMode = ColorPickerMode.Grid,
    showAlpha: Boolean = true,
    showBrightness: Boolean = true,
    showHistory: Boolean = true,
    showHexInput: Boolean = true,
    showModeSelector: Boolean = true,
    customPalette: List<Color>? = null,
    enabled: Boolean = true,
    contentDescription: String = "Color picker",
    onColorChanged: (Color) -> Unit = {}
) {
    // Update mode in state
    LaunchedEffect(mode) {
        state.mode = mode
    }

    // Debounced color change callback
    LaunchedEffect(state.currentColor) {
        snapshotFlow { state.currentColor }
            .debounce(50) // 50ms debounce for smooth performance
            .collect { color ->
                onColorChanged(color)
            }
    }

    Column (
        modifier = modifier
            .semantics {
                this.contentDescription = contentDescription
            }
            .then(if (!enabled) Modifier.graphicsLayer(alpha = 0.5f) else Modifier),
        verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)
    ) {
        // Color preview
        ColorPreview(
            currentColor = state.currentColor,
            previousColor = state.previousColor,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        // Mode selector
        if (showModeSelector) {
            ModeSelector(
                currentMode = state.mode,
                onModeChange = { if (enabled) state.mode = it },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Main picker area
        AnimatedContent(
            targetState = state.mode,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            }
        ) { pickerMode ->
            when (pickerMode) {
                ColorPickerMode.Grid -> GridColorPicker(
                    selectedColor = state.currentColor,
                    onColorSelected = { if (enabled) state.updateColor(it) },
                    palette = customPalette ?: MaterialColors.material3Colors,
                    enabled = enabled,
                    modifier = Modifier.fillMaxWidth()
                )

                ColorPickerMode.Wheel -> WheelColorPicker(
                    state = state,
                    enabled = enabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                ColorPickerMode.RGB -> RGBSliders(
                    color = state.currentColor,
                    onColorChange = { if (enabled) state.updateColor(it) },
                    showAlpha = showAlpha,
                    enabled = enabled,
                    modifier = Modifier.fillMaxWidth()
                )

                ColorPickerMode.HSV -> HSVSliders(
                    color = state.currentColor,
                    onColorChange = { if (enabled) state.updateColor(it) },
                    showAlpha = showAlpha,
                    enabled = enabled,
                    modifier = Modifier.fillMaxWidth()
                )

                ColorPickerMode.HSL -> HSLSliders(
                    color = state.currentColor,
                    onColorChange = { if (enabled) state.updateColor(it) },
                    showAlpha = showAlpha,
                    enabled = enabled,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Brightness slider
        if (showBrightness && state.mode == ColorPickerMode.Wheel) {
            BrightnessSlider(
                color = state.currentColor,
                onBrightnessChange = { brightness ->
                    if (enabled) {
                        val hsv = state.hsv
                        state.updateColor(Color.hsv(hsv.hue, hsv.saturation, brightness, state.currentColor.alpha))
                    }
                },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Alpha slider (not shown for RGB/HSV/HSL modes since they include it)
        if (showAlpha && state.mode != ColorPickerMode.RGB && state.mode != ColorPickerMode.HSV && state.mode != ColorPickerMode.HSL) {
            AlphaSlider(
                color = state.currentColor,
                onAlphaChange = { alpha ->
                    if (enabled) state.updateColor(state.currentColor.copy(alpha = alpha))
                },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Hex input
        if (showHexInput) {
            HexInput(
                hexValue = state.hexString,
                onHexChange = { if (enabled) state.setFromHex(it) },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Color history
        if (showHistory && state.colorHistory.isNotEmpty()) {
            ColorHistory(
                colors = state.colorHistory,
                onColorSelected = { if (enabled) state.updateColor(it) },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ============================================================================
// Color Preview
// ============================================================================

@Composable
private fun ColorPreview(
    currentColor: Color,
    previousColor: Color,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(HierarchicalSize.Container.Huge),
        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)
    ) {
        // Previous color
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(HierarchicalSize.Radius.Medium))
                .drawCheckerboard()
                .background(previousColor)
                .border(HierarchicalSize.Border.Nano, AppTheme.colors.baseContentDisabled, RoundedCornerShape(HierarchicalSize.Radius.Medium)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Previous",
                style = AppTheme.typography.captionRegular,
                color = if (previousColor.luminance() > 0.5f) Color.Black else Color.White
            )
        }

        // Current color
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(HierarchicalSize.Radius.Medium))
                .drawCheckerboard()
                .background(currentColor)
                .border(HierarchicalSize.Border.Nano, AppTheme.colors.baseContentDisabled, RoundedCornerShape(HierarchicalSize.Radius.Medium)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Current",
                style = AppTheme.typography.captionRegular,
                color = if (currentColor.luminance() > 0.5f) Color.Black else Color.White
            )
        }
    }
}

// ============================================================================
// Mode Selector
// ============================================================================

@Composable
private fun ModeSelector(
    currentMode: ColorPickerMode,
    onModeChange: (ColorPickerMode) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)
    ) {
        ColorPickerMode.entries.forEach { mode ->
            val isSelected = currentMode == mode
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(HierarchicalSize.Container.Compact)
                    .clickable(enabled = enabled) { onModeChange(mode) },
                shape = RoundedCornerShape(HierarchicalSize.Radius.Small),
                color = if (isSelected) AppTheme.colors.brandSurfaceDefault else Color.Transparent,
                border = BorderStroke(
                    HierarchicalSize.Border.Nano,
                    if (isSelected) AppTheme.colors.brandBorderDefault else AppTheme.colors.baseBorderDefault
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = mode.name,
                        style = AppTheme.typography.captionBold,
                        color = if (isSelected) AppTheme.colors.brandContentDefault else AppTheme.colors.baseContentBody
                    )
                }
            }
        }
    }
}

// ============================================================================
// Grid Color Picker
// ============================================================================

@Composable
private fun GridColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    palette: List<Color>,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = HierarchicalSize.Container.Medium),
        modifier = modifier.heightIn(max = HierarchicalSize.Container.Massive * 3.75f),
        contentPadding = PaddingValues(HierarchicalSize.Spacing.Compact),
        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact),
        verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact),
        userScrollEnabled = enabled
    ) {
        items(palette) { color ->
            val isSelected = color == selectedColor
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(HierarchicalSize.Radius.Medium))
                    .clickable(
                        enabled = enabled,
                        onClick = { onColorSelected(color) },
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple()
                    )
                    .drawCheckerboard()
                    .background(color)
                    .then(
                        if (isSelected) {
                            Modifier.border(HierarchicalSize.Border.Large, AppTheme.colors.brandBorderDefault, RoundedCornerShape(HierarchicalSize.Radius.Medium))
                        } else {
                            Modifier.border(HierarchicalSize.Border.Nano, AppTheme.colors.baseBorderDefault, RoundedCornerShape(HierarchicalSize.Radius.Medium))
                        }
                    )
                    .semantics {
                        contentDescription = "Color ${color.toHexString(false)}"
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Canvas(modifier = Modifier.size(HierarchicalSize.Icon.Small)) {
                        drawCircle(
                            color = if (color.luminance() > 0.5f) Color.Black else Color.White,
                            radius = size.minDimension / 2,
                            style = Stroke(width = HierarchicalSize.Border.Medium.toPx())
                        )
                        drawLine(
                            color = if (color.luminance() > 0.5f) Color.Black else Color.White,
                            start = Offset(size.width * 0.3f, size.height * 0.5f),
                            end = Offset(size.width * 0.45f, size.height * 0.65f),
                            strokeWidth = HierarchicalSize.Border.Medium.toPx()
                        )
                        drawLine(
                            color = if (color.luminance() > 0.5f) Color.Black else Color.White,
                            start = Offset(size.width * 0.45f, size.height * 0.65f),
                            end = Offset(size.width * 0.7f, size.height * 0.35f),
                            strokeWidth = HierarchicalSize.Border.Medium.toPx()
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// Wheel Color Picker
// ============================================================================

@Composable
private fun WheelColorPicker(
    state: ColorPickerState,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val hsv = state.hsv

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (enabled) {
                        Modifier
                            .pointerInput(Unit) {
                                detectDragGestures { change, _ ->
                                    val center = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
                                    val radius = min(canvasSize.width, canvasSize.height) / 2f
                                    val touchPoint = change.position
                                    val vector = touchPoint - center
                                    val distance = vector.length()

                                    if (distance <= radius) {
                                        val angle = atan2(vector.y, vector.x)
                                        val hue = (angle.toDegrees() + 360f) % 360f
                                        val saturation = (distance / radius).coerceIn(0f, 1f)
                                        state.updateColor(Color.hsv(hue, saturation, hsv.value, state.currentColor.alpha))
                                    }
                                }
                            }
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    val center = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
                                    val radius = min(canvasSize.width, canvasSize.height) / 2f
                                    val vector = offset - center
                                    val distance = vector.length()

                                    if (distance <= radius) {
                                        val angle = atan2(vector.y, vector.x)
                                        val hue = (angle.toDegrees() + 360f) % 360f
                                        val saturation = (distance / radius).coerceIn(0f, 1f)
                                        state.updateColor(Color.hsv(hue, saturation, hsv.value, state.currentColor.alpha))
                                    }
                                }
                            }
                    } else Modifier
                )
        ) {
            canvasSize = IntSize(size.width.toInt(), size.height.toInt())
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = min(size.width, size.height) / 2f

            // Draw HSV wheel
            drawHSVWheel(center, radius, hsv.value)

            // Draw selector
            val angle = hsv.hue.toRadians()
            val distance = hsv.saturation * radius
            val selectorPos = Offset(
                center.x + distance * cos(angle),
                center.y + distance * sin(angle)
            )

            // Outer ring
            drawCircle(
                color = Color.White,
                radius = HierarchicalSize.Spacing.Small.toPx(),
                center = selectorPos,
                style = Stroke(width = HierarchicalSize.Border.Large.toPx())
            )
            // Inner ring
            drawCircle(
                color = state.currentColor,
                radius = HierarchicalSize.Spacing.Compact.toPx(),
                center = selectorPos
            )
        }
    }
}

private fun DrawScope.drawHSVWheel(center: Offset, radius: Float, value: Float) {
    val steps = 360
    val angleStep = 360f / steps

    for (i in 0 until steps) {
        val startAngle = i * angleStep
        val sweepAngle = angleStep

        val gradient = Brush.radialGradient(
            0f to Color.hsv(startAngle, 0f, value),
            1f to Color.hsv(startAngle, 1f, value),
            center = center,
            radius = radius
        )

        drawArc(
            brush = gradient,
            startAngle = startAngle - 90f,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        )
    }
}

// ============================================================================
// RGB Sliders
// ============================================================================

@Composable
private fun RGBSliders(
    color: Color,
    onColorChange: (Color) -> Unit,
    showAlpha: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
    ) {
        // Red slider
        ColorChannelSlider(
            label = "R",
            value = color.red,
            onValueChange = { onColorChange(color.copy(red = it)) },
            color = Color.Red,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        // Green slider
        ColorChannelSlider(
            label = "G",
            value = color.green,
            onValueChange = { onColorChange(color.copy(green = it)) },
            color = Color.Green,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        // Blue slider
        ColorChannelSlider(
            label = "B",
            value = color.blue,
            onValueChange = { onColorChange(color.copy(blue = it)) },
            color = Color.Blue,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        // Alpha slider
        if (showAlpha) {
            ColorChannelSlider(
                label = "A",
                value = color.alpha,
                onValueChange = { onColorChange(color.copy(alpha = it)) },
                color = Color.Gray,
                showCheckerboard = true,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ============================================================================
// HSV Sliders
// ============================================================================

@Composable
private fun HSVSliders(
    color: Color,
    onColorChange: (Color) -> Unit,
    showAlpha: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val hsv = color.toHSV()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
    ) {
        // Hue slider
        HueSlider(
            hue = hsv.hue,
            onHueChange = { onColorChange(Color.hsv(it, hsv.saturation, hsv.value, color.alpha)) },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        // Saturation slider
        ColorChannelSlider(
            label = "S",
            value = hsv.saturation,
            onValueChange = { onColorChange(Color.hsv(hsv.hue, it, hsv.value, color.alpha)) },
            gradientColors = listOf(
                Color.hsv(hsv.hue, 0f, hsv.value),
                Color.hsv(hsv.hue, 1f, hsv.value)
            ),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        // Value slider
        ColorChannelSlider(
            label = "V",
            value = hsv.value,
            onValueChange = { onColorChange(Color.hsv(hsv.hue, hsv.saturation, it, color.alpha)) },
            gradientColors = listOf(
                Color.hsv(hsv.hue, hsv.saturation, 0f),
                Color.hsv(hsv.hue, hsv.saturation, 1f)
            ),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        // Alpha slider
        if (showAlpha) {
            ColorChannelSlider(
                label = "A",
                value = color.alpha,
                onValueChange = { onColorChange(color.copy(alpha = it)) },
                color = Color.Gray,
                showCheckerboard = true,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ============================================================================
// HSL Sliders
// ============================================================================

@Composable
private fun HSLSliders(
    color: Color,
    onColorChange: (Color) -> Unit,
    showAlpha: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val hsl = color.toHSL()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
    ) {
        // Hue slider
        HueSlider(
            hue = hsl.hue,
            onHueChange = { onColorChange(Color.hsl(it, hsl.saturation, hsl.lightness, color.alpha)) },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        // Saturation slider
        ColorChannelSlider(
            label = "S",
            value = hsl.saturation,
            onValueChange = { onColorChange(Color.hsl(hsl.hue, it, hsl.lightness, color.alpha)) },
            gradientColors = listOf(
                Color.hsl(hsl.hue, 0f, hsl.lightness),
                Color.hsl(hsl.hue, 1f, hsl.lightness)
            ),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        // Lightness slider
        ColorChannelSlider(
            label = "L",
            value = hsl.lightness,
            onValueChange = { onColorChange(Color.hsl(hsl.hue, hsl.saturation, it, color.alpha)) },
            gradientColors = listOf(
                Color.Black,
                Color.hsl(hsl.hue, hsl.saturation, 0.5f),
                Color.White
            ),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        // Alpha slider
        if (showAlpha) {
            ColorChannelSlider(
                label = "A",
                value = color.alpha,
                onValueChange = { onColorChange(color.copy(alpha = it)) },
                color = Color.Gray,
                showCheckerboard = true,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ============================================================================
// Hue Slider
// ============================================================================

@Composable
private fun HueSlider(
    hue: Float,
    onHueChange: (Float) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val hueGradient = remember {
        Brush.horizontalGradient(
            0f to Color.hsv(0f, 1f, 1f),
            0.17f to Color.hsv(60f, 1f, 1f),
            0.33f to Color.hsv(120f, 1f, 1f),
            0.5f to Color.hsv(180f, 1f, 1f),
            0.67f to Color.hsv(240f, 1f, 1f),
            0.83f to Color.hsv(300f, 1f, 1f),
            1f to Color.hsv(360f, 1f, 1f)
        )
    }

    ColorChannelSlider(
        label = "H",
        value = hue / 360f,
        onValueChange = { onHueChange(it * 360f) },
        gradientBrush = hueGradient,
        enabled = enabled,
        modifier = modifier
    )
}

// ============================================================================
// Color Channel Slider
// ============================================================================

@Composable
private fun ColorChannelSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    color: Color? = null,
    gradientColors: List<Color>? = null,
    gradientBrush: Brush? = null,
    showCheckerboard: Boolean = false
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AppTheme.typography.bodyBold,
            modifier = Modifier.width(HierarchicalSize.Icon.Medium)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(HierarchicalSize.Container.Compact)
        ) {
            var sliderWidth by remember { mutableStateOf(0f) }

            // Track
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(HierarchicalSize.Radius.Huge))
                    .then(if (showCheckerboard) Modifier.drawCheckerboard() else Modifier)
            ) {
                sliderWidth = size.width

                val brush = when {
                    gradientBrush != null -> gradientBrush
                    gradientColors != null -> Brush.horizontalGradient(gradientColors)
                    color != null -> Brush.horizontalGradient(
                        listOf(Color.Transparent, color)
                    )
                    else -> Brush.horizontalGradient(
                        listOf(Color.LightGray, Color.DarkGray)
                    )
                }

                drawRoundRect(
                    brush = brush,
                    cornerRadius = CornerRadius(HierarchicalSize.Radius.Huge.toPx())
                )
            }

            // Thumb
            Box(
                modifier = Modifier
                    .offset(x = with(LocalDensity.current) { ((sliderWidth * value) - HierarchicalSize.Spacing.Small.toPx()).toDp() })
                    .size(HierarchicalSize.Icon.Medium)
                    .align(Alignment.CenterStart)
                    .shadow(HierarchicalSize.Shadow.Large, CircleShape)
                    .background(Color.White, CircleShape)
                    .border(HierarchicalSize.Border.Medium, AppTheme.colors.baseBorderDefault, CircleShape)
                    .then(
                        if (enabled) {
                            Modifier
                                .pointerInput(Unit) {
                                    detectDragGestures { change, _ ->
                                        val newValue = (change.position.x / sliderWidth).coerceIn(0f, 1f)
                                        onValueChange(newValue)
                                    }
                                }
                                .pointerInput(Unit) {
                                    detectTapGestures { offset ->
                                        val newValue = (offset.x / sliderWidth).coerceIn(0f, 1f)
                                        onValueChange(newValue)
                                    }
                                }
                        } else Modifier
                    )
            )
        }

        Text(
            text = (value * 255).roundToInt().toString(),
            style = AppTheme.typography.bodyRegular,
            modifier = Modifier.width(HierarchicalSize.Container.Small),
            textAlign = TextAlign.End
        )
    }
}

// ============================================================================
// Brightness Slider
// ============================================================================

@Composable
private fun BrightnessSlider(
    color: Color,
    onBrightnessChange: (Float) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val hsv = color.toHSV()

    ColorChannelSlider(
        label = "B",
        value = hsv.value,
        onValueChange = onBrightnessChange,
        gradientColors = listOf(Color.Black, Color.hsv(hsv.hue, hsv.saturation, 1f)),
        enabled = enabled,
        modifier = modifier
    )
}

// ============================================================================
// Alpha Slider
// ============================================================================

@Composable
private fun AlphaSlider(
    color: Color,
    onAlphaChange: (Float) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    ColorChannelSlider(
        label = "A",
        value = color.alpha,
        onValueChange = onAlphaChange,
        gradientColors = listOf(color.copy(alpha = 0f), color.copy(alpha = 1f)),
        showCheckerboard = true,
        enabled = enabled,
        modifier = modifier
    )
}

// ============================================================================
// Hex Input
// ============================================================================

@Composable
private fun HexInput(
    hexValue: String,
    onHexChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var text by remember(hexValue) { mutableStateOf(hexValue) }
    var isError by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = text,
        onValueChange = { newValue ->
            text = newValue
            val color = Color.fromHex(newValue)
            isError = color == null && newValue.isNotEmpty()
            color?.let { onHexChange(newValue) }
        },
        label = "Hex Color",
        placeholder = "#AARRGGBB",
        isError = isError,
        helperText = if (isError) "Invalid hex color" else null,
        enabled = enabled,
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Done
        )
    )
}

// ============================================================================
// Color History
// ============================================================================

@Composable
private fun ColorHistory(
    colors: List<Color>,
    onColorSelected: (Color) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            "Recent Colors",
            style = AppTheme.typography.captionBold,
            modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Compact)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)
        ) {
            colors.take(8).forEach { color ->
                Box(
                    modifier = Modifier
                        .size(HierarchicalSize.Container.Small)
                        .clip(RoundedCornerShape(HierarchicalSize.Radius.Small))
                        .clickable(enabled = enabled) { onColorSelected(color) }
                        .drawCheckerboard()
                        .background(color)
                        .border(HierarchicalSize.Border.Nano, AppTheme.colors.baseBorderDefault, RoundedCornerShape(HierarchicalSize.Radius.Small))
                        .semantics {
                            contentDescription = "Recent color ${color.toHexString(false)}"
                        }
                )
            }
        }
    }
}

// ============================================================================
// Helper Functions
// ============================================================================

/**
 * Draws a checkerboard pattern for transparency preview
 */
private fun Modifier.drawCheckerboard(
    tileSize: Dp = HierarchicalSize.Spacing.Compact,
    colorLight: Color = Color(0xFFFFFFFF),
    colorDark: Color = Color(0xFFCCCCCC)
): Modifier = this.drawBehind {
    val tileSizePx = tileSize.toPx()
    val numTilesX = (size.width / tileSizePx).toInt() + 1
    val numTilesY = (size.height / tileSizePx).toInt() + 1

    for (i in 0 until numTilesX) {
        for (j in 0 until numTilesY) {
            val isEven = (i + j) % 2 == 0
            drawRect(
                color = if (isEven) colorLight else colorDark,
                topLeft = Offset(i * tileSizePx, j * tileSizePx),
                size = androidx.compose.ui.geometry.Size(tileSizePx, tileSizePx)
            )
        }
    }
}

