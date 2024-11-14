package com.example.lunapic.repository.network

import aws.sdk.kotlin.services.s3.model.Bucket
import aws.sdk.kotlin.services.s3.model.DeleteObjectsResponse
import com.example.lunapic.repository.network.aws.data.Media

interface CloudStorageServiceRepository {
    suspend fun createBucket(name: String)
    suspend fun deleteBucket(bucketName: String)
    suspend fun listBuckets(): List<Bucket>
    suspend fun getObjects(bucketName: String) : List<Media>
    suspend fun deleteObjects(bucketName: String, keys: List<String>) : DeleteObjectsResponse
}