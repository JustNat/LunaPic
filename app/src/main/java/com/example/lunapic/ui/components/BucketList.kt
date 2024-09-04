package com.example.lunapic.ui.components

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
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import aws.sdk.kotlin.services.s3.model.Bucket
import com.example.lunapic.ui.theme.LunaPicTheme
import com.example.lunapic.viewmodels.BucketListViewModel

@Composable
fun BucketList(
    createBucketDialogOpen: Boolean,
    closeCreateBucketDialog: () -> Unit,
    onCreatedBucket: () -> Unit,
    onDeletedBucket: () -> Unit,
    onErrorCreatingBucket: (Exception) -> Unit,
    onErrorDeletingBucket: (Exception) -> Unit
) {
    val bucketListViewModel: BucketListViewModel = viewModel()
    val isDeleteBucketDialogOpen by bucketListViewModel.isDeleteBucketDialogOpen.collectAsStateWithLifecycle()
    val buckets by bucketListViewModel.buckts.collectAsStateWithLifecycle()
    val selectedItem by bucketListViewModel.selectedItem.collectAsStateWithLifecycle()
    val bucketName by bucketListViewModel.bucketName.collectAsStateWithLifecycle()
    val supportText by bucketListViewModel.supportText.collectAsStateWithLifecycle()
    val isError by bucketListViewModel.isError.collectAsStateWithLifecycle()

    if (buckets.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(Dp(6f)),
            verticalArrangement = Arrangement.spacedBy(Dp(6f))
        ) {
            itemsIndexed(buckets) { index: Int, _: Bucket ->
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
                            text = buckets[index].name.toString(),
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.sizeIn(maxWidth = 145.dp)
                        )
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Excluir bucket",
                            modifier = Modifier
                                .size(24.dp)
                                .selectable(selected = selectedItem == index, onClick = {
                                    bucketListViewModel.changeSelectedItem(index)
                                    bucketListViewModel.changeDeleteBucketDialogState(true)
                                })
                        )
                    }
                }
            }
        }
        if (selectedItem != -1) {
            MyAlertDialog(isDialogOpen = isDeleteBucketDialogOpen,
                title = "Atenção",
                text = "Deseja mesmo excluir o bucket ${buckets[selectedItem].name}?",
                negativeLabel = "Cancelar",
                positiveLabel = "Confirmar",
                onDismissRequest = {
                    bucketListViewModel.changeDeleteBucketDialogState(false)
                    bucketListViewModel.changeSelectedItem(-1)
                }
            ) {
                bucketListViewModel.deleteBucket(
                    buckets[selectedItem],
                    onSuccess = { onDeletedBucket() },
                    onError = { onErrorDeletingBucket(it) }
                )
                bucketListViewModel.changeDeleteBucketDialogState(false)
            }
        }
        if (createBucketDialogOpen) {
            MyCustomDialog(onDismissRequest = {
                closeCreateBucketDialog()
                bucketListViewModel.onCreateBucketDialogClosed()
            }, content = {
                OutlinedTextField(
                    value = bucketName,
                    onValueChange = { bucketListViewModel.changeBucketName(it.trim()) },
                    label = { Text(text = "Nome do bucket") },
                    modifier = Modifier.padding(16.dp),
                    supportingText = {
                        Text(text = supportText)
                    },
                    isError = isError
                )
                MySwitch("Bucket privado")
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = {
                            closeCreateBucketDialog()
                            bucketListViewModel.onCreateBucketDialogClosed()
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Cancelar")
                    }
                    TextButton(
                        onClick = {
                            bucketListViewModel.createBucket(
                                bucketName,
                                onSuccess = { onCreatedBucket() },
                                onError = { onErrorCreatingBucket(it) }
                            )
                            closeCreateBucketDialog()
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


@Composable
@Preview
fun BucketListPreview() {
    LunaPicTheme {
        Surface {
            BucketList(createBucketDialogOpen = false,
                closeCreateBucketDialog = {},
                onCreatedBucket = {},
                onDeletedBucket = {},
                onErrorCreatingBucket = {},
                onErrorDeletingBucket = {}
            )
        }
    }
}
