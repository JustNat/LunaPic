package com.example.lunapic.utils.extensions

import aws.sdk.kotlin.services.s3.model.Bucket
import com.example.lunapic.data.repositories.network.aws.models.CloudBucket

fun Bucket.toCloudModel(): CloudBucket {
    return CloudBucket(name = this.name ?: "", region = this.bucketRegion ?: "")
}