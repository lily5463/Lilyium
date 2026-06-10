package com.lily.lilyiumplayer.data.model

import java.util.UUID

data class ServerProfile(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val baseUrl: String,
    val username: String,
    val token: String,
    val salt: String
)
