package com.example.lunapic.domain.entities

data class AllAndPendingSyncBuckets(
    val buckets: List<Bucket>,
    val bucketsToSync: List<OutOfAppCreatedBucketDTO>?
)
