package com.lily.lilyiumplayer.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true )
data class Song(
    val id: String,
    val parent: String,
    val isDir: Boolean,
    val title: String,
    val album: String,
    val artist: String,
    val track: Long?,
    val year: Long?,
    val genre: String?,
    val coverArt: String,
    val size: Long,
    val contentType: String,
    val suffix: String,
    val starred: String? = null,
    val duration: Long,
    val bitRate: Long,
    val path: String,
    val playCount: Long?,
    val created: String,
    val albumId: String,
    val artistId: String,
    val type: String,
    val isVideo: Boolean? = null,
    val played: String?,
    val bpm: Long,
    val comment: String,
    val sortName: String,
    val mediaType: String,
    val musicBrainzId: String,
)
