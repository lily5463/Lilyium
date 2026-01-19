package com.lily.lilyium.localDB.service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lily.lilyium.localDB.model.Album


@Dao
interface DatabaseDao{

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(album: Album)
//    fun insert(song: testSong)
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(albumList: List<Album>)

    @Query("SELECT * FROM album")
    suspend fun getAllSongs(): List<com.lily.lilyium.api.model.Album>
}