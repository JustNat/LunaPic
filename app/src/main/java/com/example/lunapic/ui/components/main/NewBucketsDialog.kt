package com.example.lunapic.ui.components.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lunapic.repository.db.data.Bucket
import com.example.lunapic.ui.components.shared.MyCustomDialog
import com.example.lunapic.ui.components.shared.MySwitch
import com.example.lunapic.ui.state.bucket.BucketListEvent
import java.time.OffsetDateTime

@Composable
fun NewBucketsDialog(buckets: List<Bucket>, onEvent: (BucketListEvent) -> Unit) {
    MyCustomDialog(onDismissRequest = {}) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Text(text = "Os seguintes novos buckets foram registrado, defina-os como privados ou não:")
            }
            items(buckets.size) { index: Int ->
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
                        text = buckets[index].name,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                    MySwitch(
                        value = buckets[index].isPrivate,
                        onCheckedChange = { onEvent(BucketListEvent.SetBucketPrivacy(index)) }
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
                onClick = { onEvent(BucketListEvent.SetBucketsPrivacyDialogState(false)) },
                modifier = Modifier.padding(8.dp),
            ) {
                Text("Cancelar")
            }
            TextButton(
                onClick = { onEvent(BucketListEvent.RegisterBucketsPrivacy) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Confirmar")
            }
        }
    }
}

@Composable
@Preview
@Preview("darkTheme", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun NewBucketsDialogPreview() {

    val buckets = listOf(
        Bucket(name = "teste", isPrivate = false, lastUpdatedAt = OffsetDateTime.now()),
        Bucket(name = "boga", isPrivate = false, lastUpdatedAt = OffsetDateTime.now()),
        Bucket(name = "tonho", isPrivate = false, lastUpdatedAt = OffsetDateTime.now()),
        Bucket(
            name = "gplaysdoceuribeirodasilvaaraelebruceebavamosimboramachoebaaaaaa",
            isPrivate = false,
            lastUpdatedAt = OffsetDateTime.now()
        )
    )

     NewBucketsDialog(buckets = buckets, onEvent = {})
}