package com.example.lunapic.ui.components.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MySwitch(
    label: String = "",
    hPadding: Dp = 8.dp,
    vPadding: Dp = 0.dp,
    value: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = hPadding, vertical = vPadding)
            )
        }
        Switch(
            checked = value,
            onCheckedChange = { onCheckedChange(it) },
            modifier = Modifier.padding(horizontal = hPadding, vertical = vPadding)
        )
    }
}