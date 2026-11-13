package com.pixamob.pixacompose.primitives

import androidx.compose.ui.window.DialogProperties

// decorFitsSystemWindows = false also sets the window's soft input mode to adjust nothing.
internal actual fun edgeToEdgeDialogProperties(): DialogProperties =
    DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
