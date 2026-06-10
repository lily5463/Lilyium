package com.lily.lilyiumplayer.util

import com.lily.lilyiumplayer.data.model.ServerProfile

object SessionManager {
    var profiles: List<ServerProfile> = emptyList()
    var activeIndex: Int = 0

    // The server currently in use
    val active: ServerProfile?
        get() = profiles.getOrNull(activeIndex)

    // Call this on app start after reading from DataStore
    fun load(profiles: List<ServerProfile>, activeId: String) {
        this.profiles = profiles
        this.activeIndex = profiles.indexOfFirst { it.id == activeId }.takeIf { it >= 0 } ?: 0
    }

    // Call this when user manually picks a server
    fun switchTo(profileId: String) {
        val idx = profiles.indexOfFirst { it.id == profileId }
        if (idx >= 0) activeIndex = idx
    }

    // Called automatically when a server doesn't respond
    // Returns the new server it switched to, or null if only one server
    fun switchToNext(): ServerProfile? {
        if (profiles.size <= 1) return null
        activeIndex = (activeIndex + 1) % profiles.size
        return profiles[activeIndex]
    }
}

//object SessionManager {
//    var session: UserPreferences.UserSession? = null
//}
//
//sealed class LoginState {
//    object Loading : LoginState()
//    object LoggedOut : LoginState()
//
//    data class LoggedIn(val session: UserPreferences.UserSession) : LoginState()
//    data class Error(val error: Exception) : LoginState()
//
//}



