package com.example.lunapic.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lunapic.db.data.BucketDao
import com.example.lunapic.db.data.MediaDao
import com.example.lunapic.db.data.Bucket
import com.example.lunapic.db.data.Media

@Database(entities = [Bucket::class, Media::class], version = 1)
abstract class LunaPicDatabase : RoomDatabase() {
    abstract fun bucketDao(): BucketDao
    abstract fun mediaDao(): MediaDao
}