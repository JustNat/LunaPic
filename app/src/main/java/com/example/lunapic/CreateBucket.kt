package com.example.lunapic

import aws.sdk.kotlin.services.s3.model.BucketLocationConstraint
import aws.sdk.kotlin.services.s3.model.CreateBucketConfiguration
import aws.sdk.kotlin.services.s3.model.CreateBucketRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun createBuckt(name : String) = withContext(Dispatchers.IO){
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