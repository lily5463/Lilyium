//package com.lily.lilyiumplayer.viewModel
//
//import androidx.compose.foundation.text.input.TextFieldState
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.lily.lilyiumplayer.data.UserPreferences
//import com.lily.lilyiumplayer.util.LoginState
//import com.lily.lilyiumplayer.util.SessionManager
//import com.lily.lilyiumplayer.util.SessionManager.session
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import paige.navic.data.models.User
//import paige.navic.data.session.SessionManager
//import paige.navic.utils.LoginState
//
//class LoginViewModel : ViewModel() {
//    private val _loginState = MutableStateFlow<LoginState<User?>>(LoginState.LoggedOut)
//    val loginState: StateFlow<LoginState<User?>> = _loginState.asStateFlow()
//
//    val instanceState = TextFieldState()
//    val usernameState = TextFieldState()
//    val passwordState = TextFieldState()
//
//    init {
//        loadUser()
//    }
//
////    fun loadUser() {
////        viewModelScope.launch {
////            val user = SessionManager.currentUser
////            if (user != null) {
////                _loginState.value = LoginState.Success(user)
////            } else {
////                _loginState.value = LoginState.LoggedOut
////            }
////        }
////    }
//
//    fun login() {
//        viewModelScope.launch {
//            _loginState.value = LoginState.Loading
//            _loginState.value = try {
//                SessionManager.login(
//                    instanceState.text.toString().let {
//                        if (!it.startsWith("https://") && !it.startsWith("http://"))
//                            "https://$it"
//                        else it
//                    },
//                    usernameState.text.toString(),
//                    passwordState.text.toString()
//                )
//                if (session = null) {
//                    LoginState.LoggedIn(SessionManager.session)
//                } else {
//                    throw Exception("currentUser is null")
//                }
//            } catch (e: Exception) {
//                LoginState.Error(e)
//            }
//        }
//    }
//
//    fun logout() {
//        viewModelScope.launch {
//            SessionManager.logout()
//            _loginState.value = LoginState.LoggedOut
//        }
//    }
//}