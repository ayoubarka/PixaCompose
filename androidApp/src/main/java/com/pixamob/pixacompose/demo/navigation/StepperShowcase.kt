package com.pixamob.pixacompose.demo.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.components.navigation.PixaStepper
import com.pixamob.pixacompose.components.navigation.StepData
import com.pixamob.pixacompose.components.navigation.StepperOrientation
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun StepperShowcase() {
    var step by remember { mutableStateOf(0) }

    ShowcaseScreen {
        ShowcaseSection("Horizontal") {
            PixaStepper(
                steps = listOf(
                    StepData(title = "Cart"),
                    StepData(title = "Shipping"),
                    StepData(title = "Payment"),
                    StepData(title = "Confirm")
                ),
                currentStep = step,
                onStepClick = { step = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("Vertical") {
            PixaStepper(
                steps = listOf(
                    StepData(title = "Personal Info"),
                    StepData(title = "Address"),
                    StepData(title = "Payment"),
                    StepData(title = "Review")
                ),
                currentStep = step,
                onStepClick = { step = it },
                orientation = StepperOrientation.Vertical,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
