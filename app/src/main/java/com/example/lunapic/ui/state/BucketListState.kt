package com.example.lunapic.ui.state

import aws.sdk.kotlin.services.s3.model.Bucket

data class BucketListState(
    val buckets : List<Bucket> = emptyList(),
    val selectedBucket : Int = -1,
    val isDeleteBucketDialogOpen : Boolean = false,
    val isCreateBucketDialogOpen : Boolean = false,
    val bucketForm: BucketForm = BucketForm(),
    val supportText : String = "",
    val isError : Boolean = false
)
