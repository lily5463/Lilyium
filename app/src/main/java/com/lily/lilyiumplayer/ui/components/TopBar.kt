package com.lily.lilyiumplayer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.lily.lilyiumplayer.viewModel.SessionAction
import com.lily.lilyiumplayer.viewModel.SessionState

@Composable
fun TopBar(
    modifier: Modifier,
//    isInSearch: Boolean,
    session: SessionState,                    // what to display
    onAction: (SessionAction) -> Unit,        // what to do
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    var showProfileMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
//        if (isInSearch) {
//            showProfileMenu = false
//        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .wrapContentWidth()
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Outlined.Search, contentDescription = "Search")
            }

            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings")
            }
            IconButton(onClick = { showProfileMenu = true }) {
                Icon(Icons.Outlined.Person, contentDescription = "Profile")
            }

            DropdownMenu(
                expanded = showProfileMenu,
                onDismissRequest = { showProfileMenu = false }
            ) {
                // Header — current user info
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = session.activeProfile?.username ?: "Not logged in",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = session.activeProfile?.label ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider()

                // Server list
                session.profiles.forEach { profile ->
                    val isActive = profile.id == session.activeProfile?.id
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            when (it) {
                                SwipeToDismissBoxValue.EndToStart -> {
                                    onAction(SessionAction.DeleteServer(profile.id))
                                    true  // keep dismissed (item is deleted)
                                }
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    onAction(SessionAction.ShowEditServerDialog(profile.id))
                                    false  // ← snap back, dialog opens instead
                                }
                                else -> false
                            }
                        }
                    )
                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.error)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ){
                                Text("Delete")
                            }
                        }
                    ) {
                        SwipeableServerItem(
                            profile = profile,
                            isActive = isActive,
                            onSwitch = {
                                if (!isActive) onAction(SessionAction.SwitchServer(profile.id))
                                showProfileMenu = false
                            },
                            onEdit = {
                                showProfileMenu = false
                                onAction(SessionAction.ShowEditServerDialog(profile.id))
                            },
                            onDelete = {
                                showProfileMenu = false
                                onAction(SessionAction.DeleteServer(profile.id))
                            }
                        )
                    }
                }
//                session.profiles.forEach { profile ->
//                    val isActive = profile.id == session.activeProfile?.id
//                    SwipeableServerItem(
//                        profile = profile,
//                        isActive = isActive,
//                        onSwitch = {
//                            if (!isActive) onAction(SessionAction.SwitchServer(profile.id))
//                            showProfileMenu = false
//                        },
//                        onEdit = {
//                            showProfileMenu = false
//                            onAction(SessionAction.ShowEditServerDialog(profile.id))
//                        },
//                        onDelete = {
//                            showProfileMenu = false
//                            onAction(SessionAction.DeleteServer(profile.id))
//                        }
//                    )
//                }

                HorizontalDivider()

                DropdownMenuItem(
                    text = { Text("Add server") },
                    leadingIcon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                    onClick = {
                        showProfileMenu = false
                        onAction(SessionAction.ShowAddServerDialog)
                    }
                )

                DropdownMenuItem(
                    text = { Text("Logout", color = MaterialTheme.colorScheme.error) },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Logout,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = {
                        showProfileMenu = false
                        onAction(SessionAction.Logout)
                    }
                )
            }
        }
    }

    if (session.showAddServerDialog) {
        AddServerDialog(
            isLoading = session.isAddingServer,
            errorMessage = session.addServerError,
            onDismiss = { onAction(SessionAction.DismissAddServerDialog) },
            onSubmit = { label, server, username, password ->
                onAction(
                    SessionAction.SubmitAddServer(label, server, username, password)
                )
            }
        )
    }

    session.isEditingProfile?.let { profile ->
        AddServerDialog(
            isLoading = session.isAddingServer,
            errorMessage = session.addServerError,
            prefill = profile,
            onDismiss = { onAction(SessionAction.DismissEditServerDialog) },
            onSubmit = { label, server, username, password ->
                onAction(
                    SessionAction.SubmitEditServer(profile.id, label, server, username, password)
                )
            }
        )
    }

}