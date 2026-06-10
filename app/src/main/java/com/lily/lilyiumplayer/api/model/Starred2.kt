package com.lily.lilyiumplayer.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true )
data class Starred2(
    val song: List<Song>,
)
