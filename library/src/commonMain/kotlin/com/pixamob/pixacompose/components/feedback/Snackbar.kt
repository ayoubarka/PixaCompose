package com.pixamob.pixacompose.components.feedback

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.utils.elevationShadow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.abs

/**
 * Snackbar Component
 *
 * Temporary notification at screen bottom with optional action button.
 * Unlike Toast, Snackbar shows one message at a time with more prominent actions.
 * Supports swipe-to-dismiss and automatic queuing.
 *
 * Features:
 * - Four semantic variants: Info, Success, Warning, Error, Default
 * - Three duration presets: Short (4s), Long (10s), Indefinite (until dismissed/action)
 * - Prominent action button with custom styling
 * - Swipe-to-dismiss gesture support
 * - Single message display (queues subsequent messages)
 * - Bottom positioning with proper safe area padding
 * - Full accessibility support
 * - Smooth animations
 *
 * @sample
 * ```
 * // Basic usage
 * val snackbarState = rememberSnackbarHostState()
 *
 * SnackbarHost(hostState = snackbarState)
 *
 * // Show snackbar
 * scope.launch {
 *     snackbarState.showSnackbar(
 *         message = "Item deleted",
 *         actionLabel = "Undo"
 *     ) { /* undo action */ }
 * }
 *
 * // Show error snackbar
 * scope.launch {
 *     snackbarState.showErrorSnackbar(
 *         message = "Network error occurred",
 *         actionLabel = "Retry"
 *     ) { retryConnection() }
 * }
 * ```
 */

// ============================================================================
// CONFIGURATION
// ============================================================================

/**
 * Snackbar semantic variants
 */
enum class SnackbarVariant {
    /** Default/neutral - dark gray */
    Default,
    /** Informational message - blue */
    Info,
    /** Success/positive message - green */
    Success,
    /** Warning/caution message - orange */
    Warning,
    /** Error/critical message - red */
    Error
}

/**
 * Snackbar duration presets
 */
enum class SnackbarDuration(val milliseconds: Long) {
    /** Short duration - 4 seconds */
    Short(4000L),
    /** Long duration - 10 seconds */
    Long(10000L),
    /** Indefinite - stays until dismissed or action taken */
    Indefinite(-1L)
}

/**
 * Snackbar colors for different states
 */
@Immutable
@Stable
data class SnackbarColors(
    val background: Color,
    val message: Color,
    val actionLabel: Color,
    val actionBackground: Color = Color.Transparent
)

/**
 * Snackbar configuration
 */
@Immutable
@Stable
data class SnackbarConfig(
    val messageStyle: @Composable () -> TextStyle,
    val actionStyle: @Composable () -> TextStyle,
    val padding: Dp = Inset.Medium,
    val actionSpacing: Dp = Spacing.Small,
    val maxMessageLines: Int = 2,
    val minWidth: Dp = ComponentSize.DialogMinWidth, // 280.dp
    val maxWidth: Dp = ComponentSize.DialogMaxWidth, // 560.dp
    val minHeight: Dp = ComponentSize.SnackbarSingleLine, // 48.dp
    val elevation: ComponentElevation = ComponentElevation.Highest, // 8dp for prominence
    val cornerRadius: Dp = RadiusSize.Small,
    val swipeToDismissThreshold: Float = 0.3f,
    val iconSize: Dp = IconSize.Small // 20.dp
)

/**
 * Data class representing a snackbar item
 */
@Stable
data class SnackbarData(
    val id: Long = kotlin.random.Random.nextLong(),
    val message: String,
    val actionLabel: String? = null,
    val variant: SnackbarVariant = SnackbarVariant.Default,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val withDismissAction: Boolean = false,
    val icon: Painter? = null,
    val showIcon: Boolean = false,
    val onAction: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null,
    val customColors: SnackbarColors? = null
)

/**
 * Result of showing a snackbar
 */
enum class SnackbarResult {
    /** User clicked the action button */
    ActionPerformed,
    /** Snackbar was dismissed (swiped, timeout, or dismissed programmatically) */
    Dismissed
}

// ============================================================================
// SNACKBAR HOST STATE
// ============================================================================

/**
 * State holder for managing snackbar queue and display
 */
@Stable
class PixaSnackbarHostState {
    private val mutex = Mutex()
    private val _currentSnackbar = mutableStateOf<SnackbarData?>(null)
    private val _snackbarQueue = mutableListOf<SnackbarData>()

    /**
     * Current visible snackbar
     */
    val currentSnackbar: SnackbarData? by _currentSnackbar

    /**
     * Show a snackbar message
     *
     * @return Result indicating whether action was performed or dismissed
     */
    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        variant: SnackbarVariant = SnackbarVariant.Default,
        duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
        withDismissAction: Boolean = false,
        icon: Painter? = null,
        showIcon: Boolean = false,
        onAction: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null,
        customColors: SnackbarColors? = null
    ): SnackbarResult = mutex.withLock {
        val snackbar = SnackbarData(
            message = message,
            actionLabel = actionLabel,
            variant = variant,
            duration = duration,
            withDismissAction = withDismissAction,
            icon = icon,
            showIcon = showIcon,
            onAction = onAction,
            onDismiss = onDismiss,
            customColors = customColors
        )

        // If there's a current snackbar, queue this one
        if (_currentSnackbar.value != null) {
            _snackbarQueue.add(snackbar)
            return SnackbarResult.Dismissed
        }

        // Show the snackbar
        _currentSnackbar.value = snackbar

        // Auto-dismiss if duration is not indefinite
        if (duration != SnackbarDuration.Indefinite) {
            delay(duration.milliseconds)
            dismissCurrentSnackbar()
        }

        return SnackbarResult.Dismissed
    }

    /**
     * Dismiss current snackbar
     */
    suspend fun dismissCurrentSnackbar() {
        mutex.withLock {
            _currentSnackbar.value?.onDismiss?.invoke()
            _currentSnackbar.value = null

            // Show next in queue if any
            if (_snackbarQueue.isNotEmpty()) {
                val next = _snackbarQueue.removeAt(0)
                _currentSnackbar.value = next

                if (next.duration != SnackbarDuration.Indefinite) {
                    delay(next.duration.milliseconds)
                    dismissCurrentSnackbar()
                }
            }
        }
    }

    /**
     * Perform action and dismiss current snackbar
     */
    suspend fun performAction() {
        mutex.withLock {
            _currentSnackbar.value?.onAction?.invoke()
            dismissCurrentSnackbar()
        }
    }

    /**
     * Show success snackbar (convenience method)
     */
    suspend fun showSuccessSnackbar(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onAction: (() -> Unit)? = null
    ): SnackbarResult {
        return showSnackbar(
            message = message,
            actionLabel = actionLabel,
            variant = SnackbarVariant.Success,
            duration = duration,
            onAction = onAction
        )
    }

    /**
     * Show error snackbar (convenience method)
     */
    suspend fun showErrorSnackbar(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Long,
        onAction: (() -> Unit)? = null
    ): SnackbarResult {
        return showSnackbar(
            message = message,
            actionLabel = actionLabel,
            variant = SnackbarVariant.Error,
            duration = duration,
            onAction = onAction
        )
    }

    /**
     * Show warning snackbar (convenience method)
     */
    suspend fun showWarningSnackbar(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Long,
        onAction: (() -> Unit)? = null
    ): SnackbarResult {
        return showSnackbar(
            message = message,
            actionLabel = actionLabel,
            variant = SnackbarVariant.Warning,
            duration = duration,
            onAction = onAction
        )
    }

    /**
     * Show info snackbar (convenience method)
     */
    suspend fun showInfoSnackbar(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onAction: (() -> Unit)? = null
    ): SnackbarResult {
        return showSnackbar(
            message = message,
            actionLabel = actionLabel,
            variant = SnackbarVariant.Info,
            duration = duration,
            onAction = onAction
        )
    }
}

/**
 * Remember snackbar host state
 */
@Composable
fun rememberSnackbarHostState(): PixaSnackbarHostState {
    return remember { PixaSnackbarHostState() }
}

// ============================================================================
// THEME PROVIDER
// ============================================================================

/**
 * Get snackbar colors based on variant
 */
@Composable
private fun getSnackbarColors(
    variant: SnackbarVariant,
    colors: ColorPalette
): SnackbarColors {
    return when (variant) {
        SnackbarVariant.Default -> SnackbarColors(
            background = colors.baseSurfaceFocus, // Dark surface for prominence
            message = colors.baseContentNegative, // Light text on dark background
            actionLabel = colors.brandContentDefault, // Use brand color for action
            actionBackground = Color.Transparent
        )
        SnackbarVariant.Info -> SnackbarColors(
            background = colors.infoSurfaceDefault,
            message = colors.baseContentBody,
            actionLabel = colors.infoContentDefault,
            actionBackground = Color.Transparent
        )
        SnackbarVariant.Success -> SnackbarColors(
            background = colors.successSurfaceDefault,
            message = colors.baseContentBody,
            actionLabel = colors.successContentDefault,
            actionBackground = Color.Transparent
        )
        SnackbarVariant.Warning -> SnackbarColors(
            background = colors.warningSurfaceDefault,
            message = colors.baseContentBody,
            actionLabel = colors.warningContentDefault,
            actionBackground = Color.Transparent
        )
        SnackbarVariant.Error -> SnackbarColors(
            background = colors.errorSurfaceDefault,
            message = colors.baseContentBody,
            actionLabel = colors.errorContentDefault,
            actionBackground = Color.Transparent
        )
    }
}

/**
 * Get snackbar configuration
 */
@Composable
private fun getSnackbarConfig(): SnackbarConfig {
    val typography = AppTheme.typography
    return SnackbarConfig(
        messageStyle = { typography.bodyRegular },
        actionStyle = { typography.bodyBold }
    )
}

// ============================================================================
// PUBLIC API
// ============================================================================

/**
 * Snackbar - Single snackbar message display
 *
 * @param data Snackbar data containing message and configuration
 * @param onDismiss Callback when snackbar is dismissed
 * @param onActionPerformed Callback when action is performed
 * @param modifier Modifier for the snackbar
 */
@Composable
internal fun Snackbar(
    data: SnackbarData,
    onDismiss: () -> Unit,
    onActionPerformed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = data.customColors ?: getSnackbarColors(data.variant, AppTheme.colors)
    val config = getSnackbarConfig()

    var offsetX by remember { mutableStateOf(0f) }
    val dismissThreshold = config.swipeToDismissThreshold

    val description = "${data.variant.name.lowercase()} snackbar: ${data.message}"
    val shape = RoundedCornerShape(config.cornerRadius)

    Box(
        modifier = modifier
            .widthIn(min = config.minWidth, max = config.maxWidth)
            .heightIn(min = config.minHeight)
            .padding(horizontal = Inset.Medium, vertical = Spacing.Small)
            .graphicsLayer {
                translationX = offsetX
                alpha = 1f - (abs(offsetX) / 1000f).coerceIn(0f, 0.5f)
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (abs(offsetX) > size.width * dismissThreshold) {
                            onDismiss()
                        } else {
                            offsetX = 0f
                        }
                    },
                    onDragCancel = {
                        offsetX = 0f
                    }
                ) { _, dragAmount ->
                    offsetX += dragAmount
                }
            }
            .semantics {
                this.contentDescription = description
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .elevationShadow(
                    elevation = config.elevation,
                    shape = shape
                )
                .clip(shape)
                .background(colors.background)
                .padding(config.padding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(config.actionSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon (optional)
                if (data.showIcon && data.icon != null) {
                    PixaIcon(
                        painter = data.icon,
                        contentDescription = null,
                        tint = colors.message,
                        modifier = Modifier.size(config.iconSize)
                    )
                }

                // Message
                Text(
                    text = data.message,
                    style = config.messageStyle(),
                    color = colors.message,
                    maxLines = config.maxMessageLines,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Action button
                data.actionLabel?.let { actionLabel ->
                    Box(
                        modifier = Modifier
                            .heightIn(min = ComponentSize.Medium) // 44dp touch target
                            .clip(RoundedCornerShape(RadiusSize.Small))
                            .clickable(
                                onClick = onActionPerformed,
                                indication = ripple(bounded = true),
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            .padding(
                                horizontal = Inset.Small,
                                vertical = Spacing.ExtraSmall
                            )
                            .semantics {
                                this.role = Role.Button
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = actionLabel.uppercase(),
                            style = config.actionStyle(),
                            color = colors.actionLabel
                        )
                    }
                }

                // Dismiss button (if enabled)
                if (data.withDismissAction) {
                    Box(
                        modifier = Modifier
                            .size(ComponentSize.Medium) // 44dp touch target
                            .clip(RoundedCornerShape(RadiusSize.Small))
                            .clickable(
                                onClick = onDismiss,
                                indication = ripple(bounded = true),
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            .semantics {
                                this.contentDescription = "Dismiss"
                                this.role = Role.Button
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "×",
                            style = config.actionStyle(),
                            color = colors.message
                        )
                    }
                }
            }
        }
    }
}

/**
 * SnackbarHost - Container for displaying snackbar
 *
 * @param hostState State holder managing snackbar queue
 * @param modifier Modifier for the host container
 */
@Composable
fun SnackbarHost(
    hostState: PixaSnackbarHostState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val currentSnackbar = hostState.currentSnackbar

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = Spacing.Medium), // Safe area padding for mobile
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = currentSnackbar != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeOut(animationSpec = tween(200))
        ) {
            currentSnackbar?.let { snackbar ->
                Snackbar(
                    data = snackbar,
                    onDismiss = {
                        coroutineScope.launch {
                            hostState.dismissCurrentSnackbar()
                        }
                    },
                    onActionPerformed = {
                        coroutineScope.launch {
                            hostState.performAction()
                        }
                    }
                )
            }
        }
    }
}

// ============================================================================
// USAGE EXAMPLES
// ============================================================================

/**
 * USAGE EXAMPLES:
 *
 * 1. Basic snackbar host setup:
 * ```
 * @Composable
 * fun MyScreen() {
 *     val snackbarState = rememberSnackbarHostState()
 *
 *     Scaffold(
 *         snackbarHost = { SnackbarHost(hostState = snackbarState) }
 *     ) { padding ->
 *         // Your content here
 *     }
 * }
 * ```
 *
 * 2. Show simple snackbar:
 * ```
 * val scope = rememberCoroutineScope()
 * Button(onClick = {
 *     scope.launch {
 *         snackbarState.showSnackbar("Settings saved")
 *     }
 * })
 * ```
 *
 * 3. Show snackbar with action:
 * ```
 * scope.launch {
 *     snackbarState.showSnackbar(
 *         message = "Email deleted",
 *         actionLabel = "Undo",
 *         onAction = { undoDelete() }
 *     )
 * }
 * ```
 *
 * 4. Show error snackbar:
 * ```
 * scope.launch {
 *     snackbarState.showErrorSnackbar(
 *         message = "Failed to save changes",
 *         actionLabel = "Retry",
 *         onAction = { retrySave() }
 *     )
 * }
 * ```
 *
 * 5. Show success snackbar:
 * ```
 * scope.launch {
 *     snackbarState.showSuccessSnackbar(
 *         message = "File uploaded successfully"
 *     )
 * }
 * ```
 *
 * 6. Indefinite snackbar (until action/dismiss):
 * ```
 * scope.launch {
 *     snackbarState.showSnackbar(
 *         message = "No internet connection",
 *         actionLabel = "Retry",
 *         duration = SnackbarDuration.Indefinite,
 *         withDismissAction = true,
 *         onAction = { checkConnection() }
 *     )
 * }
 * ```
 *
 * 7. Warning snackbar:
 * ```
 * scope.launch {
 *     snackbarState.showWarningSnackbar(
 *         message = "Battery low",
 *         actionLabel = "Settings",
 *         onAction = { openBatterySettings() }
 *     )
 * }
 * ```
 *
 * 8. Info snackbar:
 * ```
 * scope.launch {
 *     snackbarState.showInfoSnackbar(
 *         message = "Update available",
 *         actionLabel = "Download",
 *         onAction = { startDownload() }
 *     )
 * }
 * ```
 *
 * 9. Snackbar with icon:
 * ```
 * scope.launch {
 *     snackbarState.showSnackbar(
 *         message = "Message sent",
 *         icon = painterResource(R.drawable.ic_send),
 *         showIcon = true
 *     )
 * }
 * ```
 *
 * 10. Custom colors snackbar:
 * ```
 * scope.launch {
 *     snackbarState.showSnackbar(
 *         message = "Premium feature activated",
 *         variant = SnackbarVariant.Default,
 *         customColors = SnackbarColors(
 *             background = Color(0xFFFFD700),
 *             message = Color.Black,
 *             actionLabel = Color(0xFF1976D2)
 *         )
 *     )
 * }
 * ```
 *
 * 11. Long duration snackbar:
 * ```
 * scope.launch {
 *     snackbarState.showSnackbar(
 *         message = "Downloading 5 files...",
 *         duration = SnackbarDuration.Long
 *     )
 * }
 * ```
 *
 * 12. Multiple snackbars (queued):
 * ```
 * // Subsequent snackbars are queued automatically
 * scope.launch {
 *     snackbarState.showSnackbar("First message")
 *     snackbarState.showSnackbar("Second message")
 *     snackbarState.showSnackbar("Third message")
 *     // They will show one after another
 * }
 * ```
 *
 * 13. Snackbar with dismiss callback:
 * ```
 * scope.launch {
 *     snackbarState.showSnackbar(
 *         message = "Operation in progress",
 *         onDismiss = {
 *             logEvent("Snackbar dismissed")
 *         }
 *     )
 * }
 * ```
 *
 * 14. Programmatic dismiss:
 * ```
 * scope.launch {
 *     snackbarState.showSnackbar(
 *         message = "Loading...",
 *         duration = SnackbarDuration.Indefinite
 *     )
 *
 *     // Later when done
 *     snackbarState.dismissCurrentSnackbar()
 * }
 * ```
 *
 * 15. Snackbar in bottom sheet:
 * ```
 * Box(modifier = Modifier.fillMaxSize()) {
 *     // Your content
 *
 *     SnackbarHost(
 *         hostState = snackbarState,
 *         modifier = Modifier.padding(bottom = 80.dp) // Above bottom nav
 *     )
 * }
 * ```
 */
