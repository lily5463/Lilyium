package com.lily.lilyiumplayer.api

import android.net.Uri
import androidx.datastore.core.IOException
import com.lily.lilyiumplayer.util.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import androidx.core.net.toUri

class AuthInterceptor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val startIndex = SessionManager.activeIndex
        var attempts = 0

        while (attempts < SessionManager.profiles.size) {
            val profile = SessionManager.active
                ?: return chain.proceed(originalRequest) // not logged in, skip

            // Parse the active server's URL
            val serverUri = profile.baseUrl.toUri()

            // Rebuild the request URL using the active server + auth params
            val newUrl = originalRequest.url.newBuilder()
                .scheme(serverUri.scheme ?: "http")
                .host(serverUri.host ?: originalRequest.url.host)
                .port(serverUri.port.takeIf { it > 0 } ?: if (serverUri.scheme == "https") 443 else 80)
                .addQueryParameter("u", profile.username)
                .addQueryParameter("t", profile.token)
                .addQueryParameter("s", profile.salt)
                .addQueryParameter("v", "1.16.1")
                .addQueryParameter("c", "Lilyium")
                .addQueryParameter("f", "json")
                .build()

            val request = originalRequest.newBuilder().url(newUrl).build()

            return try {
                val response = chain.proceed(request)

                if (response.code in 500..599) {
                    // Server returned an error, try the next one
                    response.close()
                    SessionManager.switchToNext()
                    attempts++
                    continue
                }

                response // success, return it
            } catch (e: IOException) {
                // Server unreachable, try the next one
                SessionManager.switchToNext()
                attempts++
                continue
            }
        }

        // Every server failed, give up and restore original server
        SessionManager.activeIndex = startIndex
        return chain.proceed(originalRequest)
    }


//        val session = SessionManager.session
//        val request = chain.request()
//
//        if (session != null) {
//            val newUrl = request.url.newBuilder()
//                .addQueryParameter("u", username)
//                .addQueryParameter("t", token)
//                .addQueryParameter("s", salt)
//                .addQueryParameter("v", "1.16.1")
//                .addQueryParameter("c", "Lilyium")
//                .addQueryParameter("f", "json")
//                .build()
//
//            val newRequest = request.newBuilder()
//                .url(newUrl)
//                .build()
//
//            return chain.proceed(newRequest)
//        }
//
//        return chain.proceed((request))
//    }
}