package com.pixamob.pixacompose.primitives

import androidx.compose.ui.window.DialogProperties

/**
 * Dialog window that covers the whole screen, system bars included, and leaves the keyboard and
 * the bar insets to the content. Without it Android pans the window when the keyboard opens,
 * so content that also pads itself for the keyboard is pushed up twice.
 */
internal expect fun edgeToEdgeDialogProperties(): DialogProperties
