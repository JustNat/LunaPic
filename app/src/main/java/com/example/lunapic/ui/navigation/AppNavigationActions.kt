package com.example.lunapic.ui.navigation

object AppNavigationActions {
    object PopBack : NavigationAction {
        override val destination: Routes = Routes.PopBack
    }
    object BucketsScreen {
        fun bucketScreenToMediasScreen(bucketName: String) = object : NavigationAction {
            override val destination: Routes = Routes.MediaListRoute(bucketName)
        }
    }
}