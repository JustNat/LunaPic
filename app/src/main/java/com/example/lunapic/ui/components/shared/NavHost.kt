package com.example.lunapic.ui.components.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.lunapic.ui.components.main.BucketList
import com.example.lunapic.ui.components.media.MediaList
import com.example.lunapic.ui.navigation.Navigator
import com.example.lunapic.ui.navigation.Routes
import com.example.lunapic.viewmodels.BucketListViewModel
import com.example.lunapic.viewmodels.MediaListViewModel

@Composable
fun MyNavHost(
    navHostController: NavHostController = rememberNavController(),
    navigator: Navigator
) {
    val navigatorState by navigator.actions.collectAsStateWithLifecycle()

    LaunchedEffect(navigatorState) {
        navigatorState?.let {
            when (it.destination) {
                is Routes.PopBack -> {
                    navHostController.popBackStack()
                }

                else -> {
                    it.parcelableArguments.forEach { arg ->
                        navHostController.currentBackStackEntry?.arguments?.putParcelable(
                            arg.key,
                            arg.value
                        )
                    }
                    navHostController.navigate(it.destination, it.navOptions)
                }
            }
        }
    }

    NavHost(navController = navHostController, startDestination = Routes.BucketListRoute) {
        composable<Routes.BucketListRoute> {
            val bucketListViewModel = hiltViewModel<BucketListViewModel>()
            val state by bucketListViewModel.state.collectAsStateWithLifecycle()
            BucketList(
                state = state,
                onEvent = bucketListViewModel::onEvent,
            )
        }
        composable<Routes.MediaListRoute> {
            val args = it.toRoute<Routes.MediaListRoute>()
            val mediaListViewModel: MediaListViewModel =
                hiltViewModel<MediaListViewModel, MediaListViewModel.Factory>(
                    creationCallback = { factory ->
                        factory.create(bucketName = args.bucketName)
                    }
                )
            val state by mediaListViewModel.state.collectAsStateWithLifecycle()
            MediaList(state, mediaListViewModel.bucketName, onEvent = mediaListViewModel::onEvent)
        }
    }

}