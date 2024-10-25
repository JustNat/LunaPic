package com.example.lunapic.ui.navigation

object AppNavigationActions {
    object BucketsScreen {
        fun bucketScreenToMediasScreen(bucketName: String) = object : NavigationAction {
            override val destination: Routes = Routes.MediaListRoute(bucketName)
        }
    }
}