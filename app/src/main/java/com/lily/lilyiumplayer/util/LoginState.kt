package com.lily.lilyiumplayer.util

import com.lily.lilyiumplayer.data.model.ServerProfile

sealed class LoginState {
    object Loading : LoginState()
    object LoggedOut : LoginState()
    data class LoggedIn(val profile: ServerProfile) : LoginState()
    data class Error(val error: Exception) : LoginState()
}