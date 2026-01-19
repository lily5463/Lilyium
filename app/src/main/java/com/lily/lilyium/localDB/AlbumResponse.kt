package com.lily.lilyium.localDB

import com.lily.lilyium.localDB.model.Album
import com.squareup.moshi.Json

data class AlbumResponse(
    @Json(name = "subsonic-response") val subsonicResponse: SubsonicResponse
)

data class SubsonicResponse(
    val status: String,
    val version: String,
    val type: String,
    val serverVersion: String,
    val openSubsonic: Boolean,
    val albumList: AlbumList
)

data class AlbumList(
    val album: List<Album>
)
