package com.example.lunapic.ui.components.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MySwitch(label : String, value : Boolean, onCheckedChange : (Boolean) -> Unit ) {
    Column {
        Text(text = label, modifier = Modifier.padding(horizontal = 16.dp))
        Switch(
            checked = value,
            onCheckedChange = { onCheckedChange(it) },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}