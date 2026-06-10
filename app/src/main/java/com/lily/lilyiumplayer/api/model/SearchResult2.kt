package com.lily.lilyiumplayer.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResult2(
    val artist: List<Artist>? = null,
    val album: List<Album>? = null,
    val song: List<Song>? = null
)