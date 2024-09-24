package com.example.lunapic.repository.db

import android.content.Context
import androidx.room.Room

object DatabaseInstance {

    @Volatile
    private var instance : LunaPicDatabase? = null

    fun getDatabase(context: Context) : LunaPicDatabase {
        return instance ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                LunaPicDatabase::class.java,
                "luna_pic"
            ).build().also { instance = it }
        }
    }

}