package com.example.lunapic.repository.network.aws

import android.content.Context
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.Bucket
import aws.sdk.kotlin.services.s3.model.BucketLocationConstraint
import aws.sdk.kotlin.services.s3.model.CreateBucketConfiguration
import aws.sdk.kotlin.services.s3.model.CreateBucketRequest
import aws.sdk.kotlin.services.s3.model.DeleteBucketRequest
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.ListObjectsV2Request
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.io.use
import com.example.lunapic.repository.network.CloudStorageServiceRepository
import com.example.lunapic.storage.InternalStorageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class S3Manager @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val internalStorage: InternalStorageRepository
) : AWSCredentials(), CloudStorageServiceRepository {

    private fun buildClient(): S3Client {
        return S3Client {
            credentialsProvider = StaticCredentialsProvider(
                credentials = Credentials(ACCESS_KEY, SECRET_KEY)
            )
            region = "sa-east-1"
        }
    }

    private suspend fun listObjects(bucketName: String) = withContext(Dispatchers.IO) {
        buildClient().use {
            val response = it.listObjectsV2(
                ListObjectsV2Request {
                    bucket = bucketName
                }
            ).contents ?: emptyList()
            response
        }
    }

    override suspend fun createBucket(name: String): Unit = withContext(Dispatchers.IO) {
        buildClient().use {
            it.createBucket(
                CreateBucketRequest {
                    bucket = name
                    createBucketConfiguration = CreateBucketConfiguration {
                        locationConstraint = BucketLocationConstraint.SaEast1
                    }
                }
            )
        }
    }

    override suspend fun deleteBucket(bucketName: String): Unit = withContext(Dispatchers.IO) {
        buildClient().use {
            it.deleteBucket(
                DeleteBucketRequest {
                    bucket = bucketName
                }
            )
        }
    }

    override suspend fun listBuckets(): List<Bucket> = withContext(Dispatchers.IO) {
        buildClient().use {
            val response = it.listBuckets().buckets ?: emptyList()
            response
        }
    }

    override suspend fun getObjects(bucketName: String): Unit = withContext(Dispatchers.IO) {
        val keys = listObjects(bucketName)
        val internalDir = appContext.filesDir
        val directory = File(internalDir, bucketName)

        val filesNames = directory.listFiles()?.map { it.name } ?: emptyList()
        val filteredKeys = keys.filter { filesNames.contains(it.key ?: "") }

        if (filteredKeys.isNotEmpty()) {
            buildClient().use {
                filteredKeys.forEach { obj ->
                    it.getObject(
                        input = GetObjectRequest {
                            bucket = bucketName
                            key = obj.key
                        }
                    ) { response ->
                        response.body?.let { body ->
                            internalStorage.saveMedia(
                                fileName = obj.key ?: "",
                                fileBody = body,
                                bucketName = bucketName,
                                size = obj.size ?: 0
                            )
                        }
                    }
                }
            }
        }
    }

}