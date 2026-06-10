package com.lily.lilyiumplayer.pages

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.lily.lilyiumplayer.api.model.Song
import com.lily.lilyiumplayer.data.model.QueueSong
import com.lily.lilyiumplayer.player.AudioPlayerManager
import com.lily.lilyiumplayer.ui.components.clickable.SongRow
import com.lily.lilyiumplayer.ui.icons.AppIcons
import com.lily.lilyiumplayer.viewModel.AlbumDetailViewModel
import kotlin.collections.map

@Composable
fun InsideAlbumPage(
    albumId: String,
    onBackClick: () -> Unit,
    viewModel: AlbumDetailViewModel = viewModel()
) {
    LaunchedEffect(albumId) { viewModel.loadAlbum(albumId) }

    val album = viewModel.album

    if (album == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val songs = album.song ?: emptyList()
    val queue = remember(songs) {
        songs.toQueue(viewModel::getCoverArtUrl, viewModel::getStreamUrl)
    }

    val listState = rememberLazyListState()
    val heroHeightDp = 360.dp

    // Outer Box — required so the floating back button can use .align()
    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {

            // ── Hero ──────────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(heroHeightDp)
                ) {
                    // Blurred art as hero background
                    AsyncImage(
                        model = viewModel.getCoverArtUrl(album.coverArt ?: albumId),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(32.dp)
                    )
                    // Dark tint over blur
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.55f))
                    )
                    // Crisp centred album art card
                    AsyncImage(
                        model = viewModel.getCoverArtUrl(album.coverArt ?: albumId),
                        contentDescription = album.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(269.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .align(Alignment.Center)

                    )
                    // Gradient scrim fading into background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.background
                                    )
                                )
                            )
                    )
                }
            }

            // ── Album header ──────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = album.name,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = album.artist ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    val meta = buildString {
                        album.year?.let { append(it) }
                        album.songCount?.let {
                            if (isNotEmpty()) append("  ·  ")
                            append("$it songs")
                        }
                    }
                    if (meta.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = meta,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // ── Action row ────────────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledIconButton(
                            onClick = { AudioPlayerManager.playQueue(queue, 0) },
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                painter = painterResource(AppIcons.Play),
                                contentDescription = "Play",
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        FilledTonalIconButton(
                            onClick = { AudioPlayerManager.playQueue(queue.shuffled(), 0) },
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape
                        ) {
                            Icon(
                                painter = painterResource(AppIcons.Shuffle),
                                contentDescription = "Shuffle",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        FilledTonalIconButton(
                            onClick = { /* add to playlist */ },
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape
                        ) {
                            Icon(
                                painter = painterResource(AppIcons.PlaylistAdd),
                                contentDescription = "Add to playlist",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        IconButton(onClick = { /* toggle star */ }) {
                            Icon(
                                painter = painterResource(AppIcons.Heart),
                                contentDescription = "Favourite",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = { /* more options */ }) {
                            Icon(
                                painter = painterResource(AppIcons.MoreOptionsVirt),
                                contentDescription = "More options",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }

            // ── Song list ─────────────────────────────────────────────────
            items(songs, key = { it.id }) { song ->
                val index = songs.indexOf(song)
                SongRow(
                    song = song,
                    onPlay = { AudioPlayerManager.playQueue(queue, index) },
                    onToggleFavourite = { viewModel.toggleFavourite(song) },
                    getCoverUrl = { id -> viewModel.getCoverArtUrl(id) },
                    showCover = false,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 1.dp)
                )
            }
        }

        // ── Floating back button — inside Box so .align() works ───────────
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
        }
    }
}

private fun List<Song>.toQueue(
    getCoverUrl: (String) -> String,
    getStreamUrl: (String) -> String
) = map { song ->
    QueueSong(
        id = song.id,
        title = song.title,
        artist = song.artist ?: "",
        coverUrl = getCoverUrl(song.coverArt ?: song.id),
        streamUrl = getStreamUrl(song.id)
    )
}
