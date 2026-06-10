package com.lily.lilyiumplayer.api.model


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlbumDetail(
    val id: String,
    val name: String,
    val artist: String?,
    val artistId: String?,
    val coverArt: String?,
    val songCount: Int?,
    val duration: Int?,
    val year: Int?,
    val genre: String?,
    val song: List<Song>? = emptyList()
)