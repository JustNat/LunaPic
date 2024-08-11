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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lunapic.aws.methods.listBuckts
import com.example.lunapic.ui.components.BucketList
import com.example.lunapic.ui.components.CreateBucketDialog
import com.example.lunapic.ui.components.Greeting
import com.example.lunapic.ui.components.MainScaffold
import com.example.lunapic.ui.theme.LunaPicTheme
import kotlinx.coroutines.runBlocking

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

    val buckets = runBlocking {
        mutableStateOf(listBuckts())
    }

    if (isDialogOpen.value) {
        MainScaffold(onFabClick = { isDialogOpen.value = true })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            Greeting(name = "Gabriel", vpadding = 6.dp)
            BucketList(bucketsResponse = buckets.value)
        }
        CreateBucketDialog {
            isDialogOpen.value = false
            runBlocking {
                buckets.value = listBuckts()
            }
        }
    } else {
        MainScaffold(onFabClick = { isDialogOpen.value = true })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            Greeting(name = "Gabriel", vpadding = 6.dp)
            BucketList(bucketsResponse = buckets.value)
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