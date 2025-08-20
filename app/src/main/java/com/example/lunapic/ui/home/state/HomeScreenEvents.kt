package com.example.lunapic.ui.home.state

sealed interface HomeScreenEvents {
    data object CreateBucket: HomeScreenEvents
    data class SetBucketName(val bucketName: String) : HomeScreenEvents
    data class SetIsPrivate(val isPrivate: Boolean) : HomeScreenEvents
    data class SetSelectedBucket(val selectedBucket: String?) : HomeScreenEvents
    data class DeleteBucket(val bucketName: String) : HomeScreenEvents
    data class SetCreateDialogState(val state: Boolean) : HomeScreenEvents
    data class SetDeleteDialogState(val state: Boolean) : HomeScreenEvents
    data class NavigateToMediaScreen(val bucketName: String) : HomeScreenEvents
    data class SetBucketPrivacy(val bucketName: String) : HomeScreenEvents
    data object RegisterNewBuckets : HomeScreenEvents
}