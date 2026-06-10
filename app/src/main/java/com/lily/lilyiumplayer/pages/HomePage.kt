package com.lily.lilyiumplayer.pages

import com.lily.lilyiumplayer.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lily.lilyiumplayer.data.model.QueueSong
import com.lily.lilyiumplayer.player.AudioPlayerManager
import com.lily.lilyiumplayer.ui.components.MediaTileWithInfo
import com.lily.lilyiumplayer.viewModel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlin.collections.map

@Composable
fun HomePage(
    modifier: Modifier,
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val albums = uiState.albums
    val recentlyAddedAlbums = uiState.recentlyAddedAlbum
    val recentlyPlayedAlbums = uiState.recentlyPlayedAlbum
    val randomAlbum = uiState.randomAlbum

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {

        // ── Header ──────────────────────────────────────────────────────────
        item {
            Icon(
                painter = painterResource(R.drawable.liyliumlogotrans),
                contentDescription = "Lilyium",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // ── Quick actions ────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Track Mix
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    onClick = {
                        val randomSongs = uiState.randomSongs
                        val queue = randomSongs.map { song ->
                            QueueSong(
                                id = song.id,
                                title = song.title,
                                artist = song.artist,
                                coverUrl = viewModel.getCoverArtUrl(song.id),
                                streamUrl = viewModel.getStreamUrl(song.id)
                            )
                        }
                        AudioPlayerManager.playQueue(queue, 1)
                        viewModel.refreshRandomSongs()
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.nightlife_24dp_e3e3e3_fill0_wght300_grad0_opsz24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Track Mix",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Queues
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { navController.navigate("favorites") }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(androidx.media3.session.R.drawable.media3_icon_heart_filled),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Favorites",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        // ── Rediscover ───────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            SectionHeader(
                title = "Rediscover",
                onClick = { /* navigate */ }
            )
            Spacer(Modifier.height(12.dp))

            val rediscoverState = rememberLazyListState()
            LaunchedEffect(Unit) {
                while (true) {
                    delay(25L)
                    if (rediscoverState.isScrollInProgress) {
                        // wait until user lifts finger
                        snapshotFlow { rediscoverState.isScrollInProgress }
                            .filter { !it }
                            .first()
                        delay(3000L) // pause 3 seconds after user stops
                    } else {
                        val scrolled = rediscoverState.scrollBy(1f)
                        if (scrolled == 0f) { // reached the end
                            rediscoverState.scrollToItem(0) // jump back to start
                        }
                    }
                }
            }
            LazyRow (
                state = rediscoverState,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                if (uiState.isLoading) {
                    items(6) {
                        MediaTileWithInfo(
                            coverUrl = null,
                            title = "Loading...",
                            artist = null,
                            imageSize = 169.dp,
                            height = 224.dp,
                            onClick = {}
                        )
                    }
                } else {
                    items(randomAlbum) { album ->
                        MediaTileWithInfo (
                            coverUrl = viewModel.getCoverArtUrl(album.id),
                            title = album.title,
                            artist = album.artist,
                            imageSize = 169.dp,
                            height = 224.dp,
                            onClick = { navController.navigate("albumDetail/${album.id}") }
                        )
                    }
                }
            }
        }


        // ── Recently Listened ────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            SectionHeader(
                title = "Recently Listened",
                onClick = { /* TODO: add navigation to its screen that display all */ }
            )
            Spacer(Modifier.height(12.dp))

            val listState = rememberLazyListState()

            // Scroll to end once data is ready
            LaunchedEffect(albums.size) {
                if (albums.isNotEmpty()) {
                    listState.scrollToItem(albums.size - 1)
                }
            }

            // Auto-scroll loop restarts whenever albums change
            LaunchedEffect(albums.size) {
                if (albums.isEmpty()) return@LaunchedEffect

                while (true) {
                    delay(25L)

                    if (listState.isScrollInProgress) {
                        snapshotFlow { listState.isScrollInProgress }
                            .filter { !it }
                            .first()
                        delay(3_000L)
                    } else {
                        val scrolled = listState.scrollBy(-1f)
                        if (scrolled == 0f) {
                            listState.scrollToItem(albums.size - 1)
                        }
                    }
                }
            }

            LazyRow (
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                items(recentlyPlayedAlbums) { item ->
                    MediaTileWithInfo(
                        coverUrl = viewModel.getCoverArtUrl(item.id),
                        title = item.title,
                        artist = item.artist,
                        imageSize = 130.dp,
                        height = 180.dp,
                        onClick = { navController.navigate("albumDetail/${item.id}") }
                    )
                }
            }
        }

        // ── Recently Added ───────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(12.dp))
            SectionHeader(
                title = "Recently Added",
                onClick = { /* navigate */ }
            )
            Spacer(Modifier.height(12.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                if (uiState.isLoading) {
                    items(6) {
                        MediaTileWithInfo(
                            coverUrl = null,
                            title = "Loading...",
                            artist = null,
                            imageSize = 200.dp,
                            height = 250.dp,
                            onClick = {}
                        )
                    }
                } else {
                    items(recentlyAddedAlbums) { album ->
                        MediaTileWithInfo (
                            coverUrl = viewModel.getCoverArtUrl(album.id),
                            title = album.title,
                            artist = album.artist,
                            imageSize = 200.dp,
                            height = 250.dp,
                            onClick = { navController.navigate("albumDetail/${album.id}") }
                        )
                    }
                }
            }
        }
    }
}

// ── Reusable section header ──────────────────────────────────────────────────
@Composable
private fun SectionHeader(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.NavigateNext,
            contentDescription = "See all",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
    }
}