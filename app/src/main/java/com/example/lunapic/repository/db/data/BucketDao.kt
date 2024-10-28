package com.example.lunapic.repository.db.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.time.OffsetDateTime

@Dao
interface BucketDao {
    @Insert
    suspend fun insertBucket(bucket: Bucket)

    @Query("DELETE FROM bucket WHERE name = :bucketName")
    suspend fun deleteBucket(bucketName: String)

    @Query("UPDATE bucket SET last_updated_at = :offset WHERE name = :bucketName")
    suspend fun updateLastUpdatedAt(bucketName: String, offset: OffsetDateTime)

    @Query("SELECT COUNT(*) FROM bucket WHERE name = :bucketName")
    suspend fun isBucketRegistered(bucketName: String) : Int

    @Update(entity = Bucket::class)
    suspend fun updateBucket(bucket: Bucket)
}