package com.lily.lilyiumplayer.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Artists(
    val ignoredArticles: String = "",
    val index: List<Index>
)
