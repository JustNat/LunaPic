package com.example.lunapic.domain.entities

import com.example.lunapic.db.data.Bucket

data class OutOfAppCreatedBucketDTO(
    val name: String, val isPrivate: Boolean = false
) {
    fun formatToDbBucket(): Bucket {
        return Bucket(this.name, this.isPrivate)
    }
}