package com.example.lunapic.di

import android.content.Context
import androidx.room.Room
import com.example.lunapic.repository.db.LunaPicDatabase
import com.example.lunapic.repository.db.data.BucketDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context : Context) : LunaPicDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            LunaPicDatabase::class.java,
            "luna_pic"
        ).build()
    }

    @Provides
    fun provideBucketDao(db : LunaPicDatabase) : BucketDao {
        return db.bucketDao()
    }
}