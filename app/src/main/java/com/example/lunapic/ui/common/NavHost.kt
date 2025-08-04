package com.example.lunapic.ui.common

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
import com.example.lunapic.ui.routing.Navigator
import com.example.lunapic.ui.routing.Routes
import com.example.lunapic.ui.home.HomeScreen
import com.example.lunapic.ui.home.HomeViewModel
import com.example.lunapic.ui.media.MediaListScreen
import com.example.lunapic.ui.media.MediaListViewModel

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

    NavHost(navController = navHostController, startDestination = Routes.HomeScreenRoute) {
        composable<Routes.HomeScreenRoute> {
            val bucketListViewModel = hiltViewModel<HomeViewModel>()
            val state by bucketListViewModel.state.collectAsStateWithLifecycle()
            HomeScreen(
                state = state,
                onEvent = bucketListViewModel::onEvent,
            )
        }
        composable<Routes.MediaListScreenRoute> {
            val args = it.toRoute<Routes.MediaListScreenRoute>()
            val mediaListViewModel: MediaListViewModel =
                hiltViewModel<MediaListViewModel, MediaListViewModel.Factory>(
                    creationCallback = { factory ->
                        factory.create(bucketName = args.bucketName)
                    }
                )
            val state by mediaListViewModel.state.collectAsStateWithLifecycle()
            MediaListScreen(state, mediaListViewModel.bucketName, onEvent = mediaListViewModel::onEvent)
        }
    }

}