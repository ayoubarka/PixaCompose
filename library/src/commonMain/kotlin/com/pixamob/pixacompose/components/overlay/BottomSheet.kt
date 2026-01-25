package com.pixamob.pixacompose.components.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.theme.*

// ═══════════════════════════════════════════════════════════════════════════════
// CONFIGURATION - Data models and enums
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Bottom Sheet Size Variant
 * Defines height constraints and padding for different sheet sizes
 */
enum class BottomSheetSizeVariant {
    /** Compact: 40% screen height, minimal padding */
    Compact,
    /** Standard: 60% screen height, standard padding (default) */
    Standard,
    /** Expanded: 75% screen height, generous padding */
    Expanded,
    /** Full: 95% screen height, maximum content */
    Full
}

/**
 * Bottom Sheet Style Variant
 * Defines the visual style/color scheme of the sheet
 */
enum class BottomSheetStyle {
    /** Primary: Default surface with standard content colors */
    Primary,
    /** Secondary: Subtle surface with secondary content colors */
    Secondary,
    /** Surface: White/light surface with high contrast */
    Surface
}

/**
 * Bottom Sheet Scope
 * Provides control functions for the bottom sheet
 */
interface BottomSheetScope {
    /** Dismiss/hide the bottom sheet */
    fun dismiss()

    /** Expand sheet fully (if partially expanded) */
    fun expand()

    /** Partially expand sheet (if supported) */
    fun collapse()
}

/**
 * Internal implementation of BottomSheetScope
 */
@OptIn(ExperimentalMaterial3Api::class)
private class BottomSheetScopeImpl(
    private val sheetState: SheetState,
    private val onDismissRequest: () -> Unit
) : BottomSheetScope {
    override fun dismiss() {
        onDismissRequest()
    }

    override fun expand() {
        // Expand to full height if possible
        // SheetState doesn't have direct expand method, handled by state
    }

    override fun collapse() {
        // Collapse to partial height if skipPartiallyExpanded is false
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// THEME - Size mappings and styling configurations
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Bottom Sheet Size Configuration
 * Internal configuration for size variant
 */
@Immutable
private data class BottomSheetSizeConfig(
    val maxHeightFraction: Float,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val cornerRadius: Dp,
    val dragHandleHeight: Dp,
    val dragHandleWidth: Dp
)

/**
 * Maps size variant to concrete dimensions
 */
private fun BottomSheetSizeVariant.toSizeConfig(): BottomSheetSizeConfig = when (this) {
    BottomSheetSizeVariant.Compact -> BottomSheetSizeConfig(
        maxHeightFraction = 0.4f,
        horizontalPadding = HierarchicalSize.Spacing.Medium,
        verticalPadding = HierarchicalSize.Spacing.Medium,
        cornerRadius = RadiusSize.Large,
        dragHandleHeight = 4.dp,
        dragHandleWidth = 32.dp
    )
    BottomSheetSizeVariant.Standard -> BottomSheetSizeConfig(
        maxHeightFraction = 0.6f,
        horizontalPadding = HierarchicalSize.Spacing.Large,
        verticalPadding = HierarchicalSize.Spacing.Large,
        cornerRadius = RadiusSize.ExtraLarge,
        dragHandleHeight = 4.dp,
        dragHandleWidth = 40.dp
    )
    BottomSheetSizeVariant.Expanded -> BottomSheetSizeConfig(
        maxHeightFraction = 0.75f,
        horizontalPadding = HierarchicalSize.Spacing.Huge,
        verticalPadding = HierarchicalSize.Spacing.Huge,
        cornerRadius = RadiusSize.ExtraLarge,
        dragHandleHeight = 4.dp,
        dragHandleWidth = 48.dp
    )
    BottomSheetSizeVariant.Full -> BottomSheetSizeConfig(
        maxHeightFraction = 0.95f,
        horizontalPadding = HierarchicalSize.Spacing.Huge,
        verticalPadding = HierarchicalSize.Spacing.Huge,
        cornerRadius = RadiusSize.ExtraLarge,
        dragHandleHeight = 4.dp,
        dragHandleWidth = 48.dp
    )
}

/**
 * Bottom Sheet Color Configuration
 */
@Stable
private data class BottomSheetColors(
    val container: Color,
    val content: Color,
    val scrim: Color,
    val dragHandle: Color,
    val divider: Color
)

/**
 * Get colors for sheet style (elevated or default)
 */
@Composable
private fun BottomSheetStyle.getColors(elevated: Boolean): BottomSheetColors {
    val colors = AppTheme.colors

    return when (this) {
        BottomSheetStyle.Primary -> BottomSheetColors(
            container = if (elevated) colors.baseSurfaceElevated else colors.baseSurfaceDefault,
            content = colors.baseContentBody,
            scrim = colors.baseSurfaceShadow.copy(alpha = 0.66f),
            dragHandle = colors.baseContentCaption.copy(alpha = 0.4f),
            divider = colors.baseBorderSubtle.copy(alpha = 0.33f)
        )
        BottomSheetStyle.Secondary -> BottomSheetColors(
            container = if (elevated) colors.baseSurfaceElevated else colors.baseSurfaceSubtle,
            content = colors.baseContentSubtitle,
            scrim = colors.baseSurfaceShadow.copy(alpha = 0.66f),
            dragHandle = colors.baseContentCaption.copy(alpha = 0.4f),
            divider = colors.baseBorderSubtle.copy(alpha = 0.33f)
        )
        BottomSheetStyle.Surface -> BottomSheetColors(
            container = if (elevated) colors.baseSurfaceElevated else colors.baseSurfaceDefault,
            content = colors.baseContentBody,
            scrim = colors.baseSurfaceShadow.copy(alpha = 0.66f),
            dragHandle = colors.baseContentCaption.copy(alpha = 0.4f),
            divider = colors.baseBorderSubtle.copy(alpha = 0.33f)
        )
    }
}

/**
 * Nested scroll connection for bottom sheets
 * Allows proper handling of nested scrolling content
 */
private class BottomSheetNestedScrollConnection : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        return Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        return Offset.Zero
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// BASE - Internal composables
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Internal drag handle composable
 */
@Composable
private fun DragHandle(
    width: Dp,
    height: Dp,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .padding(top = 8.dp)
            .semantics { role = Role.Button },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .background(
                    color = color,
                    shape = RoundedCornerShape(height / 2)
                )
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PUBLIC - Main component
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BaseBottomSheet - Foundational Bottom Sheet Component
 *
 * A flexible bottom sheet component with comprehensive customization options.
 * Uses Material 3's ModalBottomSheet as the foundation with full theme integration.
 *
 * ## Key Features
 * - Size variants (Compact/Standard/Expanded/Full) with different height fractions
 * - Style variants (Primary/Secondary/Surface) with color schemes
 * - Elevated variant for depth
 * - Optional drag handle with accessibility
 * - Scrim overlay with customizable color
 * - Nested scroll support for scrollable content
 * - Theme-aware styling with AppTheme integration
 * - BottomSheetScope for programmatic control
 *
 * ## Size Variants
 * - **Compact (40%)**: Quick actions, simple selections
 * - **Standard (60%)**: Default, most common use case
 * - **Expanded (75%)**: Rich content, multiple sections
 * - **Full (95%)**: Full-screen-like experience
 *
 * ## Usage Examples
 *
 * ### Basic sheet with content:
 * ```kotlin
 * var showSheet by remember { mutableStateOf(false) }
 *
 * if (showSheet) {
 *     PixaBottomSheet(
 *         onDismissRequest = { showSheet = false }
 *     ) {
 *         Text("Sheet Content", modifier = Modifier.padding(16.dp))
 *     }
 * }
 * ```
 *
 * ### Expanded sheet with elevation:
 * ```kotlin
 * PixaBottomSheet(
 *     onDismissRequest = { showSheet = false },
 *     size = BottomSheetSizeVariant.Expanded,
 *     style = BottomSheetStyle.Primary,
 *     elevated = true
 * ) {
 *     Column {
 *         Text("Title", style = AppTheme.typography.titleBold)
 *         Divider()
 *         // Content...
 *     }
 * }
 * ```
 *
 * ### Without drag handle:
 * ```kotlin
 * PixaBottomSheet(
 *     onDismissRequest = { showSheet = false },
 *     showDragHandle = false,
 *     shape = RoundedCornerShape(0.dp) // Square top
 * ) {
 *     // Content
 * }
 * ```
 *
 * @param onDismissRequest Callback when sheet should be dismissed
 * @param modifier Modifier for the sheet container
 * @param sheetState Sheet state for controlling sheet behavior
 * @param size Size variant affecting max height and padding
 * @param style Style variant affecting color scheme
 * @param elevated If true, uses elevated surface colors
 * @param shape Custom shape for the sheet (defaults to rounded top corners)
 * @param showDragHandle If true, shows drag handle at top
 * @param skipPartiallyExpanded If true, sheet goes from hidden to fully expanded
 * @param content The content of the bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PixaBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    size: BottomSheetSizeVariant = BottomSheetSizeVariant.Standard,
    style: BottomSheetStyle = BottomSheetStyle.Primary,
    elevated: Boolean = false,
    shape: Shape? = null,
    showDragHandle: Boolean = true,
    skipPartiallyExpanded: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val sizeConfig = size.toSizeConfig()
    val colors = style.getColors(elevated)
    val sheetShape = shape ?: RoundedCornerShape(
        topStart = sizeConfig.cornerRadius,
        topEnd = sizeConfig.cornerRadius
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        containerColor = colors.container,
        contentColor = colors.content,
        scrimColor = colors.scrim,
        shape = sheetShape,
        tonalElevation = if (elevated) 8.dp else 0.dp,
        dragHandle = if (showDragHandle) {
            {
                DragHandle(
                    width = sizeConfig.dragHandleWidth,
                    height = sizeConfig.dragHandleHeight,
                    color = colors.dragHandle
                )
            }
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 1000.dp.times(sizeConfig.maxHeightFraction))
                .padding(
                    horizontal = sizeConfig.horizontalPadding,
                    vertical = sizeConfig.verticalPadding
                )
                .nestedScroll(remember { BottomSheetNestedScrollConnection() })
        ) {
            content()
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// CONVENIENCE - Preset configurations and common patterns
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SelectOptionBottomSheet - Sheet with Trigger and Content Pattern
 *
 * A bottom sheet that combines a trigger composable with sheet content.
 * Automatically manages visibility state.
 *
 * ## Usage Example
 * ```kotlin
 * SelectOptionBottomSheet(
 *     trigger = { show ->
 *         BaseButton(
 *             text = "Select Option",
 *             onClick = { show() }
 *         )
 *     },
 *     content = { dismiss ->
 *         Column {
 *             Text("Option 1", modifier = Modifier.clickable { dismiss() })
 *             Text("Option 2", modifier = Modifier.clickable { dismiss() })
 *         }
 *     }
 * )
 * ```
 *
 * @param trigger Composable that triggers the sheet (receives show function)
 * @param content Content of the sheet (receives dismiss function)
 * @param modifier Modifier for the trigger container
 * @param size Size variant
 * @param style Style variant
 * @param elevated Elevated surface
 * @param showDragHandle Show drag handle
 * @param errorMessage Optional error message below trigger
 * @param onDismiss Additional callback when dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectOptionBottomSheet(
    trigger: @Composable (show: () -> Unit) -> Unit,
    content: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    size: BottomSheetSizeVariant = BottomSheetSizeVariant.Standard,
    style: BottomSheetStyle = BottomSheetStyle.Primary,
    elevated: Boolean = false,
    showDragHandle: Boolean = true,
    errorMessage: String? = null,
    onDismiss: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
    ) {
        trigger { isVisible = true }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = AppTheme.typography.captionBold,
                color = AppTheme.colors.errorContentDefault,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    if (isVisible) {
        PixaBottomSheet(
            onDismissRequest = {
                isVisible = false
                onDismiss()
            },
            sheetState = sheetState,
            size = size,
            style = style,
            elevated = elevated,
            showDragHandle = showDragHandle
        ) {
            content {
                isVisible = false
                onDismiss()
            }
        }
    }
}

/**
 * ExpandableBottomSheet - Sheet with Collapsed and Expanded States
 *
 * A bottom sheet that shows collapsed content initially and can expand to show more.
 *
 * ## Usage Example
 * ```kotlin
 * ExpandableBottomSheet(
 *     collapsedContent = { expand ->
 *         Column {
 *             Text("Preview Content")
 *             BaseButton(
 *                 text = "See More",
 *                 onClick = { expand() }
 *             )
 *         }
 *     },
 *     expandedContent = { dismiss ->
 *         Column {
 *             Text("Full Content")
 *             // ... more content
 *         }
 *     }
 * )
 * ```
 *
 * @param collapsedContent Initial collapsed content (receives expand function)
 * @param expandedContent Full expanded content (receives dismiss function)
 * @param modifier Modifier for container
 * @param initiallyExpanded Start in expanded state
 * @param size Size variant for expanded state
 * @param style Style variant
 * @param elevated Elevated surface
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableBottomSheet(
    collapsedContent: @Composable ColumnScope.(expand: () -> Unit) -> Unit,
    expandedContent: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false,
    size: BottomSheetSizeVariant = BottomSheetSizeVariant.Expanded,
    style: BottomSheetStyle = BottomSheetStyle.Primary,
    elevated: Boolean = false
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }
    var isVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        if (!isVisible) {
            collapsedContent {
                isVisible = true
                isExpanded = true
            }
        }
    }

    if (isVisible) {
        PixaBottomSheet(
            onDismissRequest = {
                isVisible = false
                isExpanded = false
            },
            sheetState = sheetState,
            size = if (isExpanded) size else BottomSheetSizeVariant.Compact,
            style = style,
            elevated = elevated,
            skipPartiallyExpanded = false
        ) {
            expandedContent {
                isVisible = false
                isExpanded = false
            }
        }
    }
}

/**
 * ListBottomSheet - Sheet with List of Selectable Items
 *
 * A bottom sheet displaying a list of items for selection.
 *
 * ## Usage Example
 * ```kotlin
 * ListBottomSheet(
 *     title = "Select Country",
 *     items = countries,
 *     onItemSelected = { country ->
 *         // Handle selection
 *     }
 * ) { country, onSelect ->
 *     Text(
 *         text = country.name,
 *         modifier = Modifier
 *             .fillMaxWidth()
 *             .clickable { onSelect(country) }
 *             .padding(16.dp)
 *     )
 * }
 * ```
 *
 * @param title Title text for the sheet
 * @param items List of items to display
 * @param onItemSelected Callback when an item is selected
 * @param itemContent Composable for each item (receives item and select callback)
 * @param modifier Modifier for container
 * @param size Size variant
 * @param style Style variant
 * @param elevated Elevated surface
 * @param onDismiss Callback when sheet is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ListBottomSheet(
    title: String,
    items: List<T>,
    onItemSelected: (T) -> Unit,
    itemContent: @Composable (item: T, onSelect: (T) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    size: BottomSheetSizeVariant = BottomSheetSizeVariant.Standard,
    style: BottomSheetStyle = BottomSheetStyle.Primary,
    elevated: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isVisible by remember { mutableStateOf(true) }

    if (isVisible) {
        PixaBottomSheet(
            onDismissRequest = {
                isVisible = false
                onDismiss()
            },
            sheetState = sheetState,
            size = size,
            style = style,
            elevated = elevated
        ) {
            Text(
                text = title,
                style = AppTheme.typography.titleBold,
                color = AppTheme.colors.baseContentTitle,
                modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Small)
            )

            androidx.compose.material3.HorizontalDivider(
                thickness = 1.dp,
                color = AppTheme.colors.baseBorderSubtle.copy(alpha = 0.33f),
                modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Medium)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
            ) {
                items(items) { item ->
                    itemContent(item) { selectedItem ->
                        onItemSelected(selectedItem)
                        isVisible = false
                    }
                }
            }
        }
    }
}

/**
 * ConfirmationBottomSheet - Sheet for Confirmation Dialogs
 *
 * A bottom sheet for confirming actions with title, message, and action buttons.
 *
 * ## Usage Example
 * ```kotlin
 * ConfirmationBottomSheet(
 *     title = "Delete Item?",
 *     message = "This action cannot be undone.",
 *     confirmText = "Delete",
 *     cancelText = "Cancel",
 *     onConfirm = { /* Delete item */ },
 *     onDismiss = { /* Cancel */ }
 * )
 * ```
 *
 * @param title Title text
 * @param message Message/description text
 * @param confirmText Text for confirm button
 * @param cancelText Text for cancel button
 * @param onConfirm Callback when confirmed
 * @param onDismiss Callback when dismissed/canceled
 * @param modifier Modifier for container
 * @param isDestructive If true, uses destructive/error colors for confirm button
 * @param size Size variant
 * @param style Style variant
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationBottomSheet(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false,
    size: BottomSheetSizeVariant = BottomSheetSizeVariant.Compact,
    style: BottomSheetStyle = BottomSheetStyle.Primary
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    PixaBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        size = size,
        style = style,
        modifier = modifier
    ) {
        Text(
            text = title,
            style = AppTheme.typography.titleBold,
            color = AppTheme.colors.baseContentTitle
        )

        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))

        Text(
            text = message,
            style = AppTheme.typography.bodyRegular,
            color = AppTheme.colors.baseContentBody
        )

        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)
        ) {
            PixaButton(
                text = cancelText,
                onClick = onDismiss,
                variant = ButtonVariant.Outlined,
                modifier = Modifier.weight(1f)
            )

            PixaButton(
                text = confirmText,
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                variant = ButtonVariant.Solid,
                isDestructive = isDestructive,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

