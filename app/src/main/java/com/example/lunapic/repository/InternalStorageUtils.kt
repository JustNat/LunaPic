package com.example.lunapic.repository

import android.content.Context
import android.os.StatFs
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object InternalStorageUtils {

    private val fileLock = Any()

    suspend fun saveMedia(
        context: Context, fileName: String, fileBody: ByteStream, dirName: String, size: Long
    ) = withContext(Dispatchers.IO) {
        val internalDir = context.filesDir
        val availableStorage = getAvailableStorage(context)
        val directory = File(internalDir, dirName)
        val file = File(directory, fileName)

        synchronized(fileLock) {
            if (!directory.exists()) {
                directory.mkdirs()
            }
            if (size >= availableStorage) {
                throw IOException("Não há espaço restante no dispositivo.")
            }
            FileOutputStream(file).use {
                fileBody.toInputStream().copyTo(it)
            }
        }
    }

    private fun getAvailableStorage(context: Context): Long {
        val internalDir = context.filesDir
        val stat = StatFs(internalDir.absolutePath)
        return stat.blockSizeLong * stat.availableBlocksLong
    }
}