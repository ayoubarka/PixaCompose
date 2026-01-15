package com.pixamob.pixacompose.components.feedback

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaCard
import com.pixamob.pixacompose.components.display.BaseCardElevation
import com.pixamob.pixacompose.components.display.BaseCardPadding
import com.pixamob.pixacompose.components.display.BaseCardVariant
import com.pixamob.pixacompose.components.display.Icon
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.utils.elevationShadow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

/**
 * Toast Component
 *
 * Temporary notification messages that auto-dismiss and don't block user interaction.
 * Supports stacking, positioning, animations, and semantic colors.
 *
 * Features:
 * - Four semantic variants: Info, Success, Warning, Error
 * - Three duration presets: Short (2s), Long (4s), Unlimited (manual dismiss)
 * - Multiple screen positions: Top, Bottom, TopStart, TopEnd, BottomStart, BottomEnd, Center
 * - Three visual styles: Filled, Outlined, Subtle
 * - Toast stacking with configurable limit (default 3)
 * - Smooth enter/exit animations based on position
 * - Optional icon and action button
 * - Full accessibility support
 * - Card-based layout with proper spacing
 *
 * @sample
 * ```
 * // Basic usage with ToastHost
 * val toastState = rememberToastHostState()
 *
 * ToastHost(
 *     hostState = toastState,
 *     position = ToastPosition.Bottom
 * )
 *
 * // Show toast from coroutine
 * LaunchedEffect(Unit) {
 *     toastState.showToast(
 *         message = "File uploaded successfully",
 *         variant = ToastVariant.Success,
 *         duration = ToastDuration.Short
 *     )
 * }
 *
 * // Quick success toast
 * scope.launch {
 *     toastState.showSuccessToast("Changes saved!")
 * }
 * ```
 */

// ============================================================================
// CONFIGURATION
// ============================================================================

/**
 * Toast semantic variants
 */
enum class ToastVariant {
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
 * Toast duration presets
 */
enum class ToastDuration(val milliseconds: Long) {
    /** Short duration - 2 seconds */
    Short(2000L),
    /** Long duration - 4 seconds */
    Long(4000L),
    /** Unlimited - must be manually dismissed */
    Unlimited(-1L)
}

/**
 * Toast screen position
 */
enum class ToastPosition {
    /** Top center of screen */
    Top,
    /** Bottom center of screen */
    Bottom,
    /** Top start (left in LTR) */
    TopStart,
    /** Top end (right in LTR) */
    TopEnd,
    /** Bottom start (left in LTR) */
    BottomStart,
    /** Bottom end (right in LTR) */
    BottomEnd,
    /** Center of screen */
    Center
}

/**
 * Toast visual style
 */
enum class ToastStyle {
    /** Filled background with semantic color */
    Filled,
    /** Outlined border with white/transparent background */
    Outlined,
    /** Subtle background tint */
    Subtle
}

/**
 * Toast colors for different states
 */
@Immutable
@Stable
data class ToastColors(
    val background: Color,
    val border: Color,
    val icon: Color,
    val message: Color,
    val action: Color,
    val close: Color
)

/**
 * Toast configuration
 */
@Immutable
@Stable
data class ToastConfig(
    val iconSize: Dp = IconSize.Small, // 20.dp
    val minTouchTarget: Dp = ComponentSize.Medium, // 44.dp touch target for mobile
    val messageStyle: @Composable () -> TextStyle,
    val actionStyle: @Composable () -> TextStyle,
    val spacing: Dp = Spacing.Small,
    val padding: Dp = Inset.Medium,
    val maxMessageLines: Int = 2,
    val minWidth: Dp = 200.dp, // Minimum for readability on mobile
    val maxWidth: Dp = ComponentSize.DialogMaxWidth.minus(32.dp), // 528.dp (560-32 for margins)
    val elevation: ComponentElevation = ComponentElevation.Medium, // 2dp standard elevation
    val cornerRadius: Dp = RadiusSize.Medium
)

/**
 * Data class representing a toast item
 */
@Stable
data class ToastData(
    val id: Long = Random.nextLong(),
    val message: String,
    val variant: ToastVariant = ToastVariant.Info,
    val duration: ToastDuration = ToastDuration.Short,
    val style: ToastStyle = ToastStyle.Filled,
    val icon: Painter? = null,
    val showIcon: Boolean = true,
    val dismissible: Boolean = true,
    val actionText: String? = null,
    val onAction: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null,
    val customColors: ToastColors? = null
)

// ============================================================================
// TOAST HOST STATE
// ============================================================================

/**
 * State holder for managing toast queue and display
 */
@Stable
class PixaToastHostState(
    private val maxToasts: Int = 3
) {
    private val mutex = Mutex()
    private val _currentToasts = mutableStateListOf<ToastData>()

    /**
     * Current visible toasts
     */
    val currentToasts: List<ToastData> = _currentToasts

    /**
     * Show a toast message
     */
    suspend fun showToast(
        message: String,
        variant: ToastVariant = ToastVariant.Info,
        duration: ToastDuration = ToastDuration.Short,
        style: ToastStyle = ToastStyle.Filled,
        icon: Painter? = null,
        showIcon: Boolean = true,
        dismissible: Boolean = true,
        actionText: String? = null,
        onAction: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null,
        customColors: ToastColors? = null
    ) {
        mutex.withLock {
            val toast = ToastData(
                message = message,
                variant = variant,
                duration = duration,
                style = style,
                icon = icon,
                showIcon = showIcon,
                dismissible = dismissible,
                actionText = actionText,
                onAction = onAction,
                onDismiss = onDismiss,
                customColors = customColors
            )

            // Enforce stack limit - remove oldest if at max
            if (_currentToasts.size >= maxToasts) {
                val oldest = _currentToasts.firstOrNull()
                oldest?.let { dismissToast(it.id) }
            }

            _currentToasts.add(toast)

            // Auto-dismiss if duration is not unlimited
            if (duration != ToastDuration.Unlimited) {
                delay(duration.milliseconds)
                dismissToast(toast.id)
            }
        }
    }

    /**
     * Dismiss a specific toast by ID
     */
    suspend fun dismissToast(id: Long) {
        mutex.withLock {
            val toast = _currentToasts.find { it.id == id }
            toast?.onDismiss?.invoke()
            _currentToasts.removeAll { it.id == id }
        }
    }

    /**
     * Dismiss all toasts
     */
    suspend fun dismissAll() {
        mutex.withLock {
            _currentToasts.forEach { it.onDismiss?.invoke() }
            _currentToasts.clear()
        }
    }

    /**
     * Show success toast (convenience method)
     */
    suspend fun showSuccessToast(
        message: String,
        duration: ToastDuration = ToastDuration.Short,
        actionText: String? = null,
        onAction: (() -> Unit)? = null
    ) {
        showToast(
            message = message,
            variant = ToastVariant.Success,
            duration = duration,
            actionText = actionText,
            onAction = onAction
        )
    }

    /**
     * Show error toast (convenience method)
     */
    suspend fun showErrorToast(
        message: String,
        duration: ToastDuration = ToastDuration.Long,
        actionText: String? = null,
        onAction: (() -> Unit)? = null
    ) {
        showToast(
            message = message,
            variant = ToastVariant.Error,
            duration = duration,
            actionText = actionText,
            onAction = onAction
        )
    }

    /**
     * Show warning toast (convenience method)
     */
    suspend fun showWarningToast(
        message: String,
        duration: ToastDuration = ToastDuration.Long,
        actionText: String? = null,
        onAction: (() -> Unit)? = null
    ) {
        showToast(
            message = message,
            variant = ToastVariant.Warning,
            duration = duration,
            actionText = actionText,
            onAction = onAction
        )
    }

    /**
     * Show info toast (convenience method)
     */
    suspend fun showInfoToast(
        message: String,
        duration: ToastDuration = ToastDuration.Short,
        actionText: String? = null,
        onAction: (() -> Unit)? = null
    ) {
        showToast(
            message = message,
            variant = ToastVariant.Info,
            duration = duration,
            actionText = actionText,
            onAction = onAction
        )
    }
}

/**
 * Remember toast host state
 */
@Composable
fun rememberToastHostState(maxToasts: Int = 3): PixaToastHostState {
    return remember { PixaToastHostState(maxToasts) }
}

// ============================================================================
// THEME PROVIDER
// ============================================================================

/**
 * Get toast colors based on variant and style
 */
@Composable
private fun getToastColors(
    variant: ToastVariant,
    style: ToastStyle,
    colors: ColorPalette
): ToastColors {
    return when (variant) {
        ToastVariant.Info -> when (style) {
            ToastStyle.Filled -> ToastColors(
                background = colors.infoSurfaceDefault,
                border = colors.infoBorderDefault,
                icon = colors.infoContentDefault,
                message = colors.baseContentBody,
                action = colors.infoContentDefault,
                close = colors.baseContentBody
            )
            ToastStyle.Outlined -> ToastColors(
                background = colors.baseSurfaceDefault,
                border = colors.infoBorderDefault,
                icon = colors.infoContentDefault,
                message = colors.baseContentBody,
                action = colors.infoContentDefault,
                close = colors.baseContentBody
            )
            ToastStyle.Subtle -> ToastColors(
                background = colors.infoSurfaceSubtle,
                border = Color.Transparent,
                icon = colors.infoContentDefault,
                message = colors.baseContentBody,
                action = colors.infoContentDefault,
                close = colors.baseContentBody
            )
        }
        ToastVariant.Success -> when (style) {
            ToastStyle.Filled -> ToastColors(
                background = colors.successSurfaceDefault,
                border = colors.successBorderDefault,
                icon = colors.successContentDefault,
                message = colors.baseContentBody,
                action = colors.successContentDefault,
                close = colors.baseContentBody
            )
            ToastStyle.Outlined -> ToastColors(
                background = colors.baseSurfaceDefault,
                border = colors.successBorderDefault,
                icon = colors.successContentDefault,
                message = colors.baseContentBody,
                action = colors.successContentDefault,
                close = colors.baseContentBody
            )
            ToastStyle.Subtle -> ToastColors(
                background = colors.successSurfaceSubtle,
                border = Color.Transparent,
                icon = colors.successContentDefault,
                message = colors.baseContentBody,
                action = colors.successContentDefault,
                close = colors.baseContentBody
            )
        }
        ToastVariant.Warning -> when (style) {
            ToastStyle.Filled -> ToastColors(
                background = colors.warningSurfaceDefault,
                border = colors.warningBorderDefault,
                icon = colors.warningContentDefault,
                message = colors.baseContentBody,
                action = colors.warningContentDefault,
                close = colors.baseContentBody
            )
            ToastStyle.Outlined -> ToastColors(
                background = colors.baseSurfaceDefault,
                border = colors.warningBorderDefault,
                icon = colors.warningContentDefault,
                message = colors.baseContentBody,
                action = colors.warningContentDefault,
                close = colors.baseContentBody
            )
            ToastStyle.Subtle -> ToastColors(
                background = colors.warningSurfaceSubtle,
                border = Color.Transparent,
                icon = colors.warningContentDefault,
                message = colors.baseContentBody,
                action = colors.warningContentDefault,
                close = colors.baseContentBody
            )
        }
        ToastVariant.Error -> when (style) {
            ToastStyle.Filled -> ToastColors(
                background = colors.errorSurfaceDefault,
                border = colors.errorBorderDefault,
                icon = colors.errorContentDefault,
                message = colors.baseContentBody,
                action = colors.errorContentDefault,
                close = colors.baseContentBody
            )
            ToastStyle.Outlined -> ToastColors(
                background = colors.baseSurfaceDefault,
                border = colors.errorBorderDefault,
                icon = colors.errorContentDefault,
                message = colors.baseContentBody,
                action = colors.errorContentDefault,
                close = colors.baseContentBody
            )
            ToastStyle.Subtle -> ToastColors(
                background = colors.errorSurfaceSubtle,
                border = Color.Transparent,
                icon = colors.errorContentDefault,
                message = colors.baseContentBody,
                action = colors.errorContentDefault,
                close = colors.baseContentBody
            )
        }
    }
}

/**
 * Get toast configuration
 */
@Composable
private fun getToastConfig(): ToastConfig {
    val typography = AppTheme.typography
    return ToastConfig(
        messageStyle = { typography.bodyRegular },
        actionStyle = { typography.bodyBold }
    )
}

// ============================================================================
// TOAST ANIMATIONS
// ============================================================================

/**
 * Get enter transition based on position
 */
private fun getEnterTransition(position: ToastPosition): EnterTransition {
    return when (position) {
        ToastPosition.Top, ToastPosition.TopStart, ToastPosition.TopEnd -> {
            slideInVertically(
                initialOffsetY = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(animationSpec = tween(300))
        }
        ToastPosition.Bottom, ToastPosition.BottomStart, ToastPosition.BottomEnd -> {
            slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(animationSpec = tween(300))
        }
        ToastPosition.Center -> {
            scaleIn(
                initialScale = 0.8f,
                animationSpec = spring()
            ) + fadeIn(animationSpec = tween(300))
        }
    }
}

/**
 * Get exit transition based on position
 */
private fun getExitTransition(position: ToastPosition): ExitTransition {
    return when (position) {
        ToastPosition.Top, ToastPosition.TopStart, ToastPosition.TopEnd -> {
            slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeOut(animationSpec = tween(200))
        }
        ToastPosition.Bottom, ToastPosition.BottomStart, ToastPosition.BottomEnd -> {
            slideOutVertically(
                targetOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeOut(animationSpec = tween(200))
        }
        ToastPosition.Center -> {
            scaleOut(
                targetScale = 0.8f,
                animationSpec = spring()
            ) + fadeOut(animationSpec = tween(200))
        }
    }
}

/**
 * Get alignment based on position
 */
private fun getAlignment(position: ToastPosition): Alignment {
    return when (position) {
        ToastPosition.Top -> Alignment.TopCenter
        ToastPosition.Bottom -> Alignment.BottomCenter
        ToastPosition.TopStart -> Alignment.TopStart
        ToastPosition.TopEnd -> Alignment.TopEnd
        ToastPosition.BottomStart -> Alignment.BottomStart
        ToastPosition.BottomEnd -> Alignment.BottomEnd
        ToastPosition.Center -> Alignment.Center
    }
}

// ============================================================================
// PUBLIC API
// ============================================================================

/**
 * Toast - Single toast message display
 *
 * @param data Toast data containing message and configuration
 * @param onDismiss Callback when toast is dismissed
 * @param modifier Modifier for the toast
 */
@Composable
internal fun Toast(
    data: ToastData,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = data.customColors ?: getToastColors(data.variant, data.style, AppTheme.colors)
    val config = getToastConfig()

    val description = "${data.variant.name.lowercase()} toast: ${data.message}"
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(config.cornerRadius)

    Box(
        modifier = modifier
            .widthIn(min = config.minWidth, max = config.maxWidth)
            .padding(horizontal = Inset.Medium)
            .semantics {
                this.contentDescription = description
            }
    ) {
        PixaCard(
            modifier = Modifier
                .fillMaxWidth()
                .elevationShadow(
                    elevation = config.elevation,
                    shape = shape
                ),
            variant = when (data.style) {
                ToastStyle.Outlined -> BaseCardVariant.Outlined
                else -> BaseCardVariant.Filled
            },
            elevation = BaseCardElevation.None, // Using elevationShadow instead
            padding = BaseCardPadding.None,
            backgroundColor = colors.background
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(config.padding),
                horizontalArrangement = Arrangement.spacedBy(config.spacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                if (data.showIcon) {
                    Box(
                        modifier = Modifier.size(config.iconSize),
                        contentAlignment = Alignment.Center
                    ) {
                        data.icon?.let {
                            Icon(
                                painter = it,
                                contentDescription = null,
                                tint = colors.icon,
                                modifier = Modifier.fillMaxSize()
                            )
                        } ?: run {
                            // Fallback: colored circle indicator
                            Box(
                                modifier = Modifier
                                    .size(Spacing.ExtraSmall) // 8.dp
                                    .clip(CircleShape)
                                    .background(colors.icon)
                            )
                        }
                    }
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
                data.actionText?.let { actionText ->
                    Text(
                        text = actionText,
                        style = config.actionStyle(),
                        color = colors.action,
                        modifier = Modifier
                            .heightIn(min = config.minTouchTarget) // Touch target for mobile
                            .clickable(
                                onClick = {
                                    data.onAction?.invoke()
                                    onDismiss()
                                },
                                indication = ripple(bounded = false),
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            .padding(horizontal = Spacing.Small)
                            .semantics {
                                this.role = Role.Button
                            }
                    )
                }

                // Close button
                if (data.dismissible) {
                    Box(
                        modifier = Modifier
                            .size(config.minTouchTarget) // 44dp touch target
                            .clip(CircleShape)
                            .clickable(
                                onClick = onDismiss,
                                indication = ripple(bounded = true),
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            .semantics {
                                this.contentDescription = "Dismiss toast"
                                this.role = Role.Button
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "×",
                            style = config.actionStyle(),
                            color = colors.close
                        )
                    }
                }
            }
        }
    }
}

/**
 * ToastHost - Container for displaying toast stack
 *
 * @param hostState State holder managing toast queue
 * @param position Screen position for toasts
 * @param modifier Modifier for the host container
 */
@Composable
fun ToastHost(
    hostState: PixaToastHostState,
    position: ToastPosition = ToastPosition.Bottom,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = getAlignment(position)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall), // 8.dp spacing between toasts
            horizontalAlignment = when (position) {
                ToastPosition.TopStart, ToastPosition.BottomStart -> Alignment.Start
                ToastPosition.TopEnd, ToastPosition.BottomEnd -> Alignment.End
                else -> Alignment.CenterHorizontally
            },
            modifier = Modifier.padding(
                top = if (position in listOf(ToastPosition.Top, ToastPosition.TopStart, ToastPosition.TopEnd))
                    Spacing.Large else Spacing.None, // 24.dp for status bar clearance
                bottom = if (position in listOf(ToastPosition.Bottom, ToastPosition.BottomStart, ToastPosition.BottomEnd))
                    Spacing.Large else Spacing.None, // 24.dp for navigation bar clearance
                start = if (position in listOf(ToastPosition.TopStart, ToastPosition.BottomStart))
                    Spacing.Medium else Spacing.None,
                end = if (position in listOf(ToastPosition.TopEnd, ToastPosition.BottomEnd))
                    Spacing.Medium else Spacing.None
            )
        ) {
            hostState.currentToasts.forEach { toast ->
                key(toast.id) {
                    AnimatedVisibility(
                        visible = true,
                        enter = getEnterTransition(position),
                        exit = getExitTransition(position)
                    ) {
                        Toast(
                            data = toast,
                            onDismiss = {
                                coroutineScope.launch {
                                    hostState.dismissToast(toast.id)
                                }
                            }
                        )
                    }
                }
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
 * 1. Basic toast host setup:
 * ```
 * @Composable
 * fun MyScreen() {
 *     val toastState = rememberToastHostState()
 *
 *     Box(modifier = Modifier.fillMaxSize()) {
 *         // Your content here
 *
 *         ToastHost(
 *             hostState = toastState,
 *             position = ToastPosition.Bottom
 *         )
 *     }
 * }
 * ```
 *
 * 2. Show simple toast:
 * ```
 * val scope = rememberCoroutineScope()
 * Button(onClick = {
 *     scope.launch {
 *         toastState.showToast(
 *             message = "Settings saved successfully",
 *             variant = ToastVariant.Success
 *         )
 *     }
 * })
 * ```
 *
 * 3. Show toast with action:
 * ```
 * scope.launch {
 *     toastState.showToast(
 *         message = "File deleted",
 *         variant = ToastVariant.Info,
 *         actionText = "Undo",
 *         onAction = { undoDelete() }
 *     )
 * }
 * ```
 *
 * 4. Show error toast with longer duration:
 * ```
 * scope.launch {
 *     toastState.showErrorToast(
 *         message = "Failed to upload file. Please try again.",
 *         duration = ToastDuration.Long,
 *         actionText = "Retry",
 *         onAction = { retryUpload() }
 *     )
 * }
 * ```
 *
 * 5. Show unlimited duration toast (manual dismiss):
 * ```
 * scope.launch {
 *     toastState.showToast(
 *         message = "New message received",
 *         variant = ToastVariant.Info,
 *         duration = ToastDuration.Unlimited,
 *         dismissible = true
 *     )
 * }
 * ```
 *
 * 6. Toast with custom position:
 * ```
 * ToastHost(
 *     hostState = toastState,
 *     position = ToastPosition.TopEnd
 * )
 * ```
 *
 * 7. Success toast convenience method:
 * ```
 * scope.launch {
 *     toastState.showSuccessToast("Profile updated!")
 * }
 * ```
 *
 * 8. Warning toast with action:
 * ```
 * scope.launch {
 *     toastState.showWarningToast(
 *         message = "Storage space running low",
 *         actionText = "Manage",
 *         onAction = { navigateToStorage() }
 *     )
 * }
 * ```
 *
 * 9. Custom styled toast:
 * ```
 * scope.launch {
 *     toastState.showToast(
 *         message = "Update available",
 *         variant = ToastVariant.Info,
 *         style = ToastStyle.Outlined,
 *         duration = ToastDuration.Long,
 *         actionText = "Update",
 *         onAction = { startUpdate() }
 *     )
 * }
 * ```
 *
 * 10. Toast with custom colors:
 * ```
 * scope.launch {
 *     toastState.showToast(
 *         message = "Premium feature unlocked!",
 *         variant = ToastVariant.Success,
 *         customColors = ToastColors(
 *             background = Color(0xFFFFD700),
 *             border = Color.Transparent,
 *             icon = Color(0xFF000000),
 *             message = Color(0xFF000000),
 *             action = Color(0xFF000000),
 *             close = Color(0xFF000000)
 *         )
 *     )
 * }
 * ```
 *
 * 11. Multiple toasts with stacking:
 * ```
 * // Toasts will stack up to the limit (default 3)
 * scope.launch {
 *     toastState.showInfoToast("First message")
 *     delay(500)
 *     toastState.showSuccessToast("Second message")
 *     delay(500)
 *     toastState.showWarningToast("Third message")
 *     // Fourth toast will remove the oldest one
 * }
 * ```
 *
 * 12. Custom max toasts limit:
 * ```
 * val toastState = rememberToastHostState(maxToasts = 5)
 * ```
 *
 * 13. Dismiss all toasts:
 * ```
 * scope.launch {
 *     toastState.dismissAll()
 * }
 * ```
 *
 * 14. Toast with custom icon:
 * ```
 * scope.launch {
 *     toastState.showToast(
 *         message = "Download complete",
 *         variant = ToastVariant.Success,
 *         icon = painterResource(R.drawable.ic_download),
 *         showIcon = true
 *     )
 * }
 * ```
 *
 * 15. Center positioned toast (dialog-like):
 * ```
 * ToastHost(
 *     hostState = toastState,
 *     position = ToastPosition.Center
 * )
 * ```
 */
