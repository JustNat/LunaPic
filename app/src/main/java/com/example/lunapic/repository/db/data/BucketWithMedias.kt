package com.example.lunapic.repository.db.data

import androidx.room.Embedded
import androidx.room.Relation

data class BucketWithMedias(
    @Embedded val bucket: Bucket,
    @Relation(
        parentColumn = "name",
        entityColumn = "bucket"
    )
    val medias : List<Media>
)
