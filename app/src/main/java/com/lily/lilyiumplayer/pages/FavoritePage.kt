package com.lily.lilyiumplayer.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lily.lilyiumplayer.data.model.QueueSong

import com.lily.lilyiumplayer.player.AudioPlayerManager
import com.lily.lilyiumplayer.ui.components.clickable.SongRow
import com.lily.lilyiumplayer.viewModel.HomeViewModel

@Composable
fun FavoritePage(modifier: Modifier = Modifier, onBackClick: () -> Unit) {
    val viewModel: HomeViewModel = viewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favorites = uiState.favorites

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(
                top = 80.dp,
                bottom = 120.dp,
                start = 10.dp,
                end = 10.dp
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(favorites, key = { it.id }) { song ->
                SongRow(
                    song = song,
                    onPlay = {
                        val queue = favorites.map { s ->
                            QueueSong(
                                id = s.id,
                                title = s.title,
                                artist = s.artist,
                                coverUrl = viewModel.getCoverArtUrl(s.coverArt ?: s.id),
                                streamUrl = viewModel.getStreamUrl(s.id)
                            )
                        }
                        val queueIndex = favorites.indexOfFirst { it.id == song.id }
                        AudioPlayerManager.playQueue(queue, queueIndex.coerceAtLeast(0))
                    },
                    onToggleFavourite = {  },
                    getCoverUrl = { viewModel.getCoverArtUrl(it) }
                )
            }
        }
    }
    Box(modifier = Modifier.fillMaxWidth()) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, start = 50.dp, bottom = 10.dp),
        )   {
            Icon(
                painter = painterResource(androidx.media3.session.R.drawable.media3_icon_album),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )

            Text(
                text = "All Albums",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
            )
        }

    }

}

// ─── Row, StarRating, MetaDot, helpers — same as before ──────────────────────

//@Composable
//private fun SongRow(
//    song: Song,
//    onPlay: () -> Unit,
//    onToggleFavourite: () -> Unit,
//    getCoverUrl: (String) -> String,
//) {
//    val isFavourite = song.starred != null
//    var heartBounce by remember { mutableStateOf(false) }
//    val heartScale by animateFloatAsState(
//        targetValue = if (heartBounce) 1.3f else 1f,
//        animationSpec = spring(stiffness = Spring.StiffnessHigh),
//        finishedListener = { heartBounce = false },
//        label = "heartScale"
//    )
//    val heartColor by animateColorAsState(
//        targetValue = if (isFavourite) MaterialTheme.colorScheme.error
//        else MaterialTheme.colorScheme.onSurfaceVariant,
//        label = "heartColor"
//    )
//
//    Surface(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(12.dp))
//            .clickable { onPlay() },
//        color = MaterialTheme.colorScheme.surface,
//        tonalElevation = 1.dp,
//        shape = RoundedCornerShape(12.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 10.dp, vertical = 8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(56.dp)
//                    .clip(RoundedCornerShape(8.dp))
//            ) {
//                AsyncImage(
//                    model = getCoverUrl(song.coverArt ?: song.id),
//                    contentDescription = song.title,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier.fillMaxSize()
//                )
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(
//                            Brush.verticalGradient(
//                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.2f))
//                            )
//                        )
//                )
//            }
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = song.title,
//                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//                Text(
//                    text = song.artist,
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(6.dp)
//                ) {
//                    Text(
//                        text = song.duration.toMinuteString(),
//                        style = MaterialTheme.typography.labelSmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                    song.playCount?.takeIf { it > 0 }?.let { count ->
//                        MetaDot()
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Icon(Icons.Outlined.PlayArrow, null, Modifier.size(11.dp),
//                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
//                            Spacer(Modifier.width(2.dp))
//                            Text(count.toShortString(), style = MaterialTheme.typography.labelSmall,
//                                color = MaterialTheme.colorScheme.onSurfaceVariant)
//                        }
//                    }
////                    song.userRating?.takeIf { it > 0 }?.let { StarRating(it) }
//                }
//            }
//
//            IconButton(
//                onClick = { heartBounce = true; onToggleFavourite() },
//                modifier = Modifier.size(36.dp)
//            ) {
//                Icon(
//                    imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
//                    contentDescription = if (isFavourite) "Unfavourite" else "Favourite",
//                    tint = heartColor,
//                    modifier = Modifier.scale(heartScale)
//                )
//            }
//        }
//    }
//}

@Composable
private fun StarRating(rating: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        repeat(5) { i ->
            Icon(Icons.Filled.Star, null, Modifier.size(11.dp),
                tint = if (i < rating) MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f))
        }
    }
}

@Composable
private fun MetaDot() {
    Box(
        modifier = Modifier.size(3.dp).clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
    )
}

private fun Long.toMinuteString() = "%d:%02d".format(this / 60, this % 60)
private fun Long.toShortString() = when {
    this >= 1_000_000 -> "%.1fM".format(this / 1_000_000f)
    this >= 1_000     -> "%.1fk".format(this / 1_000f)
    else              -> "$this"
}
