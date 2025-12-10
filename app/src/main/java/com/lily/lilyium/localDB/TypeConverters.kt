package com.lily.lilyium.localDB
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class Converters {
    private val moshi = Moshi.Builder().build()

    // List<String>
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.toJson(list)
    }

    @TypeConverter
    fun toStringList(json: String?): List<String>? {
        if (json.isNullOrEmpty()) return emptyList()

        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.fromJson(json)
    }

    // Date <--> Long
    @TypeConverter
    fun fromDate(date: java.util.Date?): Long? = date?.time

    @TypeConverter
    fun toDate(value: Long?): java.util.Date? =
        if (value == null)
            null
        else java.util.Date(value)
}