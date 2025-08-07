package com.example.lunapic.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lunapic.data.repositories.storage.models.InternalBucket

@Entity
data class Bucket(
    @PrimaryKey
    val name: String,

    @ColumnInfo(name = "is_private")
    val isPrivate: Boolean,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

fun Bucket.toInternalBucket(): InternalBucket {
    return InternalBucket(name = this.name, isPrivate = this.isPrivate)
}