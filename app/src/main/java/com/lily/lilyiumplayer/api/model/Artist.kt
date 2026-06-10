package com.lily.lilyiumplayer.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true )
data class Artist(
    val id: String,
    val name: String,
    val coverArt: String = "",
    val albumCount: Int = 0,
    val starred: String? = null,     // ISO date string if starred
    val userRating: Int? = null,      // 1–5 if rated
    val artistImageUrl: String? = null,
    val album: List<Album> = emptyList()
)
