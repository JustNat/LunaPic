package com.example.lunapic.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import aws.sdk.kotlin.services.s3.model.Bucket
import com.example.lunapic.ui.theme.LunaPicTheme

@Composable
fun BucketList(bucketsResponse : List<Bucket>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(Dp(6f)),
        verticalArrangement = Arrangement.spacedBy(Dp(6f))
    ){
        if (bucketsResponse.isNotEmpty()) {
            items(bucketsResponse.size) {
                Card(
                    modifier = Modifier.size(Dp(60f)),
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
                        )
                    }
                }
            }
        } else {
            item {
                Text(
                    text = "Nenhum bucket cadastrado.",
                    textAlign = TextAlign.Center
                )
            }
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
    val previewBucketList = listOf(
            Bucket {name = "Bucket1"},
            Bucket {name = "Bucket2"},
            Bucket {name = "Bucket3"},
            Bucket {name = "Bucket4"}
        )
    LunaPicTheme {
        Surface {
            BucketList(bucketsResponse = previewBucketList)
        }
    }
}