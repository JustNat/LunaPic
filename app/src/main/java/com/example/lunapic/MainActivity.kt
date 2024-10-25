package com.example.lunapic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.util.DebugLogger
import com.example.lunapic.ui.components.main.BucketList
import com.example.lunapic.ui.components.media.MediaList
import com.example.lunapic.ui.Routes
import com.example.lunapic.ui.theme.LunaPicTheme
import com.example.lunapic.viewmodels.BucketListViewModel
import com.example.lunapic.viewmodels.MediaListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO("INCLUIR GIFS)
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
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Routes.BucketListRoute) {
                        composable<Routes.BucketListRoute> {
                            val bucketListViewModel = hiltViewModel<BucketListViewModel>()
                            val state by bucketListViewModel.state.collectAsStateWithLifecycle()
                            val snackBar = bucketListViewModel.snackBarHostState
                            BucketList(
                                state = state,
                                onEvent = bucketListViewModel::onEvent,
                                snackBar = snackBar,
                                onBucketClick = { navController.navigate(Routes.MediaListRoute(it)) }
                            )
                        }
                        composable<Routes.MediaListRoute> {
                            val args = it.toRoute<Routes.MediaListRoute>()
                            val mediaListViewModel: MediaListViewModel =
                                hiltViewModel<MediaListViewModel, MediaListViewModel.Factory>(
                                    creationCallback = { factory ->
                                        factory.create(bucketName = args.bucketName) }
                                )
                            val state by mediaListViewModel.state.collectAsStateWithLifecycle()
                            MediaList(state)
                        }
                    }
                }
            }
        }
    }
}