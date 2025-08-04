package com.example.lunapic.ui.routing

object AppNavigationActions {
    object PopBack : NavigationAction {
        override val destination: Routes = Routes.PopBack
    }
    object HomeScreen {
        fun homeScreenToMediasScreen(bucketName: String) = object : NavigationAction {
            override val destination: Routes = Routes.MediaListScreenRoute(bucketName)
        }
    }
}