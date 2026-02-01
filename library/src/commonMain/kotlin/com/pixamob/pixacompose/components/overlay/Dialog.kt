package com.pixamob.pixacompose.components.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.IconSize
import com.pixamob.pixacompose.theme.RadiusSize
import com.pixamob.pixacompose.theme.SizeVariant

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class DialogVariant {
    Default,
    Info,
    Success,
    Warning,
    Error
}

enum class DialogSize {
    Small,
    Medium,
    Large
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class DialogColors(
    val background: Color,
    val scrim: Color,
    val title: Color,
    val message: Color,
    val icon: Color,
    val border: Color
)

@Immutable
@Stable
data class DialogSizeConfig(
    val minWidth: Dp,
    val maxWidth: Dp,
    val padding: Dp,
    val iconSize: Dp,
    val titleStyle: TextStyle,
    val messageStyle: TextStyle,
    val cornerRadius: Dp,
    val elevation: Dp
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getDialogSizeConfig(size: DialogSize): DialogSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        DialogSize.Small -> DialogSizeConfig(
            minWidth = 240.dp,
            maxWidth = 300.dp,
            padding = HierarchicalSize.Spacing.Medium,
            iconSize = IconSize.Medium,
            titleStyle = typography.bodyBold,
            messageStyle = typography.captionBold,
            cornerRadius = RadiusSize.Medium,
            elevation = HierarchicalSize.Shadow.Large
        )
        DialogSize.Medium -> DialogSizeConfig(
            minWidth = 280.dp,
            maxWidth = 360.dp,
            padding = HierarchicalSize.Spacing.Large,
            iconSize = IconSize.Large,
            titleStyle = typography.subtitleBold,
            messageStyle = typography.bodyRegular,
            cornerRadius = RadiusSize.Large,
            elevation = HierarchicalSize.Shadow.Huge
        )
        DialogSize.Large -> DialogSizeConfig(
            minWidth = 320.dp,
            maxWidth = 420.dp,
            padding = HierarchicalSize.Spacing.Huge,
            iconSize = IconSize.Huge,
            titleStyle = typography.titleBold,
            messageStyle = typography.bodyBold,
            cornerRadius = RadiusSize.ExtraLarge,
            elevation = HierarchicalSize.Shadow.Massive
        )
    }
}

@Composable
private fun getDialogTheme(variant: DialogVariant): DialogColors {
    val colors = AppTheme.colors
    return when (variant) {
        DialogVariant.Default -> DialogColors(
            background = colors.baseSurfaceDefault,
            scrim = Color.Black.copy(alpha = 0.5f),
            title = colors.baseContentTitle,
            message = colors.baseContentBody,
            icon = colors.brandContentDefault,
            border = colors.baseBorderSubtle
        )
        DialogVariant.Info -> DialogColors(
            background = colors.baseSurfaceDefault,
            scrim = Color.Black.copy(alpha = 0.5f),
            title = colors.baseContentTitle,
            message = colors.baseContentBody,
            icon = colors.infoContentDefault,
            border = colors.infoBorderDefault
        )
        DialogVariant.Success -> DialogColors(
            background = colors.baseSurfaceDefault,
            scrim = Color.Black.copy(alpha = 0.5f),
            title = colors.baseContentTitle,
            message = colors.baseContentBody,
            icon = colors.successContentDefault,
            border = colors.successBorderDefault
        )
        DialogVariant.Warning -> DialogColors(
            background = colors.baseSurfaceDefault,
            scrim = Color.Black.copy(alpha = 0.5f),
            title = colors.baseContentTitle,
            message = colors.baseContentBody,
            icon = colors.warningContentDefault,
            border = colors.warningBorderDefault
        )
        DialogVariant.Error -> DialogColors(
            background = colors.baseSurfaceDefault,
            scrim = Color.Black.copy(alpha = 0.5f),
            title = colors.baseContentTitle,
            message = colors.baseContentBody,
            icon = colors.errorContentDefault,
            border = colors.errorBorderDefault
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaDialog - Modal dialog for important interactions
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Simple confirmation dialog
 * var showDialog by remember { mutableStateOf(false) }
 * if (showDialog) {
 *     PixaDialog(
 *         onDismissRequest = { showDialog = false },
 *         title = "Confirm Action",
 *         message = "Are you sure?",
 *         confirmText = "Yes",
 *         dismissText = "No",
 *         onConfirm = { performAction() }
 *     )
 * }
 *
 * // Error dialog with icon
 * PixaDialog(
 *     onDismissRequest = { showError = false },
 *     variant = DialogVariant.Error,
 *     icon = painterResource(Res.drawable.ic_error),
 *     title = "Error",
 *     message = "Something went wrong.",
 *     confirmText = "Retry"
 * )
 * ```
 *
 * @param onDismissRequest Callback when dismissed
 * @param modifier Modifier
 * @param variant Visual style variant
 * @param size Size preset
 * @param colors Custom colors
 * @param icon Optional icon
 * @param title Title text
 * @param message Message text
 * @param confirmText Primary button text
 * @param dismissText Secondary button text
 * @param onConfirm Primary action callback
 * @param onDismiss Secondary action callback
 * @param dismissOnOutsideClick Dismiss on outside click
 * @param dismissOnBackClick Dismiss on back press
 * @param content Custom content
 */
@Composable
fun PixaDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    variant: DialogVariant = DialogVariant.Default,
    size: DialogSize = DialogSize.Medium,
    colors: DialogColors? = null,
    icon: Painter? = null,
    title: String? = null,
    message: String? = null,
    confirmText: String? = null,
    dismissText: String? = null,
    onConfirm: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    dismissOnOutsideClick: Boolean = true,
    dismissOnBackClick: Boolean = true,
    content: @Composable (() -> Unit)? = null
) {
    val sizeConfig = getDialogSizeConfig(size)
    val themeColors = colors ?: getDialogTheme(variant)

    Dialog(
        onDismissRequest = { if (dismissOnOutsideClick || dismissOnBackClick) onDismissRequest() },
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackClick,
            dismissOnClickOutside = dismissOnOutsideClick
        )
    ) {
        Box(
            modifier = modifier
                .widthIn(min = sizeConfig.minWidth, max = sizeConfig.maxWidth)
                .shadow(sizeConfig.elevation, RoundedCornerShape(sizeConfig.cornerRadius))
                .clip(RoundedCornerShape(sizeConfig.cornerRadius))
                .background(themeColors.background)
                .padding(sizeConfig.padding)
                .semantics { contentDescription = title ?: "Dialog" }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (icon != null) {
                    PixaIcon(painter = icon, contentDescription = null, tint = themeColors.icon, modifier = Modifier.size(sizeConfig.iconSize))
                    Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))
                }
                if (title != null) {
                    Text(text = title, style = sizeConfig.titleStyle, color = themeColors.title, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                }
                if (message != null) {
                    Text(text = message, style = sizeConfig.messageStyle, color = themeColors.message, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))
                }
                content?.invoke()
                if (confirmText != null || dismissText != null) {
                    Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small, Alignment.End)) {
                        if (dismissText != null) {
                            PixaButton(text = dismissText, onClick = { onDismiss?.invoke() ?: onDismissRequest() }, variant = ButtonVariant.Ghost, size = SizeVariant.Medium)
                        }
                        if (confirmText != null) {
                            PixaButton(text = confirmText, onClick = { onConfirm?.invoke(); onDismissRequest() }, variant = ButtonVariant.Solid, isDestructive = variant == DialogVariant.Error, size = SizeVariant.Medium)
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

@Composable
fun PixaAlertDialog(onDismissRequest: () -> Unit, title: String, message: String, confirmText: String = "OK", variant: DialogVariant = DialogVariant.Default, icon: Painter? = null) {
    PixaDialog(onDismissRequest = onDismissRequest, variant = variant, icon = icon, title = title, message = message, confirmText = confirmText, onConfirm = onDismissRequest)
}

@Composable
fun PixaConfirmDialog(onDismissRequest: () -> Unit, title: String, message: String, confirmText: String = "Confirm", dismissText: String = "Cancel", onConfirm: () -> Unit, variant: DialogVariant = DialogVariant.Default, icon: Painter? = null) {
    PixaDialog(onDismissRequest = onDismissRequest, variant = variant, icon = icon, title = title, message = message, confirmText = confirmText, dismissText = dismissText, onConfirm = onConfirm, onDismiss = onDismissRequest)
}

@Composable
fun PixaDestructiveDialog(onDismissRequest: () -> Unit, title: String, message: String, confirmText: String = "Delete", dismissText: String = "Cancel", onConfirm: () -> Unit, icon: Painter? = null) {
    PixaDialog(onDismissRequest = onDismissRequest, variant = DialogVariant.Error, icon = icon, title = title, message = message, confirmText = confirmText, dismissText = dismissText, onConfirm = onConfirm, onDismiss = onDismissRequest)
}
