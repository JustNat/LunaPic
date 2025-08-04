package com.example.lunapic.ui.routing

import kotlinx.serialization.Serializable

sealed interface Routes {
    data object PopBack : Routes

    @Serializable
    data object HomeScreenRoute : Routes

    @Serializable
    data class MediaListScreenRoute(val bucketName: String) : Routes
}

