package com.pixamob.pixacompose.components.feedback

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils

/**
 * EmptyState — a display for when there is no content to show.
 *
 * Shown for first-time use, completed/cleared tasks, errors, or no search
 * results. Always pair with a CTA or clear instruction.
 *
 * ### Anatomy
 * Required: headline (one line) + body text (1-2 sentences).
 * Optional: badge/spot illustration and up to 2 CTA buttons.
 *
 * ### Variants
 * [EmptyStateType] categorizes states: Empty, Network, Client, Server,
 * Permission, and Custom — each resolving to one of 4 illustration colors
 * via [StateCategory].
 *
 * ### States
 * One-shot fade-in on first composition. Empty/failure/success are
 * represented by [EmptyStateType].
 *
 * ### Sizing / Adaptive behavior
 * [size] is explicit (default: Medium). Set [adaptiveSize] to derive from
 * [AppTheme.adaptiveSizeVariant] instead.
 *
 * ### Customization
 * [titleMaxLines]/[descriptionMaxLines] default to unlimited (wrap).
 * Up to 2 CTAs ([primaryActionText]/[secondaryActionText]).
 * [illustrationContentDescription] defaults to null (hidden from a11y).
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

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

sealed class EmptyStateType {
    sealed class Empty : EmptyStateType() {
        object NoContent : Empty()
        object NoResults : Empty()
        object EmptyList : Empty()
        object EmptyInbox : Empty()
        object EmptyFavorites : Empty()
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
        val category: StateCategory = StateCategory.FirstUse
    ) : EmptyStateType()
}

/**
 * 4 illustration color categories: FirstUse, Warning, Failure, Success.
 * Every [EmptyStateType] leaf resolves to exactly one of these.
 */
enum class StateCategory {
    /** Black illustration — onboarding / first-time use. */
    FirstUse,
    /** Yellow illustration — recoverable, user-actionable issues. */
    Warning,
    /** Red illustration — system errors / unsuccessful outcomes. */
    Failure,
    /** Green illustration — completed or cleared tasks. */
    Success
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
        // Null = no width cap, just fillMaxWidth
    val maxWidth: Dp?,
    val ctaSize: SizeVariant
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Resolves colors per [EmptyStateType] and [StateCategory].
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
            icon = colors.errorContentDefault,
            title = colors.baseContentTitle,
            description = colors.baseContentBody
        )
        is EmptyStateType.Client -> EmptyStateColors(
            icon = colors.warningContentDefault,
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
                StateCategory.FirstUse -> colors.baseContentCaption
                StateCategory.Warning -> colors.warningContentDefault
                StateCategory.Failure -> colors.errorContentDefault
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
 * Maps [SizeVariant] to icon size, typography, spacing, and max width.
 * Narrow tiers have no max-width cap; Medium+ clamp to [HierarchicalSize.Container.DialogMaxWidth].
 */
@Composable
private fun getEmptyStateConfig(size: SizeVariant): EmptyStateConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.Compact, SizeVariant.Small, SizeVariant.Nano -> EmptyStateConfig(
            iconSize = HierarchicalSize.Icon.Massive, // 48dp
            titleStyle = { typography.bodyBold },
            descriptionStyle = { typography.captionRegular },
            spacing = HierarchicalSize.Spacing.Small,
            contentSpacing = HierarchicalSize.Spacing.Compact,
            maxWidth = null,
            ctaSize = SizeVariant.Small
        )
        SizeVariant.Medium -> EmptyStateConfig(
            iconSize = HierarchicalSize.Image.Small, // 80dp
            titleStyle = { typography.subtitleBold },
            descriptionStyle = { typography.bodyBold },
            spacing = HierarchicalSize.Spacing.Medium,
            contentSpacing = HierarchicalSize.Spacing.Small,
            maxWidth = HierarchicalSize.Container.DialogMaxWidth,
            ctaSize = SizeVariant.Medium
        )
        SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> EmptyStateConfig(
            iconSize = HierarchicalSize.Image.Medium, // 120dp
            titleStyle = { typography.titleBold },
            descriptionStyle = { typography.bodyRegular },
            spacing = HierarchicalSize.Spacing.Large,
            contentSpacing = HierarchicalSize.Spacing.Medium,
            maxWidth = HierarchicalSize.Container.DialogMaxWidth,
            // Medium CTAs for Large+ tiers
            ctaSize = SizeVariant.Medium
        )
        else -> EmptyStateConfig(
            iconSize = HierarchicalSize.Image.Small,
            titleStyle = { typography.subtitleBold },
            descriptionStyle = { typography.bodyBold },
            spacing = HierarchicalSize.Spacing.Medium,
            contentSpacing = HierarchicalSize.Spacing.Small,
            maxWidth = HierarchicalSize.Container.DialogMaxWidth,
            ctaSize = SizeVariant.Medium
        )
    }
}

/**
 * Default title per [EmptyStateType]
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
 * Default description per [EmptyStateType]
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

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * EmptyState - Display when content is unavailable or errors occur
 *
 * @param type Type of empty state (determines default styling and messages)
 * @param modifier Modifier for the empty state
 * @param title Title text (null to use default based on type)
 * @param description Description text (null to use default based on type)
 * @param icon Optional badge/spot illustration painter (null to omit the illustration slot entirely)
 * @param showIcon Whether to show the illustration
 * @param illustrationContentDescription Accessibility label for the illustration; null (default) hides it from accessibility as decorative
 * @param size Size variant for the empty state (ignored when [adaptiveSize] is true)
 * @param adaptiveSize Derive size from [AppTheme.adaptiveSizeVariant] instead of [size]
 * @param titleMaxLines Line cap for the headline (Default: unlimited/wrap)
 * @param descriptionMaxLines Line cap for the body text (Default: unlimited/wrap)
 * @param primaryActionText Text for primary action button
 * @param onPrimaryAction Callback for primary action
 * @param secondaryActionText Text for secondary action button
 * @param onSecondaryAction Callback for secondary action
 * @param customColors Custom colors (null to use defaults based on type)
 * @param contentDescription Accessibility description for the overall component
 */
@Composable
fun PixaEmptyState(
    type: EmptyStateType = EmptyStateType.Empty.NoContent,
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    icon: Painter? = null,
    showIcon: Boolean = true,
    illustrationContentDescription: String? = null,
    size: SizeVariant = SizeVariant.Medium,
    adaptiveSize: Boolean = false,
    titleMaxLines: Int = Int.MAX_VALUE,
    descriptionMaxLines: Int = Int.MAX_VALUE,
    primaryActionText: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    secondaryActionText: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    customColors: EmptyStateColors? = null,
    contentDescription: String? = null
) {
    val colors = customColors ?: getEmptyStateColors(type, AppTheme.colors)
    val effectiveSize = if (adaptiveSize) AppTheme.adaptiveSizeVariant else size
    val config = getEmptyStateConfig(effectiveSize)

    val displayTitle = title ?: getDefaultTitle(type)
    val displayDescription = description ?: getDefaultDescription(type)

    val semanticDescription = contentDescription
        ?: "Empty state: $displayTitle. $displayDescription"

    // One-shot fade-in on first composition.
    val visibleState = remember { MutableTransitionState(false) }
    visibleState.targetState = true

    AnimatedVisibility(
        visibleState = visibleState,
        enter = AnimationUtils.fadeInTransition
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .then(if (config.maxWidth != null) Modifier.widthIn(max = config.maxWidth) else Modifier)
                .padding(HierarchicalSize.Spacing.Large)
                .semantics {
                    this.contentDescription = semanticDescription
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(config.spacing)
        ) {
            // Illustration — badge/spot artwork. Hidden from a11y by default.
            if (showIcon && icon != null) {
                PixaIcon(
                    painter = icon,
                    contentDescription = illustrationContentDescription,
                    tint = colors.icon,
                    modifier = Modifier
                        .size(config.iconSize)
                        .then(
                            if (illustrationContentDescription == null) {
                                Modifier.semantics { hideFromAccessibility() }
                            } else Modifier
                        )
                )
            }

            // Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(config.contentSpacing)
            ) {
                // Headline — required, carries a11y heading trait.
                BasicText(
                    text = displayTitle,
                    style = config.titleStyle().copy(color = colors.title, textAlign = TextAlign.Center),
                    maxLines = titleMaxLines,
                    overflow = if (titleMaxLines == Int.MAX_VALUE) TextOverflow.Clip else TextOverflow.Ellipsis,
                    modifier = Modifier.semantics { heading() }
                )

                // Body — required, 1-2 sentences.
                BasicText(
                    text = displayDescription,
                    style = config.descriptionStyle().copy(color = colors.description, textAlign = TextAlign.Center),
                    maxLines = descriptionMaxLines,
                    overflow = if (descriptionMaxLines == Int.MAX_VALUE) TextOverflow.Clip else TextOverflow.Ellipsis
                )
            }

            // Actions — caps CTAs at 2 (primary + secondary).
            if (primaryActionText != null || secondaryActionText != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
                    modifier = Modifier.padding(top = HierarchicalSize.Spacing.Small)
                ) {
                    primaryActionText?.let {
                        PixaButton(
                            text = it,
                            onClick = { onPrimaryAction?.invoke() },
                            variant = ButtonVariant.Filled,
                            size = config.ctaSize,
                            modifier = Modifier.widthIn(min = HierarchicalSize.Button.Medium.times(3))
                        )
                    }

                    secondaryActionText?.let {
                        PixaButton(
                            text = it,
                            onClick = { onSecondaryAction?.invoke() },
                            variant = ButtonVariant.Ghost,
                            size = config.ctaSize,
                            modifier = Modifier.widthIn(min = HierarchicalSize.Button.Medium.times(3))
                        )
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE FUNCTIONS
// ════════════════════════════════════════════════════════════════════════════

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

// ════════════════════════════════════════════════════════════════════════════
// USAGE EXAMPLES
// ════════════════════════════════════════════════════════════════════════════

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
 *     type = EmptyStateType.Custom(StateCategory.FirstUse),
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
 *     size = SizeVariant.Large
 * )
 * ```
 *
 * 9. Compact empty state:
 * ```
 * EmptyState(
 *     type = EmptyStateType.Empty.NoResults,
 *     size = SizeVariant.Compact,
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
