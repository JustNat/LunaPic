package com.example.lunapic

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
    val buckets = runBlocking {
        listBuckts()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dp(6f)),
    ) {
        Greeting(name = "Gabriel")
        BucketList(bucketsResponse = buckets)
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