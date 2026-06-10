//package com.lily.lilyiumplayer.ui.components
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.SwipeToDismissBoxState
//import androidx.compose.material3.SwipeToDismissBoxValue
//import androidx.compose.material3.rememberSwipeToDismissBoxState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.flow.callbackFlow
//
//@Composable
//fun <T> SwipeToDeleteContainer(
//    item: T,
//    onDelete: (T) -> Unit,
//    animationDuration: Int = 500,
//    content: @Composable (T) -> Unit
//) {
//    var isRemoved by remember {
//        mutableSetOf(false)
//    }
//    val state = rememberSwipeToDismissBoxState(
//        confirmValueChange = { value ->
//            if (value == SwipeToDismissBoxValue.EndToStart) {
//                isRemoved = true
//                true
//            } else {
//                false
//            }
//        }
//    )
//    SwipeToDismiss(
//        state = state,
//        background = {
//            DeleteBackground(dismissBoxState = state)
//        },
//
//        dismissContent = { content(item) }
//    )
//}
//
//@Composable
//fun DeleteBackground(
//    dismissBoxState: SwipeToDismissBoxState
//) {
//    val color = if (dismissBoxState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
//        MaterialTheme.colorScheme.onError
//    } else Color.Transparent
//
//    Box(
//        modifier = Modifier.fillMaxSize()
//            .background(color)
//            .padding(16.dp)
//
//
//    ) {
//        Icon(
//            imageVector = Icons.Default.Delete,
//            contentDescription = "delete server",
//            tint = Color.White
//        )
//    }
//
//}