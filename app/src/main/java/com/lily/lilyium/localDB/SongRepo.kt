package com.lily.lilyium.localDB

import android.content.Context
import com.lily.lilyium.R
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class SongRepo {
    suspend fun loadJsonIntoRoom(context: Context) {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val adapter = moshi.adapter(AlbumResponse::class.java)

        val inputStream = context.resources.openRawResource(R.raw.get_album_list)
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        // Deserialize the JSON and get the list of albums
        val albums = adapter.fromJson(jsonString)?.subsonicResponse?.albumList?.album ?: emptyList()

        val db = AppDatabase.getInstance(context)
        db.DatabaseDao().insertAll(albums)

    }
}