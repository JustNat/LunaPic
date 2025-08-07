package com.example.lunapic.db.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MediaDao {

    @Insert
    suspend fun insertMedia(media: Media)

    @Delete
    suspend fun deleteMedia(media: Media)

    @Query("SELECT COUNT(*) FROM media WHERE name = :name")
    suspend fun isMediaRegistered(name: String) : Int
}