package com.lily.lilyium.api.service

import com.lily.lilyiumplayer.api.model.SubsonicResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface NavidromeApi {
    @GET("rest/ping.view?")
    suspend fun ping(): SubsonicResponse

    @GET("rest/getRandomSongs")
    suspend fun getRandomSongs (
        @Query("size") size: Int
    ): SubsonicResponse

    @GET("rest/getCoverArt.view")
    suspend fun getCoverArt(
        @Query("id") id: String,
    ): ResponseBody

    @GET("rest/getAlbum.view?")
    suspend fun getAlbum(
        @Query("id") id: String
    ): SubsonicResponse

    @GET("rest/getAlbumList.view?")
    suspend fun getAlbumList(
        @Query("type") type: String,
        @Query("size") size: Int ?= 10,
        @Query("offset") offset: Int ?= 0,
    ): SubsonicResponse

    @GET("rest/getStarred2.view?")
    suspend fun getStarred2(
        @Query("f") format: String = "json"
    ): SubsonicResponse

    @GET("rest/search2")
    suspend fun search2(
        @Query("query") query: String,
        @Query("artistCount") artistCount: Int = 20,
        @Query("albumCount") albumCount: Int = 20,
        @Query("songCount") songCount: Int = 20
    ): SubsonicResponse

    @GET("rest/search3")
    suspend fun search3(
        @Query("query") query: String,
        @Query("artistCount") artistCount: Int = 0,
        @Query("albumCount") albumCount: Int = 0,
        @Query("songCount") songCount: Int,
        @Query("songOffset") songOffset: Int, //will have to pass page or sth? idk but it should only continue getting more when user really scroll all the way to it
    ): SubsonicResponse

    @GET("rest/getArtists")
    suspend fun getArtists (): SubsonicResponse

    @GET("rest/getArtist")
    suspend fun getArtist (
        @Query("id") id: String,
    ): SubsonicResponse

    @GET("rest/star")
    suspend fun star(
        @Query("id") id: String
    )

    suspend fun unstar(
        @Query("id") id: String
    )
//    @GET("rest/getCoverArt.view")
//    suspend fun stream(
//        @Query("id") id: String,
//        @Query("u") user: String = SessionManager.session?.username ?: "",
//        @Query("t") token: String = SessionManager.session?.token ?: "",
//        @Query("s") salt: String = SessionManager.session?.salt ?: "",
//        @Query("v") version: String = "1.16.1",
//        @Query("c") client: String = "lilyium"
//    ): ResponseBody
}