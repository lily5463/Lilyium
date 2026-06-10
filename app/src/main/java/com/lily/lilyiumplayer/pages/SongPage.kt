package com.lily.lilyiumplayer.pages

import com.lily.lilyiumplayer.R;
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.NavigateNext
import androidx.compose.material.icons.automirrored.outlined.Note
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.lily.lilyiumplayer.api.model.Song
import com.lily.lilyiumplayer.data.model.QueueSong

import com.lily.lilyiumplayer.player.AudioPlayerManager
import com.lily.lilyiumplayer.ui.components.clickable.SongRow
import com.lily.lilyiumplayer.ui.icons.AppIcons
import com.lily.lilyiumplayer.viewModel.SongsViewModel

@Composable
fun SongPage(modifier: Modifier = Modifier) {
    val viewModel: SongsViewModel = viewModel()
    val songs = viewModel.songs.collectAsLazyPagingItems()

    Box(modifier = modifier.fillMaxSize()) {
        when {
            // Initial full-screen load
            songs.loadState.refresh is LoadState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            // Initial load error
            songs.loadState.refresh is LoadState.Error -> {
                val e = (songs.loadState.refresh as LoadState.Error).error
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(e.message ?: "Something went wrong", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { songs.retry() }) { Text("Retry") }
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 10.dp,
                        end = 10.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Song rows — itemKey keeps recompositions minimal
                    item{
                        Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp, bottom = 10.dp),
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
                    items(
                        count = songs.itemCount,
                        key = songs.itemKey { it.id }
                    ) { index ->
                        val song = songs[index] ?: return@items
                        SongRow(
                            song = song,
                            onPlay = {
                                // Build queue from already-loaded snapshot
                                // (only pages fetched so far — perfectly fine)
                                val snapshot = songs.itemSnapshotList.items
                                val queue = snapshot.map { s ->
                                    QueueSong(
                                        id = s.id,
                                        title = s.title,
                                        artist = s.artist,
                                        coverUrl = viewModel.getCoverArtUrl(s.coverArt ?: s.id),
                                        streamUrl = viewModel.getStreamUrl(s.id)
                                    )
                                }
                                val queueIndex = snapshot.indexOfFirst { it.id == song.id }
                                AudioPlayerManager.playQueue(queue, queueIndex.coerceAtLeast(0))
                            },
                            onToggleFavourite = { viewModel.toggleFavourite(song) },
                            getCoverUrl = { viewModel.getCoverArtUrl(it) }
                        )
                    }

                    // Spinner at the bottom while loading next page
                    if (songs.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(28.dp))
                            }
                        }
                    }

                    // Retry button if an append (mid-scroll) fetch fails
                    if (songs.loadState.append is LoadState.Error) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(onClick = { songs.retry() }) { Text("Load more") }
                            }
                        }
                    }
                }
            }
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
