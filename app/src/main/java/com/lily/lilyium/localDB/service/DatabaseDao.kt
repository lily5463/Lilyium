package com.lily.lilyium.localDB.service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lily.lilyium.localDB.model.testSong

@Dao
interface DatabaseDao{

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(song: testSong)
//    fun insert(song: testSong)
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(songs: List<testSong>)

    @Query("SELECT * FROM songs")
    suspend fun getAllSongs(): List<testSong>
}