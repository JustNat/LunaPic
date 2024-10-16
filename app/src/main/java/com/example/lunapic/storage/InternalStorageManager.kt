package com.example.lunapic.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.StatFs
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.toInputStream
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class InternalStorageManager @Inject constructor(@ApplicationContext private val appContext: Context) :
    InternalStorageRepository {

    private val internalDir = appContext.filesDir

    private val fileLock = Any()

    override suspend fun saveMedia(
        fileName: String, fileBody: ByteStream, bucketName: String, size: Long
    ): Unit = withContext(Dispatchers.IO) {
        val availableStorage = getAvailableStorage()
        val directory = File(internalDir, bucketName)
        val file = File(directory, fileName)

        saveBucket(bucketName)

        synchronized(fileLock) {
            if (size >= availableStorage) {
                throw IOException("Não há espaço restante no dispositivo.")
            }
            FileOutputStream(file).use {
                fileBody.toInputStream().copyTo(it)
            }
        }
    }

    override suspend fun getMedias(bucketName: String): List<Bitmap> = withContext(Dispatchers.IO) {
        val directory = File(appContext.filesDir, bucketName)
        val files = directory.listFiles()?.toList() ?: emptyList()
        val medias = files.map {
            BitmapFactory.decodeFile(it.absolutePath)
        }
        medias
    }

    override suspend fun saveBucket(bucketName: String) = withContext(Dispatchers.IO) {
        val directory = File(internalDir, bucketName)
        synchronized(fileLock) {
            if (!directory.exists()) {
                directory.mkdirs()
            }
        }
    }

    override suspend fun deleteBucket(bucketName: String) {
        val directory = File(internalDir, bucketName)
        directory.delete()
    }

    private fun getAvailableStorage(): Long {
        val stat = StatFs(internalDir.absolutePath)
        return stat.blockSizeLong * stat.availableBlocksLong
    }
}