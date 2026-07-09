package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.navigation.HorizontalStepper
import com.pixamob.pixacompose.components.navigation.StepData
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun StepperShowcase() {
    var currentStep by remember { mutableStateOf(0) }
    val steps = remember {
        listOf(
            StepData(title = "Account", subTitle = "Personal info"),
            StepData(title = "Verification", subTitle = "Verify identity"),
            StepData(title = "Complete", subTitle = "Finish setup")
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Horizontal Stepper") {
            HorizontalStepper(
                steps = steps,
                currentStep = currentStep,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("Current Step Content") {
            val content = when (currentStep) {
                0 -> "Enter your personal information to create an account."
                1 -> "Please verify your identity by confirming your email."
                2 -> "Your setup is complete! You can now start using the app."
                else -> ""
            }
            Text(
                text = content,
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody
            )
        }

        ShowcaseSection("Navigation") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PixaButton(
                    text = "Previous",
                    onClick = { if (currentStep > 0) currentStep-- },
                    enabled = currentStep > 0,
                    variant = ButtonVariant.Outlined
                )
                PixaButton(
                    text = if (currentStep < steps.size - 1) "Next" else "Finish",
                    onClick = { if (currentStep < steps.size - 1) currentStep++ },
                    enabled = currentStep < steps.size
                )
            }
        }

        ShowcaseSection("Progress") {
            Text(
                text = "Step ${currentStep + 1} of ${steps.size}",
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.brandContentDefault
            )
        }
    }
}
