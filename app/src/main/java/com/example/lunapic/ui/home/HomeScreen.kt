package com.example.lunapic.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lunapic.ui.common.MyAlertDialog
import com.example.lunapic.ui.common.MyCustomDialog
import com.example.lunapic.ui.common.MySwitch
import com.example.lunapic.ui.home.components.Greeting
import com.example.lunapic.ui.home.components.NewBucketsDialog
import com.example.lunapic.ui.home.state.HomeScreenEvents
import com.example.lunapic.ui.home.state.HomeScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeScreenState,
    onEvent: (HomeScreenEvents) -> Unit
) {
    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = { SnackbarHost(state.snackBarHost) },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = {
                onEvent(HomeScreenEvents.SetCreateDialogState(true))
            }) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Adicionar bucket")
                Text(text = "Adicionar bucket")
            }
        },
        topBar = {
            TopAppBar(
                title = { Greeting(name = "Gabriel") },
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            if (state.buckets.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(Dp(6f)),
                    verticalArrangement = Arrangement.spacedBy(Dp(6f)),
                    contentPadding = PaddingValues(horizontal = 8.dp),
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
                                        .clickable {
                                            onEvent(
                                                HomeScreenEvents.NavigateToMediaScreen(
                                                    state.buckets[index].name.toString()
                                                )
                                            )
                                        }
                                )
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Excluir bucket",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .selectable(
                                            selected = state.selectedBucket == index,
                                            onClick = {
                                                onEvent(HomeScreenEvents.SetSelectedBucket(index))
                                                onEvent(HomeScreenEvents.SetDeleteDialogState(true))
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
                            onEvent(HomeScreenEvents.SetDeleteDialogState(false))
                            onEvent(HomeScreenEvents.SetSelectedBucket(-1))
                        }
                    ) {
                        onEvent(HomeScreenEvents.DeleteBucket(state.buckets[state.selectedBucket]))
                    }
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
        if (state.createBucketForm.isCreateBucketDialog) {
            MyCustomDialog(onDismissRequest = {
                onEvent(HomeScreenEvents.SetCreateDialogState(false))
            }, content = {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Novo Bucket", fontWeight = FontWeight.Medium, fontSize = 18.sp)
                }
                OutlinedTextField(
                    value = state.createBucketForm.bucketName,
                    onValueChange = { name -> onEvent(HomeScreenEvents.SetBucketName(name.filterNot { it.isWhitespace() })) },
                    label = { Text(text = "Nome do bucket") },
                    modifier = Modifier.padding(16.dp),
                    supportingText = {
                        Text(text = state.createBucketForm.supportText)
                    },
                    isError = state.createBucketForm.isError
                )
                MySwitch(
                    label = "Privado",
                    hPadding = 16.dp,
                    value = state.createBucketForm.isPrivate,
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
                        enabled = !state.createBucketForm.isError
                    ) {
                        Text("Confirmar")
                    }
                }
            })
        }
        NewBucketsDialog(
            state = state.bucketsPrivacy.isNotEmpty(),
            buckets = state.bucketsPrivacy,
            onEvent = onEvent
        )

    }
}
