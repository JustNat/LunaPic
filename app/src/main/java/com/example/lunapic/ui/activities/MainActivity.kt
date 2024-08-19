package com.example.lunapic.ui.activities

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import aws.sdk.kotlin.services.s3.model.Bucket
import com.example.lunapic.aws.methods.listBuckts
import com.example.lunapic.ui.components.BucketList
import com.example.lunapic.ui.components.CreateBucketDialog
import com.example.lunapic.ui.components.Greeting
import com.example.lunapic.ui.components.MainScaffold
import com.example.lunapic.ui.theme.LunaPicTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LunaPicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App() {
    val isDialogOpen = remember { mutableStateOf(false) }
    val buckets = remember { mutableStateListOf<Bucket>() }
    val bucketInteraction = remember { mutableIntStateOf(0) }

    LaunchedEffect(bucketInteraction.intValue) {
        listBuckts().forEach {
            if (!buckets.contains(it)) {
                buckets.add(it)
            }
        }
    }

    if (isDialogOpen.value) {
        MainScaffold(onFabClick = { isDialogOpen.value = true }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
            ) {
                Greeting(name = "Gabriel", vpadding = 6.dp)
                BucketList(bucketsResponse = buckets, onBucketDelete = { bucketInteraction.intValue })
            }
            CreateBucketDialog(onDisMissRequest = { isDialogOpen.value = false }) {
                bucketInteraction.intValue++
            }
        }
    } else {
        MainScaffold(onFabClick = { isDialogOpen.value = true }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
            ) {
                Greeting(name = "Gabriel", vpadding = 6.dp)
                BucketList(bucketsResponse = buckets, onBucketDelete = { bucketInteraction.intValue })
            }
        }
    }
}

@Preview("Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview("Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AppPreview() {
    LunaPicTheme {
        Surface {
            App()
        }
    }
}