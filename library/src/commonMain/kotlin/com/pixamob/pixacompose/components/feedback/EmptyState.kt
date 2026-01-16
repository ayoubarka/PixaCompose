package com.pixamob.pixacompose.components.feedback

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.actions.ButtonSize
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.*

/**
 * EmptyState Component
 *
 * Display when there's no content to show (empty lists, search results, etc.)
 * or when errors occur. Groups errors by type to reduce error code complexity.
 *
 * Features:
 * - Multiple state types: Empty, Network errors, Client errors, Server errors, Permission errors
 * - Customizable icon, title, description, and action buttons
 * - Multiple size variants for different contexts
 * - Theme-aware colors and semantic styling
 * - Full accessibility support
 * - Primary and secondary action support
 *
 * Error Categories:
 * - **Empty**: No content, no results, empty lists
 * - **Network**: Connection issues, timeouts, offline
 * - **Client**: Bad request, not found, validation errors
 * - **Server**: Internal errors, service unavailable, maintenance
 * - **Permission**: Unauthorized, forbidden, authentication required
 *
 * @sample
 * ```
 * // Empty list
 * EmptyState(
 *     type = EmptyStateType.Empty.NoContent,
 *     title = "No messages yet",
 *     description = "Start a conversation to see messages here"
 * )
 *
 * // Network error with retry
 * EmptyState(
 *     type = EmptyStateType.Network.NoConnection,
 *     title = "No internet connection",
 *     description = "Check your network and try again",
 *     primaryActionText = "Retry",
 *     onPrimaryAction = { retry() }
 * )
 *
 * // Permission error
 * EmptyState(
 *     type = EmptyStateType.Permission.Unauthorized,
 *     title = "Login required",
 *     description = "Please sign in to access this content",
 *     primaryActionText = "Sign In",
 *     onPrimaryAction = { navigateToLogin() }
 * )
 * ```
 */

// ============================================================================
// CONFIGURATION
// ============================================================================

/**
 * Empty state types - Grouped by category to reduce complexity
 */
sealed class EmptyStateType {
    /**
     * Empty content states - No data available
     */
    sealed class Empty : EmptyStateType() {
        /** Generic empty state */
        object NoContent : Empty()
        /** Empty search results */
        object NoResults : Empty()
        /** Empty list/collection */
        object EmptyList : Empty()
        /** Empty inbox/messages */
        object EmptyInbox : Empty()
        /** Empty favorites/bookmarks */
        object EmptyFavorites : Empty()
        /** No items in cart */
        object EmptyCart : Empty()
    }

    /**
     * Network-related errors
     */
    sealed class Network : EmptyStateType() {
        /** No internet connection */
        object NoConnection : Network()
        /** Request timeout */
        object Timeout : Network()
        /** Offline mode */
        object Offline : Network()
        /** Slow connection */
        object SlowConnection : Network()
    }

    /**
     * Client-side errors (4xx)
     */
    sealed class Client : EmptyStateType() {
        /** Bad request (400) */
        object BadRequest : Client()
        /** Resource not found (404) */
        object NotFound : Client()
        /** Validation failed (422) */
        object ValidationError : Client()
        /** Rate limit exceeded (429) */
        object RateLimited : Client()
    }

    /**
     * Server-side errors (5xx)
     */
    sealed class Server : EmptyStateType() {
        /** Internal server error (500) */
        object InternalError : Server()
        /** Service unavailable (503) */
        object ServiceUnavailable : Server()
        /** Under maintenance */
        object Maintenance : Server()
        /** Gateway timeout (504) */
        object GatewayTimeout : Server()
    }

    /**
     * Permission/Authentication errors
     */
    sealed class Permission : EmptyStateType() {
        /** Not authenticated (401) */
        object Unauthorized : Permission()
        /** Insufficient permissions (403) */
        object Forbidden : Permission()
        /** Session expired */
        object SessionExpired : Permission()
        /** Account disabled/locked */
        object AccountLocked : Permission()
    }

    /**
     * Custom state with custom configuration
     */
    data class Custom(
        val category: StateCategory = StateCategory.Info
    ) : EmptyStateType()
}

/**
 * State category for styling
 */
enum class StateCategory {
    /** Informational state */
    Info,
    /** Warning state */
    Warning,
    /** Error state */
    Error,
    /** Success state (rare for empty states) */
    Success
}

/**
 * Empty state size variants
 */
enum class EmptyStateSize {
    /** Compact - 80dp icon, smaller spacing */
    Compact,
    /** Medium - 120dp icon, standard spacing */
    Medium,
    /** Large - 160dp icon, large spacing */
    Large
}

/**
 * Empty state colors
 */
@Immutable
@Stable
data class EmptyStateColors(
    val icon: Color,
    val title: Color,
    val description: Color,
    val background: Color = Color.Transparent
)

/**
 * Empty state configuration
 */
@Immutable
@Stable
data class EmptyStateConfig(
    val iconSize: Dp,
    val titleStyle: @Composable () -> TextStyle,
    val descriptionStyle: @Composable () -> TextStyle,
    val spacing: Dp,
    val contentSpacing: Dp,
    val maxWidth: Dp
)

// ============================================================================
// THEME PROVIDER
// ============================================================================

/**
 * Get empty state colors based on type
 */
@Composable
private fun getEmptyStateColors(
    type: EmptyStateType,
    colors: ColorPalette
): EmptyStateColors {
    return when (type) {
        is EmptyStateType.Empty -> EmptyStateColors(
            icon = colors.baseContentCaption,
            title = colors.baseContentTitle,
            description = colors.baseContentBody
        )
        is EmptyStateType.Network -> EmptyStateColors(
            icon = colors.warningContentDefault,
            title = colors.baseContentTitle,
            description = colors.baseContentBody
        )
        is EmptyStateType.Client -> EmptyStateColors(
            icon = colors.infoContentDefault,
            title = colors.baseContentTitle,
            description = colors.baseContentBody
        )
        is EmptyStateType.Server -> EmptyStateColors(
            icon = colors.errorContentDefault,
            title = colors.baseContentTitle,
            description = colors.baseContentBody
        )
        is EmptyStateType.Permission -> EmptyStateColors(
            icon = colors.warningContentDefault,
            title = colors.baseContentTitle,
            description = colors.baseContentBody
        )
        is EmptyStateType.Custom -> {
            val iconColor = when (type.category) {
                StateCategory.Info -> colors.infoContentDefault
                StateCategory.Warning -> colors.warningContentDefault
                StateCategory.Error -> colors.errorContentDefault
                StateCategory.Success -> colors.successContentDefault
            }
            EmptyStateColors(
                icon = iconColor,
                title = colors.baseContentTitle,
                description = colors.baseContentBody
            )
        }
    }
}

/**
 * Get empty state configuration based on size
 */
@Composable
private fun getEmptyStateConfig(size: EmptyStateSize): EmptyStateConfig {
    val typography = AppTheme.typography
    return when (size) {
        EmptyStateSize.Compact -> EmptyStateConfig(
            iconSize = IconSize.Massive, // 48dp
            titleStyle = { typography.bodyBold },
            descriptionStyle = { typography.captionRegular },
            spacing = Spacing.Small,
            contentSpacing = Spacing.ExtraSmall,
            maxWidth = ComponentSize.DialogMaxWidth // 560.dp
        )
        EmptyStateSize.Medium -> EmptyStateConfig(
            iconSize = ComponentSize.ImageSmall, // 80dp
            titleStyle = { typography.subtitleBold },
            descriptionStyle = { typography.bodyBold },
            spacing = Spacing.Medium,
            contentSpacing = Spacing.Small,
            maxWidth = ComponentSize.DialogMaxWidth
        )
        EmptyStateSize.Large -> EmptyStateConfig(
            iconSize = ComponentSize.ImageMedium, // 120dp
            titleStyle = { typography.titleBold },
            descriptionStyle = { typography.bodyRegular },
            spacing = Spacing.Large,
            contentSpacing = Spacing.Medium,
            maxWidth = ComponentSize.DialogMaxWidth
        )
    }
}

/**
 * Get default message for empty state type
 */
private fun getDefaultTitle(type: EmptyStateType): String {
    return when (type) {
        // Empty states
        is EmptyStateType.Empty.NoContent -> "No content"
        is EmptyStateType.Empty.NoResults -> "No results found"
        is EmptyStateType.Empty.EmptyList -> "Nothing here yet"
        is EmptyStateType.Empty.EmptyInbox -> "Inbox is empty"
        is EmptyStateType.Empty.EmptyFavorites -> "No favorites yet"
        is EmptyStateType.Empty.EmptyCart -> "Your cart is empty"

        // Network errors
        is EmptyStateType.Network.NoConnection -> "No internet connection"
        is EmptyStateType.Network.Timeout -> "Request timed out"
        is EmptyStateType.Network.Offline -> "You're offline"
        is EmptyStateType.Network.SlowConnection -> "Slow connection"

        // Client errors
        is EmptyStateType.Client.BadRequest -> "Invalid request"
        is EmptyStateType.Client.NotFound -> "Not found"
        is EmptyStateType.Client.ValidationError -> "Validation failed"
        is EmptyStateType.Client.RateLimited -> "Too many requests"

        // Server errors
        is EmptyStateType.Server.InternalError -> "Something went wrong"
        is EmptyStateType.Server.ServiceUnavailable -> "Service unavailable"
        is EmptyStateType.Server.Maintenance -> "Under maintenance"
        is EmptyStateType.Server.GatewayTimeout -> "Server timeout"

        // Permission errors
        is EmptyStateType.Permission.Unauthorized -> "Login required"
        is EmptyStateType.Permission.Forbidden -> "Access denied"
        is EmptyStateType.Permission.SessionExpired -> "Session expired"
        is EmptyStateType.Permission.AccountLocked -> "Account locked"

        // Custom
        is EmptyStateType.Custom -> "No content available"
    }
}

/**
 * Get default description for empty state type
 */
private fun getDefaultDescription(type: EmptyStateType): String {
    return when (type) {
        // Empty states
        is EmptyStateType.Empty.NoContent -> "There's nothing to display at the moment"
        is EmptyStateType.Empty.NoResults -> "Try adjusting your search or filters"
        is EmptyStateType.Empty.EmptyList -> "Items you add will appear here"
        is EmptyStateType.Empty.EmptyInbox -> "You're all caught up!"
        is EmptyStateType.Empty.EmptyFavorites -> "Start adding items to your favorites"
        is EmptyStateType.Empty.EmptyCart -> "Add items to get started"

        // Network errors
        is EmptyStateType.Network.NoConnection -> "Check your connection and try again"
        is EmptyStateType.Network.Timeout -> "The request took too long. Please try again"
        is EmptyStateType.Network.Offline -> "Connect to the internet to continue"
        is EmptyStateType.Network.SlowConnection -> "Your connection is slow. This may take a while"

        // Client errors
        is EmptyStateType.Client.BadRequest -> "The request couldn't be processed"
        is EmptyStateType.Client.NotFound -> "The requested resource doesn't exist"
        is EmptyStateType.Client.ValidationError -> "Please check your input and try again"
        is EmptyStateType.Client.RateLimited -> "You've made too many requests. Please wait"

        // Server errors
        is EmptyStateType.Server.InternalError -> "We're working on fixing this"
        is EmptyStateType.Server.ServiceUnavailable -> "The service is temporarily unavailable"
        is EmptyStateType.Server.Maintenance -> "We'll be back shortly"
        is EmptyStateType.Server.GatewayTimeout -> "The server took too long to respond"

        // Permission errors
        is EmptyStateType.Permission.Unauthorized -> "Sign in to access this content"
        is EmptyStateType.Permission.Forbidden -> "You don't have permission to view this"
        is EmptyStateType.Permission.SessionExpired -> "Please log in again to continue"
        is EmptyStateType.Permission.AccountLocked -> "Contact support to unlock your account"

        // Custom
        is EmptyStateType.Custom -> "Check back later for updates"
    }
}

// ============================================================================
// PUBLIC API
// ============================================================================

/**
 * EmptyState - Display when content is unavailable or errors occur
 *
 * @param type Type of empty state (determines default styling and messages)
 * @param modifier Modifier for the empty state
 * @param title Title text (null to use default based on type)
 * @param description Description text (null to use default based on type)
 * @param icon Custom icon painter (null for no icon)
 * @param showIcon Whether to show the icon
 * @param size Size variant for the empty state
 * @param primaryActionText Text for primary action button
 * @param onPrimaryAction Callback for primary action
 * @param secondaryActionText Text for secondary action button
 * @param onSecondaryAction Callback for secondary action
 * @param customColors Custom colors (null to use defaults based on type)
 * @param contentDescription Accessibility description
 */
@Composable
fun PixaEmptyState(
    type: EmptyStateType = EmptyStateType.Empty.NoContent,
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    icon: Painter? = null,
    showIcon: Boolean = true,
    size: EmptyStateSize = EmptyStateSize.Medium,
    primaryActionText: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    secondaryActionText: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    customColors: EmptyStateColors? = null,
    contentDescription: String? = null
) {
    val colors = customColors ?: getEmptyStateColors(type, AppTheme.colors)
    val config = getEmptyStateConfig(size)

    val displayTitle = title ?: getDefaultTitle(type)
    val displayDescription = description ?: getDefaultDescription(type)

    val semanticDescription = contentDescription
        ?: "Empty state: $displayTitle. $displayDescription"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = config.maxWidth)
            .padding(Inset.Large)
            .semantics {
                this.contentDescription = semanticDescription
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(config.spacing)
    ) {
        // Icon
        if (showIcon) {
            icon?.let {
                PixaIcon(
                    painter = it,
                    contentDescription = null,
                    tint = colors.icon,
                    modifier = Modifier.size(config.iconSize)
                )
            } ?: run {
                // Fallback: Circle indicator if no custom icon
                Box(
                    modifier = Modifier
                        .size(config.iconSize)
                        .wrapContentSize()
                ) {
                    PixaIcon(
                        painter = icon ?: return@Box,
                        contentDescription = null,
                        tint = colors.icon,
                        modifier = Modifier.size(config.iconSize.times(0.6f))
                    )
                }
            }
        }

        // Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(config.contentSpacing)
        ) {
            // Title
            Text(
                text = displayTitle,
                style = config.titleStyle(),
                color = colors.title,
                textAlign = TextAlign.Center
            )

            // Description
            Text(
                text = displayDescription,
                style = config.descriptionStyle(),
                color = colors.description,
                textAlign = TextAlign.Center
            )
        }

        // Actions
        if (primaryActionText != null || secondaryActionText != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.Small),
                modifier = Modifier.padding(top = Spacing.Small)
            ) {
                // Primary action
                primaryActionText?.let {
                    PixaButton(
                        text = it,
                        onClick = { onPrimaryAction?.invoke() },
                        variant = ButtonVariant.Solid,
                        size = ButtonSize.Medium,
                        modifier = Modifier.widthIn(min = ComponentSize.ButtonMedium.times(3))
                    )
                }

                // Secondary action
                secondaryActionText?.let {
                    PixaButton(
                        text = it,
                        onClick = { onSecondaryAction?.invoke() },
                        variant = ButtonVariant.Ghost,
                        size = ButtonSize.Medium,
                        modifier = Modifier.widthIn(min = ComponentSize.ButtonMedium.times(3))
                    )
                }
            }
        }
    }
}

// ============================================================================
// CONVENIENCE FUNCTIONS
// ============================================================================

/**
 * Empty content state
 */
@Composable
fun EmptyContent(
    title: String = "No content",
    description: String = "There's nothing to display at the moment",
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    PixaEmptyState(
        type = EmptyStateType.Empty.NoContent,
        title = title,
        description = description,
        modifier = modifier,
        icon = icon,
        primaryActionText = actionText,
        onPrimaryAction = onAction
    )
}

/**
 * No search results state
 */
@Composable
fun EmptySearchResults(
    query: String? = null,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    onClearSearch: (() -> Unit)? = null
) {
    PixaEmptyState(
        type = EmptyStateType.Empty.NoResults,
        title = if (query != null) "No results for \"$query\"" else "No results found",
        description = "Try adjusting your search or filters",
        modifier = modifier,
        icon = icon,
        primaryActionText = if (onClearSearch != null) "Clear Search" else null,
        onPrimaryAction = onClearSearch
    )
}

/**
 * Network error state with retry
 */
@Composable
fun NetworkError(
    title: String = "Connection problem",
    description: String = "Check your internet and try again",
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    onRetry: (() -> Unit)? = null
) {
    PixaEmptyState(
        type = EmptyStateType.Network.NoConnection,
        title = title,
        description = description,
        modifier = modifier,
        icon = icon,
        primaryActionText = if (onRetry != null) "Retry" else null,
        onPrimaryAction = onRetry
    )
}

/**
 * Server error state
 */
@Composable
fun ServerError(
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    onRetry: (() -> Unit)? = null,
    onContactSupport: (() -> Unit)? = null
) {
    PixaEmptyState(
        type = EmptyStateType.Server.InternalError,
        modifier = modifier,
        icon = icon,
        primaryActionText = if (onRetry != null) "Try Again" else null,
        onPrimaryAction = onRetry,
        secondaryActionText = if (onContactSupport != null) "Contact Support" else null,
        onSecondaryAction = onContactSupport
    )
}

/**
 * Permission denied state
 */
@Composable
fun PermissionDenied(
    title: String = "Access required",
    description: String = "You need permission to view this content",
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    onSignIn: (() -> Unit)? = null,
    onRequestAccess: (() -> Unit)? = null
) {
    PixaEmptyState(
        type = EmptyStateType.Permission.Unauthorized,
        title = title,
        description = description,
        modifier = modifier,
        icon = icon,
        primaryActionText = if (onSignIn != null) "Sign In" else null,
        onPrimaryAction = onSignIn,
        secondaryActionText = if (onRequestAccess != null) "Request Access" else null,
        onSecondaryAction = onRequestAccess
    )
}

// ============================================================================
// USAGE EXAMPLES
// ============================================================================

/**
 * USAGE EXAMPLES:
 *
 * 1. Empty list:
 * ```
 * EmptyState(
 *     type = EmptyStateType.Empty.EmptyList,
 *     title = "No items yet",
 *     description = "Add your first item to get started"
 * )
 * ```
 *
 * 2. Network error with retry:
 * ```
 * EmptyState(
 *     type = EmptyStateType.Network.NoConnection,
 *     primaryActionText = "Retry",
 *     onPrimaryAction = { viewModel.retry() }
 * )
 * ```
 *
 * 3. Search results:
 * ```
 * EmptySearchResults(
 *     query = searchQuery,
 *     onClearSearch = { viewModel.clearSearch() }
 * )
 * ```
 *
 * 4. Permission error:
 * ```
 * EmptyState(
 *     type = EmptyStateType.Permission.Unauthorized,
 *     primaryActionText = "Sign In",
 *     onPrimaryAction = { navigateToLogin() }
 * )
 * ```
 *
 * 5. Server error:
 * ```
 * ServerError(
 *     onRetry = { viewModel.retry() },
 *     onContactSupport = { openSupport() }
 * )
 * ```
 *
 * 6. Custom empty state:
 * ```
 * EmptyState(
 *     type = EmptyStateType.Custom(StateCategory.Info),
 *     title = "Coming Soon",
 *     description = "This feature is under development",
 *     icon = painterResource(R.drawable.ic_construction)
 * )
 * ```
 *
 * 7. Empty cart:
 * ```
 * EmptyState(
 *     type = EmptyStateType.Empty.EmptyCart,
 *     primaryActionText = "Start Shopping",
 *     onPrimaryAction = { navigateToShop() }
 * )
 * ```
 *
 * 8. Maintenance mode:
 * ```
 * EmptyState(
 *     type = EmptyStateType.Server.Maintenance,
 *     size = EmptyStateSize.Large
 * )
 * ```
 *
 * 9. Compact empty state:
 * ```
 * EmptyState(
 *     type = EmptyStateType.Empty.NoResults,
 *     size = EmptyStateSize.Compact,
 *     showIcon = false
 * )
 * ```
 *
 * 10. Session expired:
 * ```
 * EmptyState(
 *     type = EmptyStateType.Permission.SessionExpired,
 *     primaryActionText = "Log In Again",
 *     onPrimaryAction = { reAuthenticate() }
 * )
 * ```
 */
