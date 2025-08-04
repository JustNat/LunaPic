package com.example.lunapic.repositories.storage

import android.content.Context
import android.os.StatFs
import aws.smithy.kotlin.runtime.io.use
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class DefaultInternalStorageRepository @Inject constructor(
    @ApplicationContext private val appContext: Context
) : InternalStorageRepository {

    private val internalDir = appContext.filesDir

    private val fileLock = Any()

    override suspend fun saveMedia(
        fileName: String, fileBody: ByteArray, bucketName: String, size: Long
    ): Unit = withContext(Dispatchers.IO) {
        val availableStorage = getAvailableStorage()
        saveBucket(bucketName)
        val directory = File(internalDir, bucketName)
        val file = File(directory, fileName)

        synchronized(fileLock) {
            file.createNewFile()
            if (size >= availableStorage) {
                throw IOException("Não há espaço restante no dispositivo.")
            }

            FileOutputStream(file).use { fileOutputStream ->
                fileBody.inputStream().use { bytes ->
                    bytes.copyTo(fileOutputStream)
                }
            }
        }
    }

    override suspend fun getMedias(bucketName: String): List<File> = withContext(Dispatchers.IO) {
        val directory = File(appContext.filesDir, bucketName)
        val files = directory.listFiles()?.toList() ?: emptyList()
        files
    }

    override suspend fun saveBucket(bucketName: String) = withContext(Dispatchers.IO) {
        val directory = File(internalDir, bucketName)
        synchronized(fileLock) {
            if (!directory.exists()) {
                directory.mkdirs()
            }
        }
    }

    override suspend fun deleteBucket(bucketName: String) : Unit = withContext(Dispatchers.IO) {
        val directory = File(internalDir, bucketName)
        directory.delete()
    }

    override suspend fun deleteMedia(file: File) : Unit = withContext(Dispatchers.IO){
        synchronized(fileLock) {
            file.delete()
        }
    }

    private fun getAvailableStorage(): Long {
        val stat = StatFs(internalDir.absolutePath)
        return stat.blockSizeLong * stat.availableBlocksLong
    }

}