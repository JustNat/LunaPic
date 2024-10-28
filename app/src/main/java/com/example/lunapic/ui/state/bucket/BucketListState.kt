package com.example.lunapic.ui.state.bucket

import androidx.compose.material3.SnackbarHostState
import aws.sdk.kotlin.services.s3.model.Bucket
import com.example.lunapic.repository.db.data.Bucket as MyBucket

data class BucketListState(
    val buckets : List<Bucket> = emptyList(),
    val selectedBucket : Int = -1,
    val isDeleteBucketDialogOpen : Boolean = false,
    val createBucketForm: CreateBucketForm = CreateBucketForm(),
    val supportText : String = "",
    val isError : Boolean = false,
    val snackBarHost : SnackbarHostState = SnackbarHostState(),
    val isSetBucketsPrivacyDialog: Boolean = false,
    val bucketsPrivacy: List<MyBucket> = emptyList()
)
