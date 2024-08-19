package com.example.lunapic.ui.components

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import aws.sdk.kotlin.services.s3.model.Bucket
import com.example.lunapic.aws.methods.deleteBuckt
import com.example.lunapic.ui.theme.LunaPicTheme
import kotlinx.coroutines.launch

@Composable
fun BucketList(bucketsResponse : List<Bucket>, onBucketDelete: () -> Unit) {
    val isDialogOpen = remember { mutableStateOf(false) }
    val selectedItem = remember { mutableIntStateOf(-1) }
    val scope = rememberCoroutineScope()

    if (bucketsResponse.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(Dp(6f)),
            verticalArrangement = Arrangement.spacedBy(Dp(6f))
        ) {
            items(bucketsResponse.size) {
                Card(
                    modifier = Modifier.size(60.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Dp(6f)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    )
                    {
                        Text(
                            text = bucketsResponse[it].name.toString(),
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .sizeIn(maxWidth = 145.dp)
                        )
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Excluir bucket",
                            modifier = Modifier
                                .size(24.dp)
                                .selectable(
                                    selected = selectedItem.intValue == it,
                                    onClick = {
                                        selectedItem.intValue = it
                                        isDialogOpen.value = true
                                    }
                                )
                        )
                    }
                }
            }
        }
        if (selectedItem.intValue != -1) {
            MyAlertDialog(
                isDialogOpen = isDialogOpen.value,
                title = "Atenção",
                text = "Deseja mesmo excluir o bucket ${bucketsResponse[selectedItem.intValue].name}?",
                negativeLabel = "Cancelar",
                positiveLabel = "Confirmar",
                onDismissRequest = { isDialogOpen.value = false },
                onConfirmation = {
                    try {
                        scope.launch {
                            deleteBuckt(bucketsResponse[selectedItem.intValue].name ?: "")
                        }
                        isDialogOpen.value = false
                    } catch (e: Exception) {
                        Log.e("BucketList", e.localizedMessage ?: "")
                    }
                }
            )
        }
    } else {
        Column(
            modifier= Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Não há buckets cadastrados")
        }
    }
}

@Composable
@Preview
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DarkMode"
)
fun BucketListPreview() {
    LunaPicTheme {
        Surface {
            BucketList(bucketsResponse = listOf()) {}
        }
    }
}