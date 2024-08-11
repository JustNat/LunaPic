package com.example.lunapic.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MySwitch() {
    val isPrivate = remember { mutableStateOf(false) }
    Column {
        Text(text = "Bucket privado", modifier = Modifier.padding(horizontal = 16.dp))
        Switch(
            checked = isPrivate.value,
            onCheckedChange = {isPrivate.value = it},
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}