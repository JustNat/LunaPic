package com.example.lunapic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.util.DebugLogger
import com.example.lunapic.ui.components.shared.MyNavHost
import com.example.lunapic.ui.navigation.Navigator
import com.example.lunapic.ui.theme.LunaPicTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SingletonImageLoader.setSafe {
            ImageLoader.Builder(applicationContext)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .memoryCache {
                    MemoryCache.Builder()
                        .weakReferencesEnabled(true)
                        .maxSizePercent(applicationContext, 0.25)
                        .build()
                }
                .diskCachePolicy(CachePolicy.DISABLED)
                .logger(if (BuildConfig.COIL_DEBUGGER) DebugLogger() else null)
                .build()
        }
        setContent {
            LunaPicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyNavHost(navigator = navigator)
                }
            }
        }
    }
}