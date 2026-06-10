package com.lily.lilyiumplayer.api.model

data class Search3(
    val artist: List<Artist>? = null,
    val album: List<Album>? = null,
    val song: List<Song>? = null
)