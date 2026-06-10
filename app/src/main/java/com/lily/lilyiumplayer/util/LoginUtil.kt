package com.lily.lilyiumplayer.util

import android.content.Context
import android.util.Log
import androidx.datastore.core.IOException
import com.lily.lilyiumplayer.api.service.ApiClient
import com.lily.lilyiumplayer.data.UserPreferences
import com.lily.lilyiumplayer.data.model.ServerProfile
import com.lily.lilyiumplayer.util.HashUtil.generateSalt
import com.lily.lilyiumplayer.util.HashUtil.md5
import okhttp3.ResponseBody
import java.util.UUID


class LoginUtil(private val context: Context) {

    private  val userPrefs = UserPreferences(context.applicationContext)
    suspend fun login(
        serverAddr: String,
        label: String,        // friendly name e.g. "Home" or "Tailscale"
        username: String,
        password: String,
        existingId: String? = null
    ): LoginResult {
        val salt = generateSalt()
        val token = md5(password + salt)

        // Temporarily load this server into SessionManager so
        // AuthInterceptor can use it for the ping request
        val tempProfile = ServerProfile(
            id = existingId ?: UUID.randomUUID().toString(),
            label = label,
            baseUrl = serverAddr,
            username = username,
            token = token,
            salt = salt
        )
        SessionManager.profiles += tempProfile
        SessionManager.activeIndex = SessionManager.profiles.lastIndex

        return try {
            Log.d("Login", "pinging $serverAddr")
            val response = ApiClient.create().ping()
            Log.d("Login", "ping response: $response")

            if (response.subsonicResponse.status == "ok") {
                Log.d("Login", "success")
                // Save permanently to DataStore
                userPrefs.upsertProfile(tempProfile, setActive = true)
                LoginResult.Success
            } else {
                Log.d("Login", "invalid credentials")
                // Remove the temp profile we added
                SessionManager.profiles = SessionManager.profiles - tempProfile
                LoginResult.InvalidCredentials
            }
        } catch (e: IOException) {
            Log.d("Login", "server unreachable: ${e.message}")
            SessionManager.profiles = SessionManager.profiles - tempProfile
            LoginResult.ServerUnreachable
        } catch (e: Exception) {
            Log.d("Login", "error: ${e.message}")
            SessionManager.profiles = SessionManager.profiles - tempProfile
            LoginResult.InvalidCredentials
        }
    }
}

sealed class LoginResult {
    object Success : LoginResult()
    object InvalidCredentials : LoginResult()
    object ServerUnreachable : LoginResult()
}
//    suspend fun login(
//        serverAddr: String,
//        username: String,
//        password: String
//    ): LoginResult {
//        val salt = generateSalt()
//        val token = md5(password + salt)
//        val api = ApiClient.create(serverAddr)
//
//        Log.d("salt", "${salt}")
//        Log.d("token", "${token}")
//        return try {
//            Log.d("Login", "pinging")
//            val response = api.ping(
//                user = username,
//                token = token,
//                salt = salt,
//                version = "1.16.1",
//                client = "Lilyium"
//            )
//            Log.d("pingingRes", "${response}")
//
//            if (response.subsonicResponse.status == "ok") {
//                Log.d("Login", "login success")
//                SessionManager.session =
//                    UserPreferences.UserSession(serverAddr, username, token, salt)
//                userPrefs.saveSession(serverAddr, username, token, salt)
//                LoginResult.Success
//            } else {
//                Log.d("Login", "login fail")
//                LoginResult.InvalidCredentials
//            }
//        } catch (e: Exception) {
//            LoginResult.InvalidCredentials
//        }
//    }
//}
//
//sealed class LoginResult {
//    object Success: LoginResult()
//    object InvalidCredentials: LoginResult()
//    object ServerUnreachable: LoginResult()
//}