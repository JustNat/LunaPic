package com.example.lunapic.data.repositories.network
import aws.sdk.kotlin.services.s3.model.DeleteObjectsResponse
import com.example.lunapic.data.repositories.network.aws.models.CloudBucket
import com.example.lunapic.data.repositories.network.aws.models.Media

interface CloudStorageRepository {
    suspend fun createBucket(name: String)
    suspend fun deleteBucket(bucketName: String)
    suspend fun listBuckets(): List<CloudBucket>
    suspend fun getObjects(bucketName: String) : List<Media>
    suspend fun deleteObjects(bucketName: String, keys: List<String>) : DeleteObjectsResponse
}