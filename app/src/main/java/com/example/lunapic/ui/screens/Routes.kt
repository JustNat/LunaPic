package com.example.lunapic.ui.screens

import kotlinx.serialization.Serializable

object Routes {
    @Serializable
    data object BucketListRoute

    @Serializable
    data class MediaListRoute(val bucketName: String)
}

