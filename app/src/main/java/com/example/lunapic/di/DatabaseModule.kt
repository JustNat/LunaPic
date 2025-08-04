package com.example.lunapic.di

import android.content.Context
import androidx.room.Room
import com.example.lunapic.repositories.db.LunaPicDatabase
import com.example.lunapic.repositories.db.data.BucketDao
import com.example.lunapic.repositories.db.data.MediaDao
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

    @Provides
    fun provideMediaDao(db : LunaPicDatabase) : MediaDao {
        return db.mediaDao()
    }
}