package com.example.lunapic.domain.usecases

import com.example.lunapic.data.repositories.network.CloudStorageRepository
import com.example.lunapic.data.repositories.storage.models.InternalBucket
import com.example.lunapic.data.repositories.storage.InternalStorageRepository
import com.example.lunapic.domain.entities.AllAndPendingSyncBuckets
import com.example.lunapic.domain.entities.Bucket
import com.example.lunapic.domain.entities.CreateBucketDTO
import com.example.lunapic.domain.entities.OutOfAppCreatedBucketDTO
import javax.inject.Inject

class BucketUseCase @Inject constructor(
    private val cloudStorageRepository: CloudStorageRepository,
    private val internalStorageRepository: InternalStorageRepository,
) {

    suspend fun getBuckets(): Result<AllAndPendingSyncBuckets> {
        try {
            val cloudBuckets = cloudStorageRepository.listBuckets()
            val internalBuckets = internalStorageRepository.getBuckets()

            val cloudBucketsToString = cloudBuckets.map { it.name }
            val bucketsPendingSync =
                syncCloudAndInternalBuckets(
                    cloudBuckets = cloudBucketsToString,
                    internalBuckets = internalBuckets
                )

            val bucketEntities = mutableListOf<Bucket>()
            cloudBuckets.forEachIndexed { index, cloudBucket ->
                {
                    if (cloudBucket.name == internalBuckets[index].name) {
                        bucketEntities.add(
                            Bucket(
                                name = cloudBucket.name,
                                isPrivate = internalBuckets[index].isPrivate,
                            )
                        )
                    }
                }
            }
            return Result.success(
                AllAndPendingSyncBuckets(
                    buckets = bucketEntities,
                    bucketsToSync = bucketsPendingSync?.map { bucketName ->
                        OutOfAppCreatedBucketDTO(bucketName)
                    }
                )
            )
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun syncCloudAndInternalBuckets(
        cloudBuckets: List<String>,
        internalBuckets: List<InternalBucket>
    ): List<String>? {
        val internalBucketsToString = internalBuckets.map { it.name }

        if (cloudBuckets != internalBucketsToString) {
            val bucketsNotInternallyStored = cloudBuckets.subtract(internalBucketsToString).toList()
            return bucketsNotInternallyStored
        }
        return null
    }

    suspend fun createBucket(data: CreateBucketDTO): Result<Unit> {
        try {
            internalStorageRepository.createBucket(data)
        } catch (e: Exception) {
            return Result.failure(e)
        }
        try {
            cloudStorageRepository.createBucket(data.bucketName)
            return Result.success(Unit)
        } catch (e: Exception) {
            internalStorageRepository.deleteBucket(data.bucketName)
            return Result.failure(e)
        }
    }

    suspend fun deleteBucket(bucketName: String): Result<Unit> {
        try {
            internalStorageRepository.deleteBucket(bucketName)
        } catch (e: Exception) {
            return Result.failure(e)
        }
        try {
            cloudStorageRepository.deleteBucket(bucketName)
            return Result.success(Unit)
        } catch (e: Exception) {
            internalStorageRepository.reverseDeleteBucketOperation()
            return Result.failure(e)
        }
    }

    suspend fun registerBucketsCreatedOutsideTheApp(buckets: List<OutOfAppCreatedBucketDTO>): Result<Unit> {
        try {
            internalStorageRepository.registerBucketsCreatedOutsideTheApp(buckets)
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}