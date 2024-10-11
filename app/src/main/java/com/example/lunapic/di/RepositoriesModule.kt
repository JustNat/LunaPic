package com.example.lunapic.di

import com.example.lunapic.network.aws.AWSRepository
import com.example.lunapic.network.aws.AWSUtils
import com.example.lunapic.repository.InternalStorageRepository
import com.example.lunapic.repository.InternalStorageUtils
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
    abstract fun bindAwsRepository(
        awsRepository : AWSUtils
    ): AWSRepository

    @Binds
    @Singleton
    abstract fun bindInternalStorageRepository(
        internalStorageRepository: InternalStorageUtils
    ) : InternalStorageRepository
}