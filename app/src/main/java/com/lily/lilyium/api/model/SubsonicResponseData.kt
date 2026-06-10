package com.lily.lilyium.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true )
data class SubsonicResponseData (
    var albumList: AlbumList,
)