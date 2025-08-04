package com.example.lunapic.repositories.network

import aws.sdk.kotlin.services.s3.model.Bucket
import aws.sdk.kotlin.services.s3.model.DeleteObjectsResponse
import com.example.lunapic.repositories.network.aws.data.Media

interface CloudStorageRepository {
    suspend fun createBucket(name: String)
    suspend fun deleteBucket(bucketName: String)
    suspend fun listBuckets(): List<Bucket>
    suspend fun getObjects(bucketName: String) : List<Media>
    suspend fun deleteObjects(bucketName: String, keys: List<String>) : DeleteObjectsResponse
}