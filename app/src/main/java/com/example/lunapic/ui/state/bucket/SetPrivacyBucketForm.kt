package com.example.lunapic.ui.state.bucket

import com.example.lunapic.repository.db.data.Bucket

data class SetPrivacyBucketForm(
    val isSetBucketsPrivacyDialog: Boolean = false,
    val bucketsPrivacy: List<Bucket> = emptyList()
)
