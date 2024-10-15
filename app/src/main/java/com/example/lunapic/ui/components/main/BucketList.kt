package com.example.lunapic.ui.components.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.lunapic.ui.components.shared.MyAlertDialog
import com.example.lunapic.ui.components.shared.MyCustomDialog
import com.example.lunapic.ui.components.shared.MySwitch
import com.example.lunapic.ui.state.BucketListEvent
import com.example.lunapic.ui.state.BucketListState

@Composable
fun BucketList(
    state: BucketListState,
    onEvent: (BucketListEvent) -> Unit,
    snackBar : SnackbarHostState,
    onBucketClick : (String) -> Unit
) {
    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = { SnackbarHost(snackBar) },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = {
                onEvent(BucketListEvent.SetCreateDialogState(true))
            }) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Adicionar bucket")
                Text(text = "Adicionar bucket")
            }
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Greeting(name = "Gabriel", vpadding = 6.dp)
            if (state.buckets.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(Dp(6f)),
                    verticalArrangement = Arrangement.spacedBy(Dp(6f)),
                    modifier = Modifier.padding(it)
                ) {
                    items(state.buckets.size) { index ->
                        Card(
                            modifier = Modifier.size(60.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = Dp(6f)),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = state.buckets[index].name.toString(),
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .sizeIn(maxWidth = 145.dp)
                                        .clickable { onBucketClick(state.buckets[index].name.toString()) }
                                )
                                Icon(
                                    imageVector = Icons.Rounded.Delete,
                                    contentDescription = "Excluir bucket",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .selectable(
                                            selected = state.selectedBucket == index,
                                            onClick = {
                                                onEvent(BucketListEvent.SetSelectedBucket(index))
                                                onEvent(BucketListEvent.SetDeleteDialogState(true))
                                            }
                                        )
                                )
                            }
                        }
                    }
                }
                if (state.selectedBucket != -1 && state.selectedBucket < state.buckets.size) {
                    MyAlertDialog(isDialogOpen = state.isDeleteBucketDialogOpen,
                        title = "Atenção",
                        text = "Deseja mesmo excluir o bucket ${state.buckets[state.selectedBucket].name}?",
                        negativeLabel = "Cancelar",
                        positiveLabel = "Confirmar",
                        onDismissRequest = {
                            onEvent(BucketListEvent.SetDeleteDialogState(false))
                            onEvent(BucketListEvent.SetSelectedBucket(-1))
                        }
                    ) {
                        onEvent(BucketListEvent.DeleteBucket(state.buckets[state.selectedBucket]))
                    }
                }
                if (state.isCreateBucketDialogOpen) {
                    MyCustomDialog(onDismissRequest = {
                        onEvent(BucketListEvent.SetCreateDialogState(false))
                    }, content = {
                        OutlinedTextField(
                            value = state.bucketForm.bucketName,
                            onValueChange = { name -> onEvent(BucketListEvent.SetBucketName( name.trim())) },
                            label = { Text(text = "Nome do bucket") },
                            modifier = Modifier.padding(16.dp),
                            supportingText = {
                                Text(text = state.supportText)
                            },
                            isError = state.isError
                        )
                        MySwitch(
                            label = "Bucket privado",
                            value = state.bucketForm.isPrivate,
                            onCheckedChange = { value -> onEvent(BucketListEvent.SetIsPrivate(value)) }
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(
                                onClick = {
                                    onEvent(BucketListEvent.SetCreateDialogState(false))
                                },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text("Cancelar")
                            }
                            TextButton(
                                onClick = {
                                    onEvent(BucketListEvent.CreateBucket)
                                },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text("Confirmar")
                            }
                        }
                    })
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Não há buckets cadastrados.")
                }
            }
        }
    }
}
