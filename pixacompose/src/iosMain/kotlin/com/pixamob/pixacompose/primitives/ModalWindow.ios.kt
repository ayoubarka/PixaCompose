package com.pixamob.pixacompose.primitives

import androidx.compose.ui.window.DialogProperties

internal actual fun edgeToEdgeDialogProperties(): DialogProperties =
    DialogProperties(usePlatformDefaultWidth = false, usePlatformInsets = false, useSoftwareKeyboardInset = false)
