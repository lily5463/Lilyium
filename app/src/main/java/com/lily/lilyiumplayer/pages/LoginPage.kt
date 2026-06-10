package com.lily.lilyiumplayer.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lily.lilyiumplayer.ui.components.AuthTextField
import com.lily.lilyiumplayer.ui.components.PrimaryButton
import com.lily.lilyiumplayer.util.LoginResult
import com.lily.lilyiumplayer.util.LoginUtil
import kotlinx.coroutines.launch

@Composable
@Preview
fun LoginPage() {
    val context = LocalContext.current
    val loginUtil = remember { LoginUtil(context.applicationContext) }
    val scope = rememberCoroutineScope()

    var label by remember { mutableStateOf("") }
    var serverAddress by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.CenterVertically)
        ) {
            // Label is optional — falls back to the server address if left empty
            AuthTextField(
                label = "Server Name (optional)",
                value = label,
                onValueChange = { label = it },
                placeHolder = "e.g. Home, Tailscale",
            )

            AuthTextField(
                label = "Server Address",
                value = serverAddress,
                onValueChange = { serverAddress = it },
                placeHolder = "http://192.168.0.1:4533",
            )

            AuthTextField(
                label = "Username",
                value = username,
                onValueChange = { username = it },
                placeHolder = "username",
            )

            AuthTextField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                placeHolder = "password",
            )

            PrimaryButton(
                loading = loading,
                label = "Login",
                onClick = {
                    scope.launch {
                        loading = true
                        val serverLabel = label.ifBlank { serverAddress } // fallback to URL if no name given
                        when (loginUtil.login(serverAddress, serverLabel, username, password)) {
                            LoginResult.Success -> {
                                // MainActivity observes DataStore and navigates automatically
                            }
                            LoginResult.InvalidCredentials -> {
                                loading = false
                                Toast.makeText(context, "Wrong username or password", Toast.LENGTH_SHORT).show()
                            }
                            LoginResult.ServerUnreachable -> {
                                loading = false
                                Toast.makeText(context, "Cannot reach server. Check IP and port.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )
        }
    }
}