package com.example.lunapic.ui

import kotlinx.serialization.Serializable

object Routes {
    @Serializable
    data object BucketListRoute

    @Serializable
    data class MediaListRoute(val bucketName: String)
}

