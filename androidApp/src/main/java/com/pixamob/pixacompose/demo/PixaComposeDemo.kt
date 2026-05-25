package com.pixamob.pixacompose.demo

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.feedback.BadgeStyle
import com.pixamob.pixacompose.components.feedback.BadgeVariant
import com.pixamob.pixacompose.components.feedback.PixaBadge
import com.pixamob.pixacompose.components.feedback.PixaCircularIndicator
import com.pixamob.pixacompose.components.feedback.PixaLinearIndicator
import com.pixamob.pixacompose.components.feedback.ProgressSize
import com.pixamob.pixacompose.components.inputs.PixaCheckbox
import com.pixamob.pixacompose.components.inputs.LabeledRadioButton
import com.pixamob.pixacompose.components.inputs.PixaSlider
import com.pixamob.pixacompose.components.inputs.PixaSwitch
import com.pixamob.pixacompose.components.inputs.PixaTextField
import com.pixamob.pixacompose.components.inputs.TextFieldVariant
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun PixaComposeDemo() {
    var isDark by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF121212) else Color(0xFFF5F5F5),
        label = "bg"
    )
    val surfaceColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF1E1E1E) else Color.White,
        label = "surface"
    )

    Column(modifier = Modifier.fillMaxSize().background(bgColor)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(surfaceColor)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "PixaCompose Showcase",
                style = AppTheme.typography.titleBold,
                color = AppTheme.colors.baseContentTitle,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppTheme.colors.baseSurfaceSubtle)
                    .clickable { isDark = !isDark },
                contentAlignment = Alignment.Center
            ) {
                Text(if (isDark) "\u2600\uFE0F" else "\uD83C\uDF19", fontSize = 20.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SectionTitle("Actions")
            ActionsSection()

            SectionTitle("Inputs")
            InputsSection()

            SectionTitle("Display & Feedback")
            DisplayFeedbackSection()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = AppTheme.typography.titleBold,
        color = AppTheme.colors.brandContentDefault,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun ComponentLabel(text: String) {
    Text(
        text = text,
        style = AppTheme.typography.captionRegular,
        color = AppTheme.colors.baseContentCaption,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun ShowcaseSection(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ComponentLabel(label)
        content()
    }
}

@Composable
fun ActionsSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ShowcaseSection("PixaButton - Variants") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ButtonVariant.entries.forEach { variant ->
                    PixaButton(
                        text = variant.name,
                        variant = variant,
                        size = SizeVariant.Compact,
                        onClick = {}
                    )
                }
            }
        }

        ShowcaseSection("PixaButton - Sizes") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(SizeVariant.Compact, SizeVariant.Small, SizeVariant.Medium).forEach { size ->
                    PixaButton(text = size.name, size = size, onClick = {})
                }
            }
        }

        ShowcaseSection("PixaButton - States") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PixaButton(text = "Disabled", enabled = false, onClick = {})
                PixaButton(text = "Loading", loading = true, onClick = {})
                PixaButton(text = "Skeleton", showSkeleton = true, onClick = {})
            }
        }
    }
}

@Composable
fun InputsSection() {
    val textValue = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ShowcaseSection("PixaTextField") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextFieldVariant.entries.forEachIndexed { index, variant ->
                    PixaTextField(
                        value = if (index == 0) textValue.value else "${variant.name} field",
                        onValueChange = { if (index == 0) textValue.value = it },
                        variant = variant,
                        label = variant.name,
                        placeholder = "Type here...",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        ShowcaseSection("PixaCheckbox") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PixaCheckbox(checked = true, onCheckedChange = {}, label = "Checked")
                PixaCheckbox(checked = false, onCheckedChange = {}, label = "Unchecked")
                PixaCheckbox(checked = false, onCheckedChange = {}, label = "Disabled", enabled = false)
            }
        }

        ShowcaseSection("RadioButton") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LabeledRadioButton(selected = true, onClick = {}, label = "Selected")
                LabeledRadioButton(selected = false, onClick = {}, label = "Unselected")
            }
        }

        ShowcaseSection("PixaSwitch") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PixaSwitch(checked = true, onCheckedChange = {})
                PixaSwitch(checked = false, onCheckedChange = {})
            }
        }

        ShowcaseSection("PixaSlider") {
            PixaSlider(value = 0.5f, onValueChange = {}, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun DisplayFeedbackSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ShowcaseSection("PixaBadge") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                BadgeVariant.entries.forEach { variant ->
                    PixaBadge(
                        content = variant.name.take(3),
                        variant = variant,
                        style = BadgeStyle.Solid
                    )
                }
            }
        }

        ShowcaseSection("PixaCircularIndicator") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PixaCircularIndicator(progress = null, sizePreset = ProgressSize.Small)
                PixaCircularIndicator(progress = 0.25f, sizePreset = ProgressSize.Medium)
                PixaCircularIndicator(progress = 0.75f, sizePreset = ProgressSize.Large)
            }
        }

        ShowcaseSection("PixaLinearIndicator") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaLinearIndicator(progress = null, modifier = Modifier.fillMaxWidth())
                PixaLinearIndicator(progress = 0.5f, modifier = Modifier.fillMaxWidth())
                PixaLinearIndicator(progress = 1f, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
