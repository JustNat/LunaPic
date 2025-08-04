package com.example.lunapic.ui.home.state

import androidx.compose.material3.SnackbarHostState
import aws.sdk.kotlin.services.s3.model.Bucket
import com.example.lunapic.ui.home.state.components.CreateBucketForm
import com.example.lunapic.repositories.db.data.Bucket as MyBucket

data class HomeScreenState(
    val buckets : List<Bucket> = emptyList(),
    val selectedBucket : Int = -1,
    val isDeleteBucketDialogOpen : Boolean = false,
    val createBucketForm: CreateBucketForm = CreateBucketForm(),
    val bucketsPrivacy: List<MyBucket> = emptyList(),
    val snackBarHost : SnackbarHostState = SnackbarHostState(),
)
