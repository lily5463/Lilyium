package com.lily.lilyiumplayer.data.model

data class QueueSong(
    val id: String,
    val title: String,
    val artist: String?,
    val coverUrl: String?,
    val streamUrl: String
)
