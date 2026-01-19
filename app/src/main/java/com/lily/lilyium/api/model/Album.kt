package com.lily.lilyium.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true )
data class Album (
    val id: String,

    val parent: String?,
    val isDir: Boolean,
    val title: String,
    val album: String?,
    val artist: String?,
    val track: Int?,
    val year: Int?,
    val genre: String?,
    val coverArt: String?, // store local file path later
    val size: Long?,
    val contentType: String?,
    val suffix: String?,
    val transcodedContentType: String?,
    val transcodedSuffix: String?,
    val duration: Int?,
    val bitRate: Int?,
    val bitDepth: Int?,
    val samplingRate: Int?,
    val channelCount: Int?,
    val path: String?,
    val isVideo: Boolean?,
    val userRating: Int?,
    val averageRating: Double?,
    val playCount: Long?,
    val discNumber: Int?,
    val created: String?, //TODO: It suppose to be date format but string for now to avoid bug
    val starred: Long?,
    val albumId: String?,
    val artistId: String?,
    val type: String?,
    val mediaType: String?,
    val bookmarkPosition: Long?,
    val originalWidth: Int?,
    val originalHeight: Int?,
    val played: String?, //TODO: It suppose to be date format but string for now to avoid bug
    val bpm: Int?,
    val comment: String?,
    val sortName: String?,
    val musicBrainzId: String?,

    // These will use JSON TypeConverters
    val genresJson: String?,
    val artistsJson: String?,
    val albumArtistsJson: String?,
    val contributorsJson: String?,
    val moodsJson: String?,
    val replayGainJson: String?,

    val displayArtist: String?,
    val displayAlbumArtist: String?,
    val displayComposer: String?,
    val explicitStatus: String?
)
