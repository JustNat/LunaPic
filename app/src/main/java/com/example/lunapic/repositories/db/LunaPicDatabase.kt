package com.example.lunapic.repositories.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lunapic.repositories.db.data.BucketDao
import com.example.lunapic.repositories.db.data.MediaDao
import com.example.lunapic.repositories.db.data.Bucket
import com.example.lunapic.repositories.db.data.Media

@Database(entities = [Bucket::class, Media::class], version = 1)
@TypeConverters(DataConverter::class)
abstract class LunaPicDatabase : RoomDatabase() {
    abstract fun bucketDao(): BucketDao
    abstract fun mediaDao(): MediaDao
}