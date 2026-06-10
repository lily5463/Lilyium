package com.lily.lilyiumplayer.ui.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer
import com.lily.lilyiumplayer.ui.theme.subText


// ─────────────────────────────────────────────
// MediaTileWithInfo — image + title + artist
// Used for Albums and Songs
// ─────────────────────────────────────────────

@Composable
fun MediaTileWithInfo(
    coverUrl: String?,
    title: String?,
    artist: String?,
    imageSize: Dp,
    height: Dp,
    onClick: () -> Unit
) {
    var isImageLoading by remember { mutableStateOf(true) }
    val isDataLoading = title == null

    Column(
        modifier = Modifier
            .width(imageSize)
            .height(height)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageSize)
                .clip(RoundedCornerShape(16.dp))
                .placeholder(
                    visible = isImageLoading,
                    highlight = PlaceholderHighlight.shimmer(),
                )
        ) {
            AsyncImage(
                model = coverUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onLoading = { isImageLoading = true },
                onSuccess = { isImageLoading = false },
                onError = { isImageLoading = false },
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .basicMarquee(iterations = Int.MAX_VALUE)
                .placeholder(
                    visible = isDataLoading,
                    highlight = PlaceholderHighlight.shimmer(),
                )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = artist ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            color = subText,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .basicMarquee(iterations = Int.MAX_VALUE)
                .placeholder(
                    visible = isDataLoading,
                    highlight = PlaceholderHighlight.shimmer(),
                )
        )
    }
}
