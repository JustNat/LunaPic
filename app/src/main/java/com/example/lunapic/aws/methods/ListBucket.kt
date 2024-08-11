package com.example.lunapic.aws.methods

import aws.sdk.kotlin.services.s3.model.Bucket
import aws.smithy.kotlin.runtime.io.use
import com.example.lunapic.aws.buildClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun listBuckts(): List<Bucket> = withContext(Dispatchers.IO){
    buildClient().use {
        val response = it.listBuckets().buckets ?: emptyList()
        response
    }
}