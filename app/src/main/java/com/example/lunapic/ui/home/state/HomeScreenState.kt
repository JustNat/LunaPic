package com.example.lunapic.ui.home.state

import androidx.compose.material3.SnackbarHostState
import com.example.lunapic.domain.entities.Bucket
import com.example.lunapic.domain.entities.OutOfAppCreatedBucketDTO
import com.example.lunapic.ui.home.state.components.CreateBucketForm

data class HomeScreenState(
    val buckets : List<Bucket> = emptyList(),
    val selectedBucket : Int = -1,
    val isDeleteBucketDialogOpen : Boolean = false,
    val createBucketForm: CreateBucketForm = CreateBucketForm(),
    val bucketsCreatedOutsideTheApp: List<OutOfAppCreatedBucketDTO>? = null,
    val snackBarHost : SnackbarHostState = SnackbarHostState(),
)
