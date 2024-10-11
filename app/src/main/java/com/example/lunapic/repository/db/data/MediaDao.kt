package com.example.lunapic.repository.db.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.time.OffsetDateTime

@Dao
interface MediaDao {

    @Insert
    suspend fun insertMedia(media: Media)

    @Delete
    suspend fun deleteMedia(media: Media)

    @Query("SELECT name FROM media WHERE bucket = :bucketName")
    suspend fun getMediaNamesFromBucket(bucketName: String): List<String>
}