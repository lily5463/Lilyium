package com.lily.lilyiumplayer.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true )
data class Contributor(
    val role: String,
    val artist: Artist2,
)
