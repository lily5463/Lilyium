package com.lily.lilyiumplayer.api.model

import com.squareup.moshi.JsonClass;

@JsonClass(generateAdapter = true )
data class RandomSongs(
    val song: List<Song>
)
