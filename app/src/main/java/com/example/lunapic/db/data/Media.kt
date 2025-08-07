package com.example.lunapic.db.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["name", "bucket"],
    foreignKeys = [ForeignKey(
        entity = Bucket::class,
        parentColumns = ["name"],
        childColumns = ["bucket"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Media(
    val name: String,
    val bucket: String
)
