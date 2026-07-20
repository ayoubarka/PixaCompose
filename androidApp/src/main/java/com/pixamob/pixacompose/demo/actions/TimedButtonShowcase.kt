package com.pixamob.pixacompose.demo.actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaTimedButton
import com.pixamob.pixacompose.components.actions.TimedButtonDuration
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun TimedButtonShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Timed Button") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaTimedButton(text = "Send Code", onTimeout = {}, durationSeconds = TimedButtonDuration.Short)
                PixaTimedButton(text = "Resend SMS", onTimeout = {}, durationSeconds = TimedButtonDuration.Medium)
            }
        }
    }
}
