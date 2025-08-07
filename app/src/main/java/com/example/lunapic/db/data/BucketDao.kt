package com.example.lunapic.db.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import java.time.OffsetDateTime

@Dao
interface BucketDao {

    @Query("SELECT * FROM bucket")
    suspend fun getBuckets(): List<Bucket>

    @Query("SELECT is_private FROM bucket WHERE name = :bucketName")
    suspend fun isBucketPrivate(bucketName: String): Boolean

    @Insert
    suspend fun insertBucket(bucket: Bucket)

    @Query("DELETE FROM bucket WHERE name = :bucketName")
    suspend fun deleteBucket(bucketName: String)

    @Query("UPDATE bucket SET last_updated_at = :offset WHERE name = :bucketName")
    suspend fun updateLastUpdatedAt(bucketName: String, offset: OffsetDateTime)

    @Transaction
    suspend fun insertBuckets(buckets: List<Bucket>) {
        buckets.forEach { bucket -> insertBucket(bucket) }
    }
}