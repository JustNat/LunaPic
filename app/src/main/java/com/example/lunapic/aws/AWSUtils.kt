package com.example.lunapic.aws

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.Bucket
import aws.sdk.kotlin.services.s3.model.BucketLocationConstraint
import aws.sdk.kotlin.services.s3.model.CreateBucketConfiguration
import aws.sdk.kotlin.services.s3.model.CreateBucketRequest
import aws.sdk.kotlin.services.s3.model.DeleteBucketRequest
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.io.use
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AWSUtils : AWSCredentials() {

    private fun buildClient() : S3Client {
        return S3Client {
            credentialsProvider = StaticCredentialsProvider(
                credentials = Credentials(ACCESS_KEY, SECRET_KEY)
            )
            region = "sa-east-1"
        }
    }

    suspend fun createBuckt(name : String) = withContext(Dispatchers.IO) {
        buildClient().use {
            it.createBucket(
                CreateBucketRequest{
                    bucket = name
                    createBucketConfiguration = CreateBucketConfiguration {
                        locationConstraint = BucketLocationConstraint.SaEast1
                    }
                }
            )
        }
    }

    suspend fun deleteBuckt(bucketName : String) = withContext(Dispatchers.IO) {
        buildClient().use {
            it.deleteBucket(
                DeleteBucketRequest {
                    bucket = bucketName
                }
            )
        }
    }

    suspend fun listBuckts(): List<Bucket> = withContext(Dispatchers.IO){
        buildClient().use {
            val response = it.listBuckets().buckets ?: emptyList()
            response
        }
    }

}