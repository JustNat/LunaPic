package com.example.lunapic.repository.db.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface BucketDao {
    @Insert
    suspend fun insertBucket(bucket: Bucket)

    @Delete
    suspend fun deleteBucket(bucket: Bucket)

    @Query("SELECT * FROM bucket")
    suspend fun getBuckets() : List<Bucket>

    @Transaction
    @Query("SELECT * FROM bucket")
    suspend fun getBucketsWithMedias(): List<BucketWithMedias>

    @Query("UPDATE bucket SET last_updated_at = CURRENT_TIMESTAMP WHERE name = :bucketName")
    suspend fun updateLastUpdatedAt(bucketName: String)
}