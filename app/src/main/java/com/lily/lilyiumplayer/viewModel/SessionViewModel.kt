package com.lily.lilyiumplayer.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lily.lilyiumplayer.data.UserPreferences
import com.lily.lilyiumplayer.data.model.ServerProfile
import com.lily.lilyiumplayer.util.LoginResult
import com.lily.lilyiumplayer.util.LoginUtil
import com.lily.lilyiumplayer.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionState(
    val profiles: List<ServerProfile> = emptyList(),
    val activeProfile: ServerProfile? = null,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = true,
    val showAddServerDialog: Boolean = false,
    val isEditingProfile: ServerProfile? = null, //non-null = edit dialog open
    val isAddingServer: Boolean = false,
    val addServerError: String? = null,
)

// All actions the UI can send to the ViewModel
sealed class SessionAction {
    object Logout : SessionAction()
    object ShowAddServerDialog : SessionAction()
    object DismissAddServerDialog : SessionAction()
    object DismissEditServerDialog : SessionAction()
    data class ShowEditServerDialog(val profileId: String) : SessionAction()
    data class SubmitAddServer(
        val label: String,
        val serverAddr: String,
        val username: String,
        val password: String
    ) : SessionAction()
    data class SubmitEditServer(
        val profileId: String,
        val label: String,
        val serverAddr: String,
        val username: String,
        val password: String
    ) : SessionAction()
    data class DeleteServer(val profileId: String) : SessionAction()
    data class SwitchServer(val profileId: String) : SessionAction()
}

class SessionViewModel(app: Application) : AndroidViewModel(app) {

    private val userPrefs = UserPreferences(app)
    private val loginUtil = LoginUtil(app)

    private val _state = MutableStateFlow(SessionState())
    val state: StateFlow<SessionState> = _state

    // One-time events (like navigate to login) — not state

    init {
        viewModelScope.launch {
            combine(
                userPrefs.profilesFlow,
                userPrefs.activeProfileFlow
            ) { profiles, active -> profiles to active }
                .collect { (profiles, active) ->
                    if (active != null) SessionManager.load(profiles, active.id)
                    _state.update {
                        it.copy(
                            profiles = profiles,
                            activeProfile = active,
                            isLoggedIn = active != null,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onAction(action: SessionAction) {
        when (action) {
            is SessionAction.Logout -> logout()
            is SessionAction.ShowAddServerDialog -> _state.update { it.copy(showAddServerDialog = true, addServerError = null) }
            is SessionAction.DismissAddServerDialog -> _state.update { it.copy(showAddServerDialog = false, addServerError = null) }
            is SessionAction.ShowEditServerDialog -> {
                val profile = _state.value.profiles.find { it.id == action.profileId }
                _state.update { it.copy(isEditingProfile = profile, addServerError = null) }
            }
            is SessionAction.DismissEditServerDialog -> _state.update { it.copy(isEditingProfile = null, addServerError = null) }
            is SessionAction.SubmitAddServer -> addServer(action)
            is SessionAction.SubmitEditServer -> editServer(action)
            is SessionAction.DeleteServer -> deleteServer(action.profileId)
            is SessionAction.SwitchServer -> switchServer(action.profileId)
        }
    }

    private fun addServer(action: SessionAction.SubmitAddServer) {
        viewModelScope.launch {
            _state.update { it.copy(isAddingServer = true, addServerError = null) }
            val result = loginUtil.login(
                serverAddr = action.serverAddr.trim(),
                label = action.label.trim(),
                username = action.username.trim(),
                password = action.password
            )
            when (result) {
                is LoginResult.Success ->
                    _state.update {
                        it.copy(isAddingServer = false, showAddServerDialog = false)
                    }
                is LoginResult.InvalidCredentials ->
                    _state.update {
                        it.copy(isAddingServer = false, addServerError = "Invalid credentials")
                    }
                is LoginResult.ServerUnreachable ->
                    _state.update {
                        it.copy(isAddingServer = false, addServerError = "Server unreachable")
                    }
            }
        }
    }


    private fun editServer(action: SessionAction.SubmitEditServer) {
        viewModelScope.launch {
            _state.update { it.copy(isAddingServer = true, addServerError = null)}
            val result = loginUtil.login(
                serverAddr = action.serverAddr.trim(),
                label = action.label.trim(),
                username = action.username.trim(),
                password = action.password.trim(),
                existingId = action.profileId
            )

            when (result) {
                is LoginResult.Success ->
                    _state.update { it.copy(isAddingServer = false, isEditingProfile = null) }
                is LoginResult.InvalidCredentials ->
                    _state.update { it.copy(isAddingServer = false, addServerError = "Invalid credentials") }
                is LoginResult.ServerUnreachable ->
                    _state.update { it.copy(isAddingServer = false, addServerError = "Server unreachable") }
            }
        }
    }

    private fun switchServer(profileId: String) {
        viewModelScope.launch {
            SessionManager.switchTo(profileId)
            userPrefs.setActiveProfile(profileId)
        }
    }

    private fun deleteServer(profileId: String) {
        viewModelScope.launch {
            userPrefs.removeProfile(profileId)
            SessionManager.profiles = SessionManager.profiles.filter { it.id != profileId }
        }
    }


    private fun logout() {
        viewModelScope.launch {
            userPrefs.clearAll()
            SessionManager.profiles = emptyList()
            SessionManager.activeIndex = 0
        }
    }
}