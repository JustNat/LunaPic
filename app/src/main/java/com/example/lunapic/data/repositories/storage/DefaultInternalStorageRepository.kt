package com.example.lunapic.data.repositories.storage

import android.content.Context
import android.os.StatFs
import aws.smithy.kotlin.runtime.io.use
import com.example.lunapic.data.repositories.storage.models.InternalBucket
import com.example.lunapic.db.data.Bucket as DbBucket
import com.example.lunapic.db.data.BucketDao
import com.example.lunapic.db.data.toInternalBucket
import com.example.lunapic.domain.entities.CreateBucketDTO
import com.example.lunapic.domain.entities.OutOfAppCreatedBucketDTO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class DefaultInternalStorageRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val bucketDao: BucketDao
) : InternalStorageRepository {

    private val internalDir = appContext.filesDir

    private val fileLock = Any()

    private var recentDeletedBucket: InternalBucket? = null

    override suspend fun getBuckets(): List<InternalBucket> = withContext(Dispatchers.IO) {
        val dbBuckets = bucketDao.getBuckets()
        dbBuckets.map { dbBucket -> dbBucket.toInternalBucket() }.sortedBy { it.name }
    }

    override suspend fun saveMedia(
        fileName: String, fileBody: ByteArray, bucketName: String, size: Long
    ): Unit = withContext(Dispatchers.IO) {
        val availableStorage = getAvailableStorage()
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

    override suspend fun createBucket(data: CreateBucketDTO) =
        withContext(Dispatchers.IO) {
            try {
                createDirectoryBucket(data.bucketName)
            } catch (e: Exception) {
                throw e
            }
            try {
                createDbBucket(data)
            } catch (e: Exception) {
                deleteDirectoryBucket(data.bucketName)
                throw e
            }
        }

    private fun createDirectoryBucket(bucketName: String) {
        val directory = File(internalDir, bucketName)
        synchronized(fileLock) {
            if (!directory.exists()) {
                val wasCreated = directory.mkdirs()
                if (!wasCreated) throw DirectoryAlreadyExists()
            }
        }
    }

    private suspend fun createDbBucket(data: CreateBucketDTO) {
        bucketDao.insertBucket(DbBucket(data.bucketName, data.isPrivate))
    }

    override suspend fun registerBucketsCreatedOutsideTheApp(buckets: List<OutOfAppCreatedBucketDTO>) {
        val formattedBuckets = buckets.map { it.formatToDbBucket() }
        bucketDao.insertBuckets(formattedBuckets)
        formattedBuckets.forEach { bucket -> createDirectoryBucket(bucket.name) }
    }

    override suspend fun deleteBucket(bucketName: String): Unit = withContext(Dispatchers.IO) {
        try {
            deleteDirectoryBucket(bucketName)
        } catch (e: Exception) {
            throw e
        }
        try {
            deleteDbBucket(bucketName)
        } catch (e: Exception) {
            createDirectoryBucket(bucketName)
            throw e
        }
    }

    private fun deleteDirectoryBucket(bucketName: String) {
        val directory = File(internalDir, bucketName)
        directory.delete()
    }

    private suspend fun deleteDbBucket(bucketName: String) {
        bucketDao.deleteBucket(bucketName)
        registerBucketInRecentDelete(bucketName)
    }

    private suspend fun registerBucketInRecentDelete(bucketName: String) {
        val isBucketPrivate = bucketDao.isBucketPrivate(bucketName)
        recentDeletedBucket = InternalBucket(name = bucketName, isPrivate = isBucketPrivate)
    }

    override suspend fun reverseDeleteBucketOperation() {
        recentDeletedBucket?.let { it ->
            createBucket(
                CreateBucketDTO(
                    bucketName = it.name,
                    isPrivate = it.isPrivate
                )
            )
        }
        recentDeletedBucket = null
    }

    override suspend fun deleteMedia(file: File): Unit = withContext(Dispatchers.IO) {
        synchronized(fileLock) {
            file.delete()
        }
    }

    private fun getAvailableStorage(): Long {
        val stat = StatFs(internalDir.absolutePath)
        return stat.blockSizeLong * stat.availableBlocksLong
    }
}