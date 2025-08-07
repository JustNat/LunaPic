package com.example.lunapic.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.lunapic.domain.entities.Bucket
import com.example.lunapic.ui.common.MyAlertDialog
import com.example.lunapic.ui.home.components.CreateBucketDialog
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
        Column(modifier = Modifier.fillMaxSize()) {
            if (state.buckets.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(Dp(6f)),
                    verticalArrangement = Arrangement.spacedBy(Dp(6f)),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.padding(it)
                ) {
                    itemsIndexed(state.buckets) { index: Int, bucket: Bucket ->
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
                                    text = bucket.name,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .sizeIn(maxWidth = 145.dp)
                                        .clickable {
                                            onEvent(
                                                HomeScreenEvents.NavigateToMediaScreen(
                                                    bucket.name
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
                    MyAlertDialog(
                        isDialogOpen = state.isDeleteBucketDialogOpen,
                        title = "Atenção",
                        text = "Deseja mesmo excluir o bucket ${state.buckets[state.selectedBucket].name}?",
                        negativeLabel = "Cancelar",
                        positiveLabel = "Confirmar",
                        onDismissRequest = {
                            onEvent(HomeScreenEvents.SetDeleteDialogState(false))
                            onEvent(HomeScreenEvents.SetSelectedBucket(-1))
                        }
                    ) {
                        onEvent(HomeScreenEvents.DeleteBucket(state.buckets[state.selectedBucket].name))
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
        CreateBucketDialog(state = state.createBucketForm, onEvent = onEvent)
        NewBucketsDialog(
            buckets = state.bucketsCreatedOutsideTheApp,
            onEvent = onEvent
        )

    }
}
