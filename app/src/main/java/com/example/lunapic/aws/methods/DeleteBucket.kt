package com.example.lunapic.aws.methods

import aws.sdk.kotlin.services.s3.model.DeleteBucketRequest
import com.example.lunapic.aws.buildClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun deleteBuckt(bucketName : String) = withContext(Dispatchers.IO) {
    buildClient().use {
        it.deleteBucket(
            DeleteBucketRequest {
                bucket = bucketName
            }
        )
    }
}