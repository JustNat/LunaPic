package com.example.lunapic.repository.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity
data class Bucket(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "is_private", defaultValue = "0") val isPrivate: Boolean,
    @ColumnInfo(name = "last_updated_at", defaultValue = "CURRENT_TIMESTAMP") val lastUpdatedAt : OffsetDateTime
)
