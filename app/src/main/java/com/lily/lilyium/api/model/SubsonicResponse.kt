package com.lily.lilyium.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true )
data class SubsonicResponse (

    @Json(name = "subsonic-response")
    val subsonicResponse : SubsonicResponseData

)