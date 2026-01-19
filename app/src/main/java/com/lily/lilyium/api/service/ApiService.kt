package com.lily.lilyium.api.service

import com.lily.lilyium.api.model.SubsonicResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("rest/getAlbumList.view?")
    suspend fun getAlbumList(
        @Query("size") size: Int = 16,
        @Query("type") type: String = "random",
        @Query("u") user: String = "asuka",
        @Query("p") pass: String = "5463728190",
        @Query("v") version: String = "1.12.0",
        @Query("c") client: String = "lilyium",
        @Query("f") format: String = "json"
    ): SubsonicResponse
}