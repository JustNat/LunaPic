package com.example.lunapic.repositories.network.aws

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.Bucket
import aws.sdk.kotlin.services.s3.model.BucketLocationConstraint
import aws.sdk.kotlin.services.s3.model.CreateBucketConfiguration
import aws.sdk.kotlin.services.s3.model.CreateBucketRequest
import aws.sdk.kotlin.services.s3.model.Delete
import aws.sdk.kotlin.services.s3.model.DeleteBucketRequest
import aws.sdk.kotlin.services.s3.model.DeleteObjectsRequest
import aws.sdk.kotlin.services.s3.model.DeleteObjectsResponse
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.ListObjectsV2Request
import aws.sdk.kotlin.services.s3.model.ObjectIdentifier
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.content.toByteArray
import aws.smithy.kotlin.runtime.io.use
import aws.smithy.kotlin.runtime.net.url.Url
import com.example.lunapic.BuildConfig
import com.example.lunapic.repositories.network.CloudStorageRepository
import com.example.lunapic.repositories.network.aws.data.Media
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class S3Repository @Inject constructor() : CloudStorageRepository {

    private fun buildClient(): S3Client {
        return S3Client {
            credentialsProvider = StaticCredentialsProvider(
                credentials = Credentials(
                    BuildConfig.AWS_ACCESS_KEY,
                    BuildConfig.AWS_SECRET_ACCESS_KEY
                )
            )
            endpointUrl = Url.parse(BuildConfig.AWS_ENDPOINT)
            region = "sa-east-1"
            forcePathStyle = BuildConfig.FORCED_PATH_STYLE
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

    override suspend fun getObjects(bucketName: String): List<Media> = withContext(Dispatchers.IO) {
        val keys = async { listObjects(bucketName) }.await()
        val byteStreams = mutableListOf<Media>()

        buildClient().use { client ->
            keys.map { obj ->
                async {
                    client.getObject(input = GetObjectRequest {
                        bucket = bucketName
                        key = obj.key
                    }) { response ->
                        response.body?.let { body ->
                            response.metadata?.keys
                            byteStreams.add(
                                Media(
                                    name = obj.key ?: "",
                                    body = body.toByteArray(),
                                    size = obj.size ?: 0,
                                    bucket = bucketName
                                )
                            )
                        }
                    }
                }
            }.mapNotNull { it.await() }
        }
        byteStreams
    }

    override suspend fun deleteObjects(bucketName: String, keys: List<String>) : DeleteObjectsResponse = withContext(Dispatchers.IO) {
        val objects = keys.map { ObjectIdentifier { key = it } }

        val response = buildClient().use { client ->
            client.deleteObjects(
                input = DeleteObjectsRequest {
                    bucket = bucketName
                    delete = Delete {
                        this.objects = objects
                        quiet = false
                    }
                }
            )
        }
        response
    }

}