package com.lily.lilyiumplayer.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true )
data class AlbumArtist(
    val id: String,
    val name: String,
)