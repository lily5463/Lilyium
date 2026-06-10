package com.lily.lilyiumplayer.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true )
data class SubsonicResponseData (
    var albumList: AlbumList? = null,
    val album: AlbumDetail? = null,
    val artists: Artists? = null,
    val artist: Artist? = null,
    val status: String,
    val version: String,
    val type: String,
    val serverVersion: String,
    val openSubsonic: Boolean,
    val starred2: Starred2? = null,
    val randomSongs: RandomSongs? = null,
    val searchResult2: SearchResult2? = null,
    val searchResult3: SearchResult2? = null
)