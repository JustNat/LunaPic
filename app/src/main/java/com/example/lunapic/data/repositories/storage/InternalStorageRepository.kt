package com.example.lunapic.data.repositories.storage

import com.example.lunapic.data.repositories.storage.models.InternalBucket
import com.example.lunapic.domain.entities.CreateBucketDTO
import com.example.lunapic.domain.entities.OutOfAppCreatedBucketDTO
import java.io.File

interface InternalStorageRepository {
    suspend fun getBuckets(): List<InternalBucket>
    suspend fun saveMedia(fileName: String, fileBody: ByteArray, bucketName: String, size: Long)
    suspend fun getMedias(bucketName: String) : List<File>
    suspend fun createBucket(data : CreateBucketDTO)
    suspend fun deleteBucket(bucketName: String)
    suspend fun reverseDeleteBucketOperation()
    suspend fun registerBucketsCreatedOutsideTheApp(buckets: List<OutOfAppCreatedBucketDTO>)
    suspend fun deleteMedia(file: File)
}