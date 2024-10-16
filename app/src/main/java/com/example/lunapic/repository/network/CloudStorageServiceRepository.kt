package com.example.lunapic.repository.network

import aws.sdk.kotlin.services.s3.model.Bucket

interface CloudStorageServiceRepository {
    suspend fun createBucket(name: String)
    suspend fun deleteBucket(bucketName: String)
    suspend fun listBuckets(): List<Bucket>
    suspend fun getObjects(bucketName: String)
}