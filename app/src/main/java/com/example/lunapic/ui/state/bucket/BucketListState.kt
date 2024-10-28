package com.example.lunapic.ui.state.bucket

import androidx.compose.material3.SnackbarHostState
import aws.sdk.kotlin.services.s3.model.Bucket

data class BucketListState(
    val buckets : List<Bucket> = emptyList(),
    val selectedBucket : Int = -1,
    val isDeleteBucketDialogOpen : Boolean = false,
    val createBucketForm: CreateBucketForm = CreateBucketForm(),
    val setPrivacyBucketForm: SetPrivacyBucketForm = SetPrivacyBucketForm(),
    val snackBarHost : SnackbarHostState = SnackbarHostState(),
)
