package com.example.lunapic.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.lunapic.ui.theme.LunaPicTheme
import kotlin.Exception

@Composable
fun CreateBucketDialog(onDismissRequest: () -> Unit, onConfirmation: (String) -> Unit) {
    val supportTextString =
        "Deve conter de 3 a 63 caracteres. Começar e terminar com letra ou número. Não deve conter letras maiúsculas."
    val bucketName = remember { mutableStateOf("") }
    val supportText = remember { mutableStateOf(supportTextString) }
    val supportTextColor = remember { mutableStateOf(Color.Unspecified) }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                OutlinedTextField(
                    value = bucketName.value,
                    onValueChange = {
                        try {
                            bucketName.value = it
                            val verification = verifyBucketName(bucketName.value.trim())
                            if (verification == 1) {
                                supportText.value = supportTextString
                                supportTextColor.value = Color.Unspecified
                            } else {
                                supportText.value = supportTextString
                                supportTextColor.value = Color.Unspecified
                            }
                        } catch (e: Exception) {
                            bucketName.value = it
                            supportTextColor.value = Color.Red
                            supportText.value = e.localizedMessage ?: "Inválido"
                        }
                    },
                    label = { Text(text = "Nome do bucket") },
                    modifier = Modifier.padding(16.dp),
                    supportingText = {
                        Text(
                            text = supportText.value,
                            color = supportTextColor.value
                        )
                    }
                )
                MySwitch("Bucket privado")
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Cancelar")
                    }
                    TextButton(
                        onClick = {
                            if (verifyBucketName(bucketName.value.trim()) == 1) {
                                onConfirmation(bucketName.value.trim())
                                onDismissRequest()
                            } else {
                                supportText.value = "Campo obrigatório."
                                supportTextColor.value = Color.Red
                            }
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}

private fun verifyBucketName(bucketName: String): Int {
    return if (bucketName.isBlank()) {
        0
    } else if (bucketName.length < 3 || bucketName.length > 63) {
        throw Exception("Nome do bucket deve conter de 3 a 63 caracteres.")
    } else if (!bucketName[0].isLetterOrDigit() || !bucketName.last().isLetterOrDigit()) {
        throw Exception("Nome do bucket deve começar e terminar com letra ou número.")
    } else if (bucketName != bucketName.lowercase()) {
        throw Exception("Nome do bucket não pode conter letras maiúsculas.")
    } else if (bucketName.contains(' ')) {
        throw Exception("Nome do bucket não pode conter espaços.")
    } else 1
}

@Composable
@Preview
@Preview("darkTheme", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun CreateBucketDialogPreview() {
    LunaPicTheme {
        Surface {
            CreateBucketDialog(onDismissRequest = {}) {}
        }
    }
}