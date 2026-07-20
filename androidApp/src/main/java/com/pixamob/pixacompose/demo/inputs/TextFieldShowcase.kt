package com.pixamob.pixacompose.demo.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.inputs.PixaTextField
import com.pixamob.pixacompose.components.inputs.TextFieldVariant
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun TextFieldShowcase() {
    var text by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Variants") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaTextField(
                    value = text, onValueChange = { text = it },
                    variant = TextFieldVariant.Filled, label = "Filled", placeholder = "Enter text"
                )
                PixaTextField(
                    value = text, onValueChange = { text = it },
                    variant = TextFieldVariant.Outlined, label = "Outlined", placeholder = "Enter text"
                )
                PixaTextField(
                    value = text, onValueChange = { text = it },
                    variant = TextFieldVariant.Ghost, label = "Ghost", placeholder = "Enter text"
                )
            }
        }

        ShowcaseSection("Sizes") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaTextField(value = text, onValueChange = { text = it }, size = SizeVariant.Small, label = "Small", placeholder = "Small size")
                PixaTextField(value = text, onValueChange = { text = it }, size = SizeVariant.Medium, label = "Medium", placeholder = "Medium size")
                PixaTextField(value = text, onValueChange = { text = it }, size = SizeVariant.Large, label = "Large", placeholder = "Large size")
            }
        }

        ShowcaseSection("States") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaTextField(value = "Enabled", onValueChange = {}, label = "Enabled")
                PixaTextField(value = "Disabled", onValueChange = {}, enabled = false, label = "Disabled")
                PixaTextField(value = "Read only", onValueChange = {}, readOnly = true, label = "Read Only")
                PixaTextField(value = "Error", onValueChange = {}, isError = true, label = "Error", errorText = "This field has an error")
            }
        }

        ShowcaseSection("Interactive") {
            PixaTextField(value = text, onValueChange = { text = it }, label = "Type here", placeholder = "Start typing...")
        }

        ShowcaseSection("Password Field") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PixaTextField(
                    value = password, onValueChange = { password = it },
                    label = "Password", placeholder = "Enter password",
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = if (passwordVisible) rememberVectorPainter(Icons.Default.VisibilityOff) else rememberVectorPainter(Icons.Default.Visibility)
                )
            }
        }
    }
}
