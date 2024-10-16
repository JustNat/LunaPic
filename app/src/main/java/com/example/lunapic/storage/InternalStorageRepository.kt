package com.example.lunapic.storage

import android.graphics.Bitmap
import aws.smithy.kotlin.runtime.content.ByteStream

interface InternalStorageRepository {
    suspend fun saveMedia(
        fileName: String,
        fileBody: ByteStream,
        bucketName: String,
        size: Long
    )
    suspend fun getMedias(bucketName: String) : List<Bitmap>
    suspend fun saveBucket(bucketName : String)
    suspend fun deleteBucket(bucketName: String)
}