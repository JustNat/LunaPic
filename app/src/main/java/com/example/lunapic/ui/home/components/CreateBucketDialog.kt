package com.example.lunapic.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lunapic.ui.common.MyCustomDialog
import com.example.lunapic.ui.common.MySwitch
import com.example.lunapic.ui.home.state.HomeScreenEvents
import com.example.lunapic.ui.home.state.components.CreateBucketForm

@Composable
fun CreateBucketDialog(state: CreateBucketForm, onEvent: (HomeScreenEvents) -> Unit) {
    if (state.isOpen) {
        MyCustomDialog(onDismissRequest = {
            onEvent(HomeScreenEvents.SetCreateDialogState(false))
        }, content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Novo Bucket", fontWeight = FontWeight.Medium, fontSize = 18.sp)
            }
            OutlinedTextField(
                value = state.bucketName,
                onValueChange = { name -> onEvent(HomeScreenEvents.SetBucketName(name.filterNot { it -> it.isWhitespace() })) },
                label = { Text(text = "Nome do bucket") },
                modifier = Modifier.padding(16.dp),
                supportingText = {
                    Text(text = state.supportText)
                },
                isError = state.isError
            )
            MySwitch(
                label = "Privado",
                hPadding = 16.dp,
                value = state.isPrivate,
                onCheckedChange = { value -> onEvent(HomeScreenEvents.SetIsPrivate(value)) }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    onClick = {
                        onEvent(HomeScreenEvents.SetCreateDialogState(false))
                    },
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text("Cancelar")
                }
                TextButton(
                    onClick = {
                        onEvent(HomeScreenEvents.CreateBucket)
                    },
                    modifier = Modifier.padding(8.dp),
                    enabled = !state.isError
                ) {
                    Text("Confirmar")
                }
            }
        })
    }
}