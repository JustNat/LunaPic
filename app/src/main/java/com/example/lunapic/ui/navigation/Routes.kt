package com.example.lunapic.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {
    data object PopBack : Routes

    @Serializable
    data object BucketListRoute : Routes

    @Serializable
    data class MediaListRoute(val bucketName: String) : Routes
}

