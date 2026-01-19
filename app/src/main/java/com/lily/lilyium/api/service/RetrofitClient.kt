package com.lily.lilyium.api.service

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    private val moshi = Moshi.Builder()
        .build()
//    val retrofit = Retrofit.Builder()
//        .baseUrl("http://100.114.211.103:4533")
//        .addConverterFactory(MoshiConverterFactory.create(moshi))
//        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.9:4533")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService = retrofit.create(ApiService::class.java)
}