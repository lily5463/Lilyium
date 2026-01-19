package com.lily.lilyium.localDB

import android.content.Context
import com.lily.lilyium.api.service.RetrofitClient
import com.lily.lilyium.mapper.toDbAlbum
import com.lily.lilyium.api.model.Album

class SongRepo {
    suspend fun loadJsonIntoRoom(context: Context) {
//        val moshi = Moshi.Builder()
//            .add(KotlinJsonAdapterFactory())
//            .build()
//
//        val adapter = moshi.adapter(AlbumResponse::class.java)
//        val albumList = RetrofitClient.apiService.getAlbumList()
//        val inputStream = context.resources.openRawResource(R.raw.albumList)
        val response = RetrofitClient.apiService.getAlbumList()
        // Deserialize the JSON and get the list of albums
        val albums = response.subsonicResponse?.albumList?.album ?: emptyList()

        val db = AppDatabase.getInstance(context)

        val dbAlbums = albums.map {it.toDbAlbum()}
        db.DatabaseDao().insertAll(dbAlbums)

    }
}

