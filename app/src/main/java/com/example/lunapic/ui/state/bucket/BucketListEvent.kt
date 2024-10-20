package com.example.lunapic.ui.state.bucket

import aws.sdk.kotlin.services.s3.model.Bucket

sealed interface BucketListEvent {
    data object CreateBucket: BucketListEvent
    data class SetBucketName(val bucketName: String) : BucketListEvent
    data class SetIsPrivate(val isPrivate: Boolean) : BucketListEvent
    data class SetSelectedBucket(val selectedBucket: Int) : BucketListEvent
    data class DeleteBucket(val bucket: Bucket) : BucketListEvent
    data class SetCreateDialogState(val state: Boolean) : BucketListEvent
    data class SetDeleteDialogState(val state: Boolean) : BucketListEvent
}