package com.example.lunapic.repository.db

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@TypeConverters(DataConverter::class)
class DataConverter {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun fromTimeStamp(timeStamp : String) : OffsetDateTime {
        return formatter.parse(timeStamp, OffsetDateTime::from)
    }

    @TypeConverter
    fun fromOffsetDateTime(date : OffsetDateTime) : String {
        return formatter.format(date)
    }
}