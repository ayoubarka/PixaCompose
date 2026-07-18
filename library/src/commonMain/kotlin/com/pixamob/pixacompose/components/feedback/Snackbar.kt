package com.pixamob.pixacompose.components.feedback

/**
 * PixaSnackbar — ephemeral, passive acknowledgment after a user-initiated
 * action (e.g. "Item deleted"). Never blocks the workflow. Not for global
 * system messaging, promotions, or critical/emergency messages (use
 * [PixaAlert] for colored feedback surfaces).
 *
 * ### Anatomy
 * Container + message (required) + optional leading content (icon 24dp,
 * loading spinner 24dp, or photo 48dp — mutually exclusive) + optional
 * single trailing action (text button or icon dismiss — never both).
 *
 * ### Variants
 * [SnackbarVariant] is a **semantic/accessibility axis only** — color is
 * fixed gray across all variants. Use [PixaAlert] for colored feedback.
 *
 * ### States
 * shown → (optionally auto-dismisses) → dismissed, driven by
 * [PixaSnackbarHostState]'s single-item queue.
 *
 * ### Sizing
 * Narrow screens: full width minus 8dp gutters. Wide screens: 320-540dp.
 * [SizeVariant] scales padding/radius/type only.
 *
 * ### Adaptive behavior
 * Width breakpoint via [WindowSizeClass] (Compact < 600dp).
 *
 * ### Behavior
 * - **Single visible snackbar** — [PixaSnackbarHostState] queues subsequent
 *   calls and shows them in order.
 * - Dismissed by: swipe (mobile), trailing action tap, or timeout.
 * - **Progress pattern**: show `Loading`, then `Success`/`Error` once the
 *   action completes (two sequential snackbars, not one mutating instance).
 */

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaAvatar
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.utils.QuinticEaseOutEasing
import com.pixamob.pixacompose.utils.ScreenUtil
import com.pixamob.pixacompose.utils.WindowSizeClass
import com.pixamob.pixacompose.utils.elevationShadow
import com.pixamob.pixacompose.utils.pixaRipple
import com.pixamob.pixacompose.utils.windowSizeClassOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.abs

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Semantic/accessibility axis only — does **not** change container color.
 * Only the accessibility label and (for [Loading]) the auto-rendered spinner.
 */
enum class SnackbarVariant {
    Default,
    Info,
    Success,
    Warning,
    Error,

    /** Spec's "Loading" common state — auto-renders a 24dp indeterminate spinner. */
    Loading
}

enum class SnackbarDuration(val milliseconds: Long) {
    /**
     * Duration derived from rendered message line count (1→3000ms, 2→5000ms,
     * 3+→7000ms). Resolved dynamically via text layout.
     */
    Auto(-2L),
    Short(4000L),
    Long(10000L),
    Indefinite(-1L)
}

/**
 * Where the snackbar appears. [Top] is default. The remaining five are
 * positioning overrides — use sparingly on mobile.
 */
enum class SnackbarPosition {
    Top,
    Bottom,
    TopStart,
    TopEnd,
    BottomStart,
    BottomEnd
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

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
    val padding: Dp,
    val actionSpacing: Dp,
    val minHeight: Dp = HierarchicalSize.Container.Medium,
    val elevation: ComponentElevation = ComponentElevation.Medium,
    val cornerRadius: Dp,
    val swipeToDismissThreshold: Float = 0.3f,
    /** Leading icon/spinner fixed at 24dp regardless of [SizeVariant]. */
    val leadingIconSize: Dp = HierarchicalSize.Icon.Medium,
    /** Leading photo fixed at 48dp regardless of [SizeVariant]. */
    val leadingPhotoSize: SizeVariant = SizeVariant.Medium,
    /** Border fixed at 1dp regardless of [SizeVariant]. */
    val borderWidth: Dp = HierarchicalSize.Border.Compact
)

/**
 * Data class representing a snackbar item.
 *
 * Leading content is mutually exclusive: [SnackbarVariant.Loading] spinner
 * → [photoUrl]/[photoModel] → [icon] (never combined).
 */
@Stable
data class SnackbarData(
    val id: Long = kotlin.random.Random.nextLong(),
    val message: String,
    val actionLabel: String? = null,
    val variant: SnackbarVariant = SnackbarVariant.Default,
    val duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Auto else SnackbarDuration.Indefinite,
    val withDismissAction: Boolean = false,
    val icon: Painter? = null,
    val showIcon: Boolean = false,
    val photoUrl: String? = null,
    val photoModel: Any? = null,
    val onAction: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null
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

// ════════════════════════════════════════════════════════════════════════════
// SNACKBAR HOST STATE
// ════════════════════════════════════════════════════════════════════════════

/**
 * State holder managing snackbar queue and display.
 * Only one snackbar at a time; subsequent calls are queued.
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
     * @return Result indicating action performed or dismissed
     */
    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        variant: SnackbarVariant = SnackbarVariant.Default,
        duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Auto else SnackbarDuration.Indefinite,
        withDismissAction: Boolean = false,
        icon: Painter? = null,
        showIcon: Boolean = false,
        photoUrl: String? = null,
        photoModel: Any? = null,
        onAction: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null
    ): SnackbarResult = mutex.withLock {
        val snackbar = SnackbarData(
            message = message,
            actionLabel = actionLabel,
            variant = variant,
            duration = duration,
            withDismissAction = withDismissAction,
            icon = icon,
            showIcon = showIcon,
            photoUrl = photoUrl,
            photoModel = photoModel,
            onAction = onAction,
            onDismiss = onDismiss
        )

        // If there's a current snackbar, queue this one
        if (_currentSnackbar.value != null) {
            _snackbarQueue.add(snackbar)
            return SnackbarResult.Dismissed
        }

        // Show the snackbar
        _currentSnackbar.value = snackbar

        // Auto-dismiss if duration is not indefinite/auto (Auto is resolved by
        // the rendered Snackbar composable itself, which measures line count).
        if (duration != SnackbarDuration.Indefinite && duration != SnackbarDuration.Auto) {
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

                if (next.duration != SnackbarDuration.Indefinite && next.duration != SnackbarDuration.Auto) {
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
        duration: SnackbarDuration = SnackbarDuration.Auto,
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
        duration: SnackbarDuration = SnackbarDuration.Auto,
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

    /**
     * Show a loading snackbar. Always indefinite — dismiss explicitly or
     * replace with a follow-up snackbar once the action finishes.
     */
    suspend fun showLoadingSnackbar(
        message: String,
        onDismiss: (() -> Unit)? = null
    ): SnackbarResult {
        return showSnackbar(
            message = message,
            variant = SnackbarVariant.Loading,
            duration = SnackbarDuration.Indefinite,
            onDismiss = onDismiss
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

// ════════════════════════════════════════════════════════════════════════════
// GLOBAL SNACKBAR MANAGER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Global singleton for showing snackbars from anywhere in the app.
 * Thread-safe and compatible with coroutines.
 *
 * Usage:
 * ```
 * // From ViewModel
 * viewModelScope.launch {
 *     PixaSnackbarManager.showSuccess("Data saved!")
 * }
 *
 * // From UseCase/Repository
 * suspend fun saveData() {
 *     PixaSnackbarManager.showError("Failed to save")
 * }
 *
 * // Quick launch from anywhere
 * PixaSnackbarManager.launch {
 *     showInfo("Operation completed")
 * }
 * ```
 */
object PixaSnackbarManager {
    private var _state: PixaSnackbarHostState? = null
    private val mutex = Mutex()

    /**
     * Internal: Initialize the global snackbar state
     * Called automatically by GlobalSnackbarHost
     */
    internal fun initialize(state: PixaSnackbarHostState) {
        _state = state
    }

    /**
     * Internal: Clear the global snackbar state
     */
    internal fun clear() {
        _state = null
    }

    /**
     * Internal: Get the current state (for composition local access)
     */
    internal fun getState(): PixaSnackbarHostState? = _state

    /**
     * Get the current snackbar state
     * @throws IllegalStateException if GlobalSnackbarHost is not initialized
     */
    private fun requireState(): PixaSnackbarHostState {
        return _state ?: error(
            "PixaSnackbarManager is not initialized. " +
            "Make sure to add GlobalSnackbarHost() at your app root composable."
        )
    }

    /**
     * Check if the snackbar manager is initialized
     */
    val isInitialized: Boolean
        get() = _state != null

    /**
     * Launch a coroutine in the appropriate scope for snackbar operations
     */
    fun launch(block: suspend PixaSnackbarHostState.() -> Unit) {
        @OptIn(DelicateCoroutinesApi::class)
        GlobalScope.launch(Dispatchers.Main) {
            mutex.withLock {
                requireState().block()
            }
        }
    }

    /**
     * Show a snackbar message
     */
    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        variant: SnackbarVariant = SnackbarVariant.Default,
        duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Auto else SnackbarDuration.Indefinite,
        withDismissAction: Boolean = false,
        icon: Painter? = null,
        showIcon: Boolean = false,
        photoUrl: String? = null,
        photoModel: Any? = null,
        onAction: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null
    ): SnackbarResult {
        return requireState().showSnackbar(
            message = message,
            actionLabel = actionLabel,
            variant = variant,
            duration = duration,
            withDismissAction = withDismissAction,
            icon = icon,
            showIcon = showIcon,
            photoUrl = photoUrl,
            photoModel = photoModel,
            onAction = onAction,
            onDismiss = onDismiss
        )
    }

    /**
     * Show success snackbar (convenience method)
     */
    suspend fun showSuccess(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Auto,
        onAction: (() -> Unit)? = null
    ): SnackbarResult {
        return requireState().showSuccessSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = duration,
            onAction = onAction
        )
    }

    /**
     * Show error snackbar (convenience method)
     */
    suspend fun showError(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Long,
        onAction: (() -> Unit)? = null
    ): SnackbarResult {
        return requireState().showErrorSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = duration,
            onAction = onAction
        )
    }

    /**
     * Show warning snackbar (convenience method)
     */
    suspend fun showWarning(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Long,
        onAction: (() -> Unit)? = null
    ): SnackbarResult {
        return requireState().showWarningSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = duration,
            onAction = onAction
        )
    }

    /**
     * Show info snackbar (convenience method)
     */
    suspend fun showInfo(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Auto,
        onAction: (() -> Unit)? = null
    ): SnackbarResult {
        return requireState().showInfoSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = duration,
            onAction = onAction
        )
    }

    /**
     * Show a loading snackbar (see [PixaSnackbarHostState.showLoadingSnackbar]).
     */
    suspend fun showLoading(
        message: String,
        onDismiss: (() -> Unit)? = null
    ): SnackbarResult {
        return requireState().showLoadingSnackbar(message, onDismiss)
    }

    /**
     * Show error snackbar from exception
     */
    suspend fun showErrorFromException(
        exception: Throwable,
        message: String? = null,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null
    ): SnackbarResult {
        val errorMessage = message ?: exception.message ?: "An error occurred"
        return showError(
            message = errorMessage,
            actionLabel = actionLabel,
            onAction = onAction
        )
    }

    /**
     * Dismiss current snackbar
     */
    suspend fun dismissCurrent() {
        requireState().dismissCurrentSnackbar()
    }
}

/**
 * CompositionLocal for accessing snackbar manager in composition tree
 * Provides an alternative to the singleton for better testability
 */
val LocalSnackbarManager = staticCompositionLocalOf<PixaSnackbarHostState?> { null }

/**
 * Get the current snackbar manager from composition
 * Falls back to global singleton if not provided locally
 */
@Composable
fun currentSnackbarManager(): PixaSnackbarHostState {
    return LocalSnackbarManager.current ?: run {
        PixaSnackbarManager.getState() ?: error(
            "Snackbar manager is not available. " +
            "Make sure to add GlobalSnackbarHost() at your app root or provide LocalSnackbarManager."
        )
    }
}

/**
 * GlobalSnackbarHost - Root-level composable for global snackbar management
 *
 * Add this once at your application root to enable global snackbar access.
 * It initializes the PixaSnackbarManager singleton and displays snackbars.
 *
 * @param modifier Modifier for the host container
 * @param position Where the snackbar appears (Default: [SnackbarPosition.Top])
 * @param size [SizeVariant] driving padding/radius/typography
 *
 * @sample
 * ```
 * @Composable
 * fun App() {
 *     AppTheme {
 *         Box(modifier = Modifier.fillMaxSize()) {
 *             GlobalSnackbarHost()  // Initialize once here
 *
 *             Scaffold {
 *                 // Your app content
 *             }
 *         }
 *     }
 * }
 * ```
 */
@Composable
fun GlobalSnackbarHost(
    modifier: Modifier = Modifier,
    position: SnackbarPosition = SnackbarPosition.Top,
    size: SizeVariant = SizeVariant.Medium
) {
    val hostState = remember { PixaSnackbarHostState() }

    // Initialize global manager
    DisposableEffect(hostState) {
        PixaSnackbarManager.initialize(hostState)
        onDispose {
            PixaSnackbarManager.clear()
        }
    }

    // Provide local composition access
    CompositionLocalProvider(LocalSnackbarManager provides hostState) {
        SnackbarHost(
            hostState = hostState,
            modifier = modifier,
            position = position,
            size = size
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// EXTENSION FUNCTIONS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Remember a coroutine scope and show a snackbar
 */
@Composable
fun rememberSnackbarScope(): SnackbarScope {
    val scope = rememberCoroutineScope()
    val manager = currentSnackbarManager()
    return remember(scope, manager) {
        SnackbarScope(scope, manager)
    }
}

/**
 * Snackbar scope for convenient snackbar operations in composables
 */
class SnackbarScope(
    private val scope: CoroutineScope,
    private val manager: PixaSnackbarHostState
) {
    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        variant: SnackbarVariant = SnackbarVariant.Default,
        duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Auto else SnackbarDuration.Indefinite,
        withDismissAction: Boolean = false,
        icon: Painter? = null,
        showIcon: Boolean = false,
        photoUrl: String? = null,
        photoModel: Any? = null,
        onAction: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null
    ) {
        scope.launch {
            manager.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                variant = variant,
                duration = duration,
                withDismissAction = withDismissAction,
                icon = icon,
                showIcon = showIcon,
                photoUrl = photoUrl,
                photoModel = photoModel,
                onAction = onAction,
                onDismiss = onDismiss
            )
        }
    }

    fun showSuccess(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Auto,
        onAction: (() -> Unit)? = null
    ) {
        scope.launch {
            manager.showSuccessSnackbar(message, actionLabel, duration, onAction)
        }
    }

    fun showError(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Long,
        onAction: (() -> Unit)? = null
    ) {
        scope.launch {
            manager.showErrorSnackbar(message, actionLabel, duration, onAction)
        }
    }

    fun showWarning(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Long,
        onAction: (() -> Unit)? = null
    ) {
        scope.launch {
            manager.showWarningSnackbar(message, actionLabel, duration, onAction)
        }
    }

    fun showInfo(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Auto,
        onAction: (() -> Unit)? = null
    ) {
        scope.launch {
            manager.showInfoSnackbar(message, actionLabel, duration, onAction)
        }
    }

    fun showLoading(
        message: String,
        onDismiss: (() -> Unit)? = null
    ) {
        scope.launch {
            manager.showLoadingSnackbar(message, onDismiss)
        }
    }

    fun showErrorFromException(
        exception: Throwable,
        message: String? = null,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null
    ) {
        scope.launch {
            val errorMessage = message ?: exception.message ?: "An error occurred"
            manager.showErrorSnackbar(errorMessage, actionLabel, SnackbarDuration.Long, onAction)
        }
    }

    fun dismissCurrent() {
        scope.launch {
            manager.dismissCurrentSnackbar()
        }
    }
}

/**
 * Extension function for launching snackbar from global manager
 * Useful for non-composable contexts
 */
fun launchSnackbar(block: suspend PixaSnackbarHostState.() -> Unit) {
    PixaSnackbarManager.launch(block)
}

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Snackbar colors — gray only, identical across all [SnackbarVariant].
 * Uses inverse-surface pairing (dark container/light text in light theme).
 */
@Composable
private fun getSnackbarColors(colors: ColorPalette): SnackbarColors {
    // Action color computed from inverse content color (same pattern as PixaTooltip).
    val message = colors.baseSurfaceDefault
    return SnackbarColors(
        background = colors.baseContentTitle,
        message = message,
        actionLabel = message,
        actionBackground = Color.Transparent
    )
}

/**
 * Get snackbar configuration
 */
@Composable
private fun getSnackbarConfig(size: SizeVariant): SnackbarConfig {
    val typography = AppTheme.typography
    val messageStyle = when (size) {
        SizeVariant.None, SizeVariant.Nano, SizeVariant.Compact -> typography.captionRegular
        SizeVariant.Small, SizeVariant.Medium -> typography.bodyRegular
        SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> typography.bodyBold
    }
    return SnackbarConfig(
        messageStyle = { messageStyle },
        actionStyle = { typography.bodyBold },
        padding = HierarchicalSize.Padding.forVariant(size),
        actionSpacing = HierarchicalSize.Spacing.forVariant(size),
        cornerRadius = HierarchicalSize.Radius.forVariant(size)
    )
}

/**
 * Resolves width breakpoint via [WindowSizeClass]: Compact (<600dp) = full
 * width minus 8dp gutters; wider = 320-540dp clamp.
 */
@Composable
private fun getSnackbarWidthModifier(): Modifier {
    val screenWidth = ScreenUtil.getScreenWidth()
    val windowSizeClass = windowSizeClassOf(screenWidth)
    return if (windowSizeClass == WindowSizeClass.Compact) {
        // Narrow: full width minus 8dp gutter each side.
        Modifier
            .fillMaxWidth()
            .padding(horizontal = HierarchicalSize.Spacing.Small)
    } else {
        // Wide: 320-540dp clamp.
        Modifier.widthIn(min = SnackbarWideMinWidth, max = SnackbarWideMaxWidth)
    }
}

/** Wide/desktop minimum width. */
private val SnackbarWideMinWidth = 320.dp

/** Wide/desktop maximum width. */
private val SnackbarWideMaxWidth = 540.dp

/** Auto duration: line count → display time. */
private fun autoDismissMsFor(lineCount: Int): Long = when {
    lineCount <= 1 -> 3000L
    lineCount == 2 -> 5000L
    else -> 7000L
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * Snackbar - Single snackbar message display
 *
 * @param data Snackbar data containing message and configuration
 * @param onDismiss Callback when snackbar is dismissed
 * @param onActionPerformed Callback when action is performed
 * @param modifier Modifier for the snackbar
 * @param size [SizeVariant] driving padding/radius/typography
 */
@Composable
internal fun Snackbar(
    data: SnackbarData,
    onDismiss: () -> Unit,
    onActionPerformed: () -> Unit,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium
) {
    val colors = getSnackbarColors(AppTheme.colors)
    val config = getSnackbarConfig(size)

    var offsetX by remember { mutableStateOf(0f) }
    val dismissThreshold = config.swipeToDismissThreshold

    val shape = RoundedCornerShape(config.cornerRadius)

    // Auto duration is derived from the rendered line count (post text-layout).
    var measuredLineCount by remember(data.id) { mutableStateOf(1) }
    LaunchedEffect(data.id, data.duration, measuredLineCount) {
        if (data.duration == SnackbarDuration.Auto) {
            delay(autoDismissMsFor(measuredLineCount))
            onDismiss()
        }
    }

    Box(
        modifier = modifier
            .then(getSnackbarWidthModifier())
            .heightIn(min = config.minHeight)
            .padding(horizontal = HierarchicalSize.Spacing.Medium, vertical = HierarchicalSize.Spacing.Small)
            .graphicsLayer {
                translationX = offsetX
                alpha = 1f - (abs(offsetX) / 1000f).coerceIn(0f, 0.5f)
            }
            .pointerInput(Unit) {
                // Qualified to avoid shadowing `size: SizeVariant`.
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (abs(offsetX) > this@pointerInput.size.width * dismissThreshold) {
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
                // Label = message text with no variant prefix.
                this.contentDescription = data.message
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .elevationShadow(elevation = config.elevation, shape = shape)
                .clip(shape)
                .background(colors.background)
                .border(config.borderWidth, colors.message.copy(alpha = 0.16f), shape)
                .padding(config.padding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(config.actionSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading content: spinner > photo > icon — mutually exclusive.
                when {
                    data.variant == SnackbarVariant.Loading -> {
                        PixaCircularIndicator(
                            sizePreset = SizeVariant.Large, // -> Icon.Medium = 24dp
                            variant = ProgressVariant.Neutral,
                            customColors = ProgressColors(
                                progress = colors.message,
                                track = colors.message.copy(alpha = 0.24f),
                                label = colors.message
                            ),
                            contentDescription = "Loading"
                        )
                    }

                    data.photoUrl != null || data.photoModel != null -> {
                        PixaAvatar(
                            size = config.leadingPhotoSize, // Medium = 48dp
                            imageUrl = data.photoUrl,
                            imageModel = data.photoModel
                        )
                    }

                    data.showIcon && data.icon != null -> {
                        PixaIcon(
                            painter = data.icon,
                            contentDescription = null,
                            tint = colors.message,
                            modifier = Modifier.size(config.leadingIconSize)
                        )
                    }
                }

                // No maxLines/overflow cap (truncation is an a11y concern);
                // onTextLayout measures line count for Auto duration.
                BasicText(
                    text = data.message,
                    style = config.messageStyle().copy(color = colors.message),
                    onTextLayout = { layoutResult -> measuredLineCount = layoutResult.lineCount },
                    modifier = Modifier.weight(1f)
                )

                // Trailing action — text action OR icon dismiss (never both).
                when {
                    data.actionLabel != null -> {
                        Box(
                            modifier = Modifier
                                .heightIn(min = HierarchicalSize.TouchTarget.Small)
                                .clip(RoundedCornerShape(HierarchicalSize.Radius.Small))
                                .clickable(
                                    onClick = onActionPerformed,
                                    indication = pixaRipple(bounded = true, color = colors.actionLabel.copy(alpha = 0.12f)),
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                                .padding(
                                    horizontal = HierarchicalSize.Spacing.Small,
                                    vertical = HierarchicalSize.Spacing.Compact
                                )
                                .semantics { this.role = Role.Button },
                            contentAlignment = Alignment.Center
                        ) {
                            BasicText(
                                text = data.actionLabel,
                                style = config.actionStyle().copy(color = colors.actionLabel)
                            )
                        }
                    }

                    data.withDismissAction -> {
                    // Dismiss glyph instead of PixaIconButton (no bundled icon assets).
                        Box(
                            modifier = Modifier
                                .size(HierarchicalSize.TouchTarget.Small)
                                .clip(RoundedCornerShape(HierarchicalSize.Radius.Small))
                                .clickable(
                                    onClick = onDismiss,
                                    indication = pixaRipple(bounded = true, color = colors.message.copy(alpha = 0.12f)),
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                                .semantics {
                                    this.contentDescription = "Dismiss"
                                    this.role = Role.Button
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            BasicText(
                                text = "×",
                                style = config.actionStyle().copy(color = colors.message)
                            )
                        }
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
 * @param position Where the snackbar appears (Default: [SnackbarPosition.Top])
 * @param size [SizeVariant] driving padding/radius/typography
 */
@Composable
fun SnackbarHost(
    hostState: PixaSnackbarHostState,
    modifier: Modifier = Modifier,
    position: SnackbarPosition = SnackbarPosition.Top,
    size: SizeVariant = SizeVariant.Medium
) {
    val coroutineScope = rememberCoroutineScope()
    val currentSnackbar = hostState.currentSnackbar

    val alignment = when (position) {
        SnackbarPosition.Top -> Alignment.TopCenter
        SnackbarPosition.Bottom -> Alignment.BottomCenter
        SnackbarPosition.TopStart -> Alignment.TopStart
        SnackbarPosition.TopEnd -> Alignment.TopEnd
        SnackbarPosition.BottomStart -> Alignment.BottomStart
        SnackbarPosition.BottomEnd -> Alignment.BottomEnd
    }
    val isTopEdge = position == SnackbarPosition.Top || position == SnackbarPosition.TopStart || position == SnackbarPosition.TopEnd
    // Top positions slide down; bottom positions slide up.
    val slideSign = if (isTopEdge) -1 else 1

    Box(
        modifier = modifier
            .fillMaxSize()
            // Approximates status-bar / safe-area margin; real insets are host-app concern.
            .padding(HierarchicalSize.Spacing.Medium),
        contentAlignment = alignment
    ) {
        AnimatedVisibility(
            visible = currentSnackbar != null,
            enter = slideInVertically(
                initialOffsetY = { it * slideSign },
                animationSpec = AnimationUtils.standardTween(durationMillis = SnackbarMotionDurationMs, easing = QuinticEaseOutEasing)
            ) + fadeIn(animationSpec = AnimationUtils.standardTween(durationMillis = SnackbarMotionDurationMs, easing = QuinticEaseOutEasing)),
            exit = slideOutVertically(
                targetOffsetY = { it * slideSign },
                animationSpec = AnimationUtils.standardTween(durationMillis = SnackbarMotionDurationMs, easing = QuinticEaseOutEasing)
            ) + fadeOut(animationSpec = AnimationUtils.standardTween(durationMillis = SnackbarMotionDurationMs, easing = QuinticEaseOutEasing))
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
                    },
                    size = size
                )
            }
        }
    }
}

/** Enter/exit transition duration (1000ms) shared by fade + slide. */
private const val SnackbarMotionDurationMs = 1000

// ════════════════════════════════════════════════════════════════════════════
// USAGE EXAMPLES
// ════════════════════════════════════════════════════════════════════════════

/**
 * USAGE EXAMPLES:
 *
 * ============================================================================
 * GLOBAL SNACKBAR SYSTEM (NEW - Recommended)
 * ============================================================================
 *
 * 1. Setup GlobalSnackbarHost at app root (ONE TIME):
 * ```
 * @Composable
 * fun App() {
 *     AppTheme {
 *         Box(modifier = Modifier.fillMaxSize()) {
 *             GlobalSnackbarHost()  // Initialize once here — top by default
 *
 *             Scaffold {
 *                 // Your navigation and content
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * 2. Show snackbar from ViewModel:
 * ```
 * class HabitViewModel : ViewModel() {
 *     fun deleteHabit(habitId: String) {
 *         viewModelScope.launch {
 *             try {
 *                 repository.deleteHabit(habitId)
 *                 PixaSnackbarManager.showSuccess(
 *                     message = "Habit deleted",
 *                     actionLabel = "Undo",
 *                     onAction = { restoreHabit(habitId) }
 *                 )
 *             } catch (e: Exception) {
 *                 PixaSnackbarManager.showError("Failed to delete habit")
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * 3. Progress pattern — loading snackbar followed by a confirmation snackbar:
 * ```
 * viewModelScope.launch {
 *     PixaSnackbarManager.showLoading("Uploading photo")
 *     try {
 *         upload()
 *         PixaSnackbarManager.showSuccess("Photo uploaded")
 *     } catch (e: Exception) {
 *         PixaSnackbarManager.showError("Upload failed", actionLabel = "Retry")
 *     }
 * }
 * ```
 *
 * 4. Show snackbar from Composable (using rememberSnackbarScope):
 * ```
 * @Composable
 * fun MyScreen() {
 *     val snackbarScope = rememberSnackbarScope()
 *
 *     PixaButton(text = "Save Settings", onClick = {
 *         snackbarScope.showSuccess(message = "Settings saved")
 *     })
 * }
 * ```
 *
 * 5. Indefinite snackbar with a single trailing action (until action/dismiss):
 * ```
 * viewModelScope.launch {
 *     PixaSnackbarManager.showSnackbar(
 *         message = "No internet connection",
 *         actionLabel = "Retry",
 *         duration = SnackbarDuration.Indefinite,
 *         onAction = { checkConnection() }
 *     )
 * }
 * ```
 *
 * 6. Snackbar with a 48dp photo leading slot:
 * ```
 * scope.launch {
 *     snackbarState.showSnackbar(
 *         message = "Message sent to Alex",
 *         photoUrl = "https://example.com/avatar.jpg"
 *     )
 * }
 * ```
 *
 * 7. Desktop-web positioning override:
 * ```
 * GlobalSnackbarHost(position = SnackbarPosition.BottomEnd)
 * ```
 */

