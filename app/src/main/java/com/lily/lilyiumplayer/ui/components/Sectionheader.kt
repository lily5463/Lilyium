package com.lily.lilyiumplayer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SectionHeader(
    title: String,
    destionation: Unit
) {
    Row (
        modifier = Modifier.fillMaxWidth()
            .clickable{ destionation}
    ){
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge)
    }
}