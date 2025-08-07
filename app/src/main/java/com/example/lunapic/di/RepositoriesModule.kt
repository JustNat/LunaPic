package com.example.lunapic.di

import com.example.lunapic.data.repositories.network.CloudStorageRepository
import com.example.lunapic.data.repositories.network.aws.S3Repository
import com.example.lunapic.data.repositories.storage.DefaultInternalStorageRepository
import com.example.lunapic.data.repositories.storage.InternalStorageRepository
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
        s3Manager : S3Repository
    ): CloudStorageRepository

    @Binds
    @Singleton
    abstract fun bindInternalStorageRepository(
        internalStorageRepository: DefaultInternalStorageRepository
    ) : InternalStorageRepository
}