package com.example.lunapic.storage

import java.io.File

interface InternalStorageRepository {
    suspend fun saveMedia(fileName: String, fileBody: ByteArray, bucketName: String, size: Long)
    suspend fun getMedias(bucketName: String) : List<File>
    suspend fun saveBucket(bucketName : String)
    suspend fun deleteBucket(bucketName: String)
}