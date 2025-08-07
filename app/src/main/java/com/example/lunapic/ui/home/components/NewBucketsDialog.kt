package com.example.lunapic.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.lunapic.domain.entities.OutOfAppCreatedBucketDTO
import com.example.lunapic.ui.common.MyCustomDialog
import com.example.lunapic.ui.common.MySwitch
import com.example.lunapic.ui.home.state.HomeScreenEvents

@Composable
fun NewBucketsDialog(buckets: List<OutOfAppCreatedBucketDTO>?, onEvent: (HomeScreenEvents) -> Unit) {
    val isBucketListEmpty = buckets?.isEmpty() ?: true
    if (!isBucketListEmpty) {
        MyCustomDialog(onDismissRequest = {}) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Text(text = "Os seguintes buckets foram cadastrados fora do âmbito do app, defina a privacidade de cada um.")
                }
                itemsIndexed(buckets) { index: Int, bucket: OutOfAppCreatedBucketDTO ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .clipToBounds()
                                .weight(1f),
                            text = bucket.name,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )
                        MySwitch(
                            value = bucket.isPrivate,
                            onCheckedChange = { onEvent(HomeScreenEvents.SetBucketPrivacy(index)) }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    onClick = { onEvent(HomeScreenEvents.RegisterNewBuckets) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Confirmar")
                }
            }
        }
    }
}
