package com.example.lunapic.ui.media.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Done
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    padding: Dp = 16.dp,
    size: Dp = 24.dp,
    iconSize : Dp = 20.dp,
    uncheckedColor: Color = Color.LightGray,
    checkedColor: Color = MaterialTheme.colorScheme.primary,
) {

    val circleColor by animateColorAsState(
        targetValue = if (checked) checkedColor else Color.Transparent, label = "checkboxColor"
    )

    Box(
        modifier = Modifier
            .padding(padding)
            .size(size)
            .clickable { onCheckedChange(!checked) }
            .drawBehind {
                drawCircle(color = uncheckedColor, style = Stroke(width = 1.dp.toPx()))
                drawCircle(color = circleColor)
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = checked,
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = Icons.Sharp.Done,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

}

@Composable
@Preview
private fun RoundCheckboxPreview() {
    var checked by remember { mutableStateOf(false) }
    MaterialTheme {
        Row(modifier = Modifier.fillMaxWidth()) {
            RoundCheckbox(checked = checked, onCheckedChange = { checked = it })
            Checkbox(checked = checked, onCheckedChange = { checked = it })
        }
    }
}
