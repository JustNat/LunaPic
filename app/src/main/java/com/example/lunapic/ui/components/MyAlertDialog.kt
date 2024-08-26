package com.example.lunapic.ui.components

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MyAlertDialog(
    isDialogOpen : Boolean,
    icon : ImageVector = Icons.Default.Warning,
    title : String,
    text : String,
    negativeLabel : String,
    positiveLabel : String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    if (isDialogOpen) {
        AlertDialog(
            icon = { Icon(imageVector = icon, contentDescription = "aviso") },
            title = { Text(text = title) },
            text = { Text(text = text) },
            onDismissRequest = { onDismissRequest()},
            confirmButton = {
                TextButton(onClick = {
                    onConfirmation()
                }) {
                    Text(text = positiveLabel)
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(text = negativeLabel)
                }
            }
        )
    }
}

@Composable
@Preview
@Preview(name = "darkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun MyAlertDialogPreview() {
    MyAlertDialog(
        isDialogOpen = true,
        title = "Atenção",
        text = "Deseja confirmar a operação?",
        negativeLabel = "Cancelar",
        positiveLabel = "Sim",
        onDismissRequest = { }) {
    }
}