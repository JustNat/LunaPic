package com.example.lunapic.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import aws.sdk.kotlin.services.s3.model.Bucket
import com.example.lunapic.aws.methods.deleteBuckt
import com.example.lunapic.aws.methods.listBuckts
import com.example.lunapic.ui.theme.LunaPicTheme
import kotlinx.coroutines.launch

@Composable
fun BucketList(
    createBucketDialogOpen: Boolean,
    changeCreateBucketDialogState: () -> Unit,
    onCreatedBucket: () -> Unit,
    onDeletedBucket: () -> Unit,
    onErrorDeletingBucket: (Exception) -> Unit
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val bucketsResponse = remember { mutableStateListOf<Bucket>() }
    var isDeleteBucketDialogOpen by remember { mutableStateOf(false) }
    var bucketCreated by remember { mutableIntStateOf(0) }
    var bucketDeleted by remember { mutableIntStateOf(0) }

    LaunchedEffect(bucketDeleted) {
        selectedItem = -1
        if (bucketsResponse.isNotEmpty()) {
            bucketsResponse.clear()
            bucketCreated++
        }
    }

    LaunchedEffect(bucketCreated) {
        listBuckts().forEach {
            if (!bucketsResponse.contains(it)) bucketsResponse.add(it)
        }
    }

    if (bucketsResponse.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(Dp(6f)),
            verticalArrangement = Arrangement.spacedBy(Dp(6f))
        ) {
            itemsIndexed(bucketsResponse) { index: Int, _: Bucket ->
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
                            text = bucketsResponse[index].name.toString(),
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.sizeIn(maxWidth = 145.dp)
                        )
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Excluir bucket",
                            modifier = Modifier
                                .size(24.dp)
                                .selectable(selected = selectedItem == index, onClick = {
                                    selectedItem = index
                                    isDeleteBucketDialogOpen = true
                                })
                        )
                    }
                }
            }
        }
        if (selectedItem != -1) {
            MyAlertDialog(isDialogOpen = isDeleteBucketDialogOpen,
                title = "Atenção",
                text = "Deseja mesmo excluir o bucket ${bucketsResponse[selectedItem].name}?",
                negativeLabel = "Cancelar",
                positiveLabel = "Confirmar",
                onDismissRequest = { isDeleteBucketDialogOpen = false },
                onConfirmation = {
                    scope.launch {
                        try {
                            deleteBuckt(bucketsResponse[selectedItem].name ?: "")
                            bucketDeleted++
                            onDeletedBucket()
                        } catch (e: Exception) {
                            onErrorDeletingBucket(e)
                        }
                    }
                    isDeleteBucketDialogOpen = false
                })
        }
        if (createBucketDialogOpen) {
            CreateBucketDialog(onDismissRequest = { changeCreateBucketDialogState() },
                onConfirmation = {
                    bucketCreated++
                    changeCreateBucketDialogState()
                    onCreatedBucket()
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
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES, name = "DarkMode"
)
fun BucketListPreview() {
    LunaPicTheme {
        Surface {
            BucketList(createBucketDialogOpen = false,
                changeCreateBucketDialogState = {},
                onCreatedBucket = {},
                onDeletedBucket = {}) {}
        }
    }
}