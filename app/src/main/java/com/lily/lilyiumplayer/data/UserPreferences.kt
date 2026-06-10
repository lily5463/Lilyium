package com.lily.lilyiumplayer.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lily.lilyiumplayer.api.service.MoshiProvider
import com.lily.lilyiumplayer.data.model.ServerProfile
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {


    private val moshi = MoshiProvider.moshi

    private val profileListAdapter = moshi.adapter<List<ServerProfile>>(
        Types.newParameterizedType(List::class.java, ServerProfile::class.java)
    )

    companion object {
        val PROFILES_JSON = stringPreferencesKey("profiles_json")
        val ACTIVE_PROFILE_ID = stringPreferencesKey("active_profile_id")
    }



    // Called after a successful login — saves the server to the list
    suspend fun upsertProfile(profile: ServerProfile, setActive: Boolean = false) {
        context.dataStore.edit { prefs ->
            val current = prefs[PROFILES_JSON]
                ?.let { profileListAdapter.fromJson(it) }
                ?: emptyList()

            val updated = current.filter { it.id != profile.id } + profile
            prefs[PROFILES_JSON] = profileListAdapter.toJson(updated)

            if (setActive || prefs[ACTIVE_PROFILE_ID] == null) {
                prefs[ACTIVE_PROFILE_ID] = profile.id
            }
        }
    }

    // Called when user removes a server from the list
    suspend fun removeProfile(profileId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[PROFILES_JSON]
                ?.let { profileListAdapter.fromJson(it) }
                ?: emptyList()

            val updated = current.filter { it.id != profileId }
            prefs[PROFILES_JSON] = profileListAdapter.toJson(updated)

            // If the deleted server was active, fall back to the first remaining one
            if (prefs[ACTIVE_PROFILE_ID] == profileId) {
                prefs[ACTIVE_PROFILE_ID] = updated.firstOrNull()?.id ?: ""
            }
        }
    }

    // Called when user manually taps a server to switch to it
    suspend fun setActiveProfile(profileId: String) {
        context.dataStore.edit { prefs ->
            prefs[ACTIVE_PROFILE_ID] = profileId
        }
    }

    // All saved servers — collect this in your UI to show the server list
    val profilesFlow: Flow<List<ServerProfile>> = context.dataStore.data.map { prefs ->
        prefs[PROFILES_JSON]
            ?.let { profileListAdapter.fromJson(it) }
            ?: emptyList()
    }

    // The currently active server — collect this wherever you need the current server info
    val activeProfileFlow: Flow<ServerProfile?> = context.dataStore.data.map { prefs ->
        val profiles = prefs[PROFILES_JSON]
            ?.let { profileListAdapter.fromJson(it) }
            ?: return@map null

        val activeId = prefs[ACTIVE_PROFILE_ID]
        profiles.firstOrNull { it.id == activeId } ?: profiles.firstOrNull()
    }

    // Wipes everything — used for full logout
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }

    //    companion object {
//        val BASE_URL = stringPreferencesKey("base_url")
//        val USERNAME = stringPreferencesKey("username")
//        val SALT = stringPreferencesKey("salt")
//        val TOKEN = stringPreferencesKey("token")
//    }
//    suspend fun saveSession(baseUrl: String, username: String, token: String, salt: String) {
//        context.dataStore.edit { prefs ->
//            prefs[BASE_URL] = baseUrl
//            prefs[USERNAME] = username
//            prefs[TOKEN] = token
//            prefs[SALT] = salt
//        }
//    }
//
//    val baseUrl: Flow<String?> = context.dataStore.data.map {it[BASE_URL]}
//    val username: Flow<String?> = context.dataStore.data.map {it[USERNAME]}
//    val salt: Flow<String?> = context.dataStore.data.map {it[SALT]}
//
//    val session: Flow<ServerProfile?> = context.dataStore.data.map { prefs ->
//        val baseUrl = prefs[BASE_URL]
//        val username = prefs[USERNAME]
//        val token = prefs[TOKEN]
//        val salt = prefs[SALT]
//
//        if (baseUrl != null && username != null  && token != null && salt != null) {
//            ServerProfile(baseUrl, username, token, salt)
//        } else {
//            null
//        }
//    }
//
////    Log otu
//    suspend fun clearSession() {
//        context.dataStore.edit { it.clear() }
//    }
}