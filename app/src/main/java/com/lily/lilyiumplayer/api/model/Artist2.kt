package com.lily.lilyiumplayer.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true )
data class Artist2(
    val id: String,
    val name: String,
)
