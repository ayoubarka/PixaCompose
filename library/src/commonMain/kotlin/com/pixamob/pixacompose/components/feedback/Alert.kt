package com.pixamob.pixacompose.components.feedback

import androidx.compose.animation.AnimatedVisibility
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
import com.pixamob.pixacompose.components.display.BaseCard
import com.pixamob.pixacompose.components.display.BaseCardElevation
import com.pixamob.pixacompose.components.display.BaseCardPadding
import com.pixamob.pixacompose.components.display.BaseCardVariant
import com.pixamob.pixacompose.components.display.Icon
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils
import kotlinx.coroutines.delay

/**
 * Alert Component
 *
 * Important inline messages with semantic colors (info, success, warning, error).
 * Built on top of Card component with integrated icons, actions, and dismissal.
 *
 * Features:
 * - Four semantic variants: Info, Success, Warning, Error
 * - Multiple styles: Filled, Outlined, Subtle
 * - Optional icon (default icons provided per variant)
 * - Dismissible with close button
 * - Action button support
 * - Card-based layout with proper spacing
 * - Full accessibility support
 * - Animated entrance and dismissal
 *
 * @sample
 * ```
 * // Simple info alert
 * Alert(
 *     title = "New Feature Available",
 *     message = "Check out our latest updates in the settings menu.",
 *     variant = AlertVariant.Info
 * )
 *
 * // Dismissible warning alert
 * Alert(
 *     title = "Storage Almost Full",
 *     message = "You're running low on storage space.",
 *     variant = AlertVariant.Warning,
 *     dismissible = true,
 *     onDismiss = { /* handle dismiss */ }
 * )
 *
 * // Error alert with action
 * Alert(
 *     title = "Connection Failed",
 *     message = "Unable to connect to server. Please try again.",
 *     variant = AlertVariant.Error,
 *     actionText = "Retry",
 *     onAction = { /* retry connection */ }
 * )
 * ```
 */

// ============================================================================
// CONFIGURATION
// ============================================================================

/**
 * Alert semantic variants
 */
enum class AlertVariant {
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
 * Alert visual style
 */
enum class AlertStyle {
    /** Filled background with semantic color */
    Filled,
    /** Outlined border with white/transparent background */
    Outlined,
    /** Subtle background tint */
    Subtle
}

/**
 * Alert colors for different states
 */
@Immutable
@Stable
data class AlertColors(
    val background: Color,
    val border: Color,
    val icon: Color,
    val title: Color,
    val message: Color,
    val action: Color,
    val close: Color
)

/**
 * Alert configuration
 */
@Immutable
@Stable
data class AlertConfig(
    val iconSize: Dp = 20.dp,
    val minTouchTarget: Dp = 44.dp,
    val titleStyle: @Composable () -> TextStyle,
    val messageStyle: @Composable () -> TextStyle,
    val actionStyle: @Composable () -> TextStyle,
    val spacing: Dp = Spacing.Small,
    val padding: Dp = Spacing.Medium,
    val maxTitleLines: Int = 2,
    val maxMessageLines: Int = 4
)

// ============================================================================
// THEME PROVIDER
// ============================================================================

/**
 * Get alert colors based on variant and style
 */
@Composable
private fun getAlertColors(
    variant: AlertVariant,
    style: AlertStyle,
    colors: ColorPalette
): AlertColors {
    return when (variant) {
        AlertVariant.Info -> when (style) {
            AlertStyle.Filled -> AlertColors(
                background = colors.infoSurfaceDefault,
                border = colors.infoBorderDefault,
                icon = colors.infoContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.infoContentDefault,
                close = colors.baseContentBody
            )
            AlertStyle.Outlined -> AlertColors(
                background = colors.baseSurfaceDefault,
                border = colors.infoBorderDefault,
                icon = colors.infoContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.infoContentDefault,
                close = colors.baseContentBody
            )
            AlertStyle.Subtle -> AlertColors(
                background = colors.infoSurfaceSubtle,
                border = Color.Transparent,
                icon = colors.infoContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.infoContentDefault,
                close = colors.baseContentBody
            )
        }
        AlertVariant.Success -> when (style) {
            AlertStyle.Filled -> AlertColors(
                background = colors.successSurfaceDefault,
                border = colors.successBorderDefault,
                icon = colors.successContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.successContentDefault,
                close = colors.baseContentBody
            )
            AlertStyle.Outlined -> AlertColors(
                background = colors.baseSurfaceDefault,
                border = colors.successBorderDefault,
                icon = colors.successContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.successContentDefault,
                close = colors.baseContentBody
            )
            AlertStyle.Subtle -> AlertColors(
                background = colors.successSurfaceSubtle,
                border = Color.Transparent,
                icon = colors.successContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.successContentDefault,
                close = colors.baseContentBody
            )
        }
        AlertVariant.Warning -> when (style) {
            AlertStyle.Filled -> AlertColors(
                background = colors.warningSurfaceDefault,
                border = colors.warningBorderDefault,
                icon = colors.warningContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.warningContentDefault,
                close = colors.baseContentBody
            )
            AlertStyle.Outlined -> AlertColors(
                background = colors.baseSurfaceDefault,
                border = colors.warningBorderDefault,
                icon = colors.warningContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.warningContentDefault,
                close = colors.baseContentBody
            )
            AlertStyle.Subtle -> AlertColors(
                background = colors.warningSurfaceSubtle,
                border = Color.Transparent,
                icon = colors.warningContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.warningContentDefault,
                close = colors.baseContentBody
            )
        }
        AlertVariant.Error -> when (style) {
            AlertStyle.Filled -> AlertColors(
                background = colors.errorSurfaceDefault,
                border = colors.errorBorderDefault,
                icon = colors.errorContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.errorContentDefault,
                close = colors.baseContentBody
            )
            AlertStyle.Outlined -> AlertColors(
                background = colors.baseSurfaceDefault,
                border = colors.errorBorderDefault,
                icon = colors.errorContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.errorContentDefault,
                close = colors.baseContentBody
            )
            AlertStyle.Subtle -> AlertColors(
                background = colors.errorSurfaceSubtle,
                border = Color.Transparent,
                icon = colors.errorContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.errorContentDefault,
                close = colors.baseContentBody
            )
        }
    }
}

/**
 * Get alert configuration
 */
@Composable
private fun getAlertConfig(): AlertConfig {
    val typography = AppTheme.typography
    return AlertConfig(
        titleStyle = { typography.subtitleBold },
        messageStyle = { typography.bodyRegular },
        actionStyle = { typography.bodyBold }
    )
}

// ============================================================================
// PUBLIC API
// ============================================================================

/**
 * Alert - Inline message component with semantic colors
 *
 * @param title Alert title (required)
 * @param message Alert message/description
 * @param variant Semantic variant (Info, Success, Warning, Error)
 * @param modifier Modifier for the alert
 * @param style Visual style (Filled, Outlined, Subtle)
 * @param icon Optional custom icon (default icon provided per variant)
 * @param showIcon Whether to show the icon
 * @param dismissible Whether the alert can be dismissed
 * @param onDismiss Callback when alert is dismissed
 * @param autoDismissMillis Auto-dismiss duration in milliseconds (null for no auto-dismiss)
 * @param onClick Optional callback for clicking the whole alert
 * @param actions Composable lambda for action buttons (supports multiple actions)
 * @param actionText Deprecated: Use actions parameter for better control
 * @param onAction Deprecated: Use actions parameter for better control
 * @param customColors Optional custom colors
 * @param contentDescription Accessibility description
 */
@Composable
fun Alert(
    title: String,
    message: String? = null,
    variant: AlertVariant = AlertVariant.Info,
    modifier: Modifier = Modifier,
    style: AlertStyle = AlertStyle.Subtle,
    icon: Painter? = null,
    showIcon: Boolean = true,
    dismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    autoDismissMillis: Long? = null,
    onClick: (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    customColors: AlertColors? = null,
    contentDescription: String? = null
) {
    val colors = customColors ?: getAlertColors(variant, style, AppTheme.colors)
    val config = getAlertConfig()

    var visible by remember { mutableStateOf(true) }

    // Auto-dismiss after specified duration
    LaunchedEffect(autoDismissMillis) {
        autoDismissMillis?.let { duration ->
            delay(duration)
            visible = false
            onDismiss?.invoke()
        }
    }

    val description = contentDescription ?: "${variant.name.lowercase()} alert: $title"
    val alertType = when (variant) {
        AlertVariant.Info -> "info"
        AlertVariant.Success -> "success"
        AlertVariant.Warning -> "warning"
        AlertVariant.Error -> "error"
    }

    AnimatedVisibility(
        visible = visible,
        enter = AnimationUtils.scaleInTransition + AnimationUtils.fadeInTransition,
        exit = AnimationUtils.scaleOutTransition + AnimationUtils.fadeOutTransition
    ) {
        // Wrap in a Box with border for outlined style since Card doesn't support custom borders
        Box(
            modifier = modifier
                .fillMaxWidth()
                .semantics {
                    this.contentDescription = description
                    this.role = Role.Button
                }
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            onClick = onClick,
                            indication = ripple(),
                            interactionSource = remember { MutableInteractionSource() }
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            BaseCard(
                modifier = Modifier.fillMaxWidth(),
                variant = when (style) {
                    AlertStyle.Outlined -> BaseCardVariant.Outlined
                    else -> BaseCardVariant.Filled
                },
                elevation = BaseCardElevation.None,
                padding = BaseCardPadding.None,
                backgroundColor = colors.background
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(config.padding),
                    horizontalArrangement = Arrangement.spacedBy(config.spacing),
                    verticalAlignment = Alignment.Top
                ) {
                    // Icon
                    if (showIcon) {
                        Box(
                            modifier = Modifier.size(config.iconSize),
                            contentAlignment = Alignment.Center
                        ) {
                            // TODO: Use default icons per variant if icon is null
                            icon?.let {
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
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(colors.icon)
                                )
                            }
                        }
                    }

                    // Content
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)
                    ) {
                        // Title
                        Text(
                            text = title,
                            style = config.titleStyle(),
                            color = colors.title,
                            maxLines = config.maxTitleLines,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Message
                        message?.let {
                            Text(
                                text = it,
                                style = config.messageStyle(),
                                color = colors.message,
                                maxLines = config.maxMessageLines,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Actions (multi-action support)
                        if (actions != null) {
                            Row(
                                modifier = Modifier.padding(top = Spacing.Tiny),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
                            ) {
                                actions()
                            }
                        } else if (actionText != null) {
                            // Legacy single action support
                            Text(
                                text = actionText,
                                style = config.actionStyle(),
                                color = colors.action,
                                modifier = Modifier
                                    .padding(top = Spacing.Tiny)
                                    .clickable(
                                        onClick = { onAction?.invoke() },
                                        indication = ripple(bounded = false),
                                        interactionSource = remember { MutableInteractionSource() }
                                    )
                                    .semantics {
                                        this.role = Role.Button
                                    }
                            )
                        }
                    }

                    // Close button
                    if (dismissible) {
                        Box(
                            modifier = Modifier
                                .size(config.minTouchTarget)
                                .clip(CircleShape)
                                .clickable(
                                    onClick = {
                                        visible = false
                                        onDismiss?.invoke()
                                    },
                                    indication = ripple(bounded = true),
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                                .semantics {
                                    this.contentDescription = "Dismiss alert"
                                    this.role = Role.Button
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // TODO: Use close icon
                            // For now, using X text
                            Text(
                                text = "×",
                                style = config.titleStyle(),
                                color = colors.close
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// CONVENIENCE VARIANTS
// ============================================================================

/**
 * Info Alert - Informational message
 */
@Composable
fun InfoAlert(
    title: String,
    message: String? = null,
    modifier: Modifier = Modifier,
    dismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    autoDismissMillis: Long? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Alert(
        title = title,
        message = message,
        variant = AlertVariant.Info,
        modifier = modifier,
        dismissible = dismissible,
        onDismiss = onDismiss,
        autoDismissMillis = autoDismissMillis,
        actions = actions,
        actionText = actionText,
        onAction = onAction
    )
}

/**
 * Success Alert - Success/positive message
 */
@Composable
fun SuccessAlert(
    title: String,
    message: String? = null,
    modifier: Modifier = Modifier,
    dismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    autoDismissMillis: Long? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Alert(
        title = title,
        message = message,
        variant = AlertVariant.Success,
        modifier = modifier,
        dismissible = dismissible,
        onDismiss = onDismiss,
        autoDismissMillis = autoDismissMillis,
        actions = actions,
        actionText = actionText,
        onAction = onAction
    )
}

/**
 * Warning Alert - Warning/caution message
 */
@Composable
fun WarningAlert(
    title: String,
    message: String? = null,
    modifier: Modifier = Modifier,
    dismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    autoDismissMillis: Long? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Alert(
        title = title,
        message = message,
        variant = AlertVariant.Warning,
        modifier = modifier,
        dismissible = dismissible,
        onDismiss = onDismiss,
        autoDismissMillis = autoDismissMillis,
        actions = actions,
        actionText = actionText,
        onAction = onAction
    )
}

/**
 * Error Alert - Error/critical message
 */
@Composable
fun ErrorAlert(
    title: String,
    message: String? = null,
    modifier: Modifier = Modifier,
    dismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    autoDismissMillis: Long? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Alert(
        title = title,
        message = message,
        variant = AlertVariant.Error,
        modifier = modifier,
        dismissible = dismissible,
        onDismiss = onDismiss,
        autoDismissMillis = autoDismissMillis,
        actions = actions,
        actionText = actionText,
        onAction = onAction
    )
}

// ============================================================================
// USAGE EXAMPLES
// ============================================================================

/**
 * USAGE EXAMPLES:
 *
 * 1. Simple info alert:
 * ```
 * InfoAlert(
 *     title = "New Feature Available",
 *     message = "Check out our latest updates in the settings menu."
 * )
 * ```
 *
 * 2. Dismissible warning:
 * ```
 * var showAlert by remember { mutableStateOf(true) }
 * if (showAlert) {
 *     WarningAlert(
 *         title = "Storage Almost Full",
 *         message = "You're running low on storage space. Consider removing unused files.",
 *         dismissible = true,
 *         onDismiss = { showAlert = false }
 *     )
 * }
 * ```
 *
 * 3. Error alert with retry action:
 * ```
 * ErrorAlert(
 *     title = "Connection Failed",
 *     message = "Unable to connect to the server. Please check your internet connection.",
 *     actionText = "Retry",
 *     onAction = { retryConnection() },
 *     dismissible = true
 * )
 * ```
 *
 * 4. Success alert with outlined style:
 * ```
 * SuccessAlert(
 *     title = "Upload Complete",
 *     message = "Your files have been successfully uploaded.",
 *     style = AlertStyle.Outlined,
 *     actionText = "View Files",
 *     onAction = { navigateToFiles() }
 * )
 * ```
 *
 * 5. Alert with multiple actions:
 * ```
 * Alert(
 *     title = "Update Available",
 *     message = "A new version is ready to install.",
 *     variant = AlertVariant.Info,
 *     actions = {
 *         Text(
 *             text = "Update Now",
 *             style = AppTheme.typography.bodyBold,
 *             color = AppTheme.colors.infoContentDefault,
 *             modifier = Modifier.clickable { installUpdate() }
 *         )
 *         Text(
 *             text = "Remind Later",
 *             style = AppTheme.typography.bodyRegular,
 *             color = AppTheme.colors.baseContentBody,
 *             modifier = Modifier.clickable { remindLater() }
 *         )
 *     },
 *     dismissible = true
 * )
 * ```
 *
 * 6. Auto-dismiss alert (toast-like):
 * ```
 * SuccessAlert(
 *     title = "Settings Saved",
 *     message = "Your preferences have been updated.",
 *     autoDismissMillis = 3000,
 *     onDismiss = { /* Handle dismiss */ }
 * )
 * ```
 *
 * 7. Clickable alert:
 * ```
 * InfoAlert(
 *     title = "System Maintenance",
 *     message = "Scheduled for tonight at 2 AM. Tap for details.",
 *     onClick = { showMaintenanceDetails() }
 * )
 * ```
 *
 * 8. Alert with custom colors:
 * ```
 * Alert(
 *     title = "Custom Alert",
 *     message = "This alert uses custom brand colors.",
 *     variant = AlertVariant.Info,
 *     customColors = AlertColors(
 *         background = customBrandColor,
 *         border = customBorderColor,
 *         icon = customIconColor,
 *         title = customTextColor,
 *         message = customTextColor,
 *         action = customActionColor,
 *         close = customTextColor
 *     )
 * )
 * ```
 *
 * 9. Multiple alerts in a list:
 * ```
 * Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
 *     InfoAlert(
 *         title = "Maintenance Scheduled",
 *         message = "System will be down tomorrow at 2 AM."
 *     )
 *     WarningAlert(
 *         title = "Password Expiring Soon",
 *         message = "Your password will expire in 3 days.",
 *         actionText = "Change Password",
 *         onAction = { navigateToPasswordChange() }
 *     )
 * }
 * ```
 *
 * 10. Alert with custom icon:
 * ```
 * Alert(
 *     title = "Premium Feature",
 *     message = "Upgrade to access this feature.",
 *     variant = AlertVariant.Info,
 *     icon = painterResource(R.drawable.ic_premium),
 *     actionText = "Upgrade Now",
 *     onAction = { navigateToUpgrade() }
 * )
 * ```
 */





