package com.example.lunapic.aws

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
import com.example.lunapic.repository.InternalStorageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object AWSUtils : AWSCredentials() {

    private fun buildClient(): S3Client {
        return S3Client {
            credentialsProvider = StaticCredentialsProvider(
                credentials = Credentials(ACCESS_KEY, SECRET_KEY)
            )
            region = "sa-east-1"
        }
    }

    suspend fun createBuckt(name: String) = withContext(Dispatchers.IO) {
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

    suspend fun deleteBuckt(bucketName: String) = withContext(Dispatchers.IO) {
        buildClient().use {
            it.deleteBucket(
                DeleteBucketRequest {
                    bucket = bucketName
                }
            )
        }
    }

    suspend fun listBuckts(): List<Bucket> = withContext(Dispatchers.IO) {
        buildClient().use {
            val response = it.listBuckets().buckets ?: emptyList()
            response
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

    suspend fun getObjects(bucketName: String, context: Context) = withContext(Dispatchers.IO) {

        val keys = listObjects(bucketName)
        val internalDir = context.filesDir
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
                            InternalStorageUtils.saveMedia(
                                context = context,
                                fileName = obj.key ?: "",
                                fileBody = body,
                                dirName = bucketName,
                                size = obj.size ?: 0
                            )
                        }
                    }
                }
            }
        }
    }

}