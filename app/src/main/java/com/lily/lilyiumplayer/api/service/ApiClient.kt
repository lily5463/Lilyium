package com.lily.lilyiumplayer.api.service

import com.lily.lilyium.api.service.NavidromeApi
import com.lily.lilyiumplayer.api.AuthInterceptor
import com.lily.lilyiumplayer.util.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {
    private val moshi = MoshiProvider.moshi

    fun create(): NavidromeApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor()) // no parameters anymore
            .build()

        // Placeholder base URL — AuthInterceptor replaces it per request
        val baseUrl = SessionManager.active?.baseUrl ?: "http://localhost/"

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NavidromeApi::class.java)
    }


//    fun create(baseUrl: String): NavidromeApi {
//        val logging = HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        }
//
//        val client = OkHttpClient.Builder()
//            .addInterceptor(
//                AuthInterceptor(
//                    username = session?.username ?: String(),
//                    token = session?.token ?: String(),
//                    salt = session?.salt ?: String()
//                )
//            )
//            .build()
//
//        return Retrofit.Builder()
//            .baseUrl(baseUrl)
//            .client(client)
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .build()
//            .create(NavidromeApi::class.java)
//    }
}