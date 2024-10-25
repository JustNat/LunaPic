package com.example.lunapic.storage

import aws.smithy.kotlin.runtime.content.ByteStream
import java.io.File

interface InternalStorageRepository {
    suspend fun saveMedia(fileName: String, fileBody: ByteArray, bucketName: String, size: Long)
    suspend fun getMedias(bucketName: String) : List<File>
    suspend fun saveBucket(bucketName : String)
    suspend fun deleteBucket(bucketName: String)
}