package com.example.lunapic.di

import com.example.lunapic.repository.network.CloudStorageServiceRepository
import com.example.lunapic.repository.network.aws.S3Manager
import com.example.lunapic.storage.InternalStorageManager
import com.example.lunapic.storage.InternalStorageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {

    @Binds
    @Singleton
    abstract fun bindS3Manager(
        s3Manager : S3Manager
    ): CloudStorageServiceRepository

    @Binds
    @Singleton
    abstract fun bindInternalStorageRepository(
        internalStorageRepository: InternalStorageManager
    ) : InternalStorageRepository
}