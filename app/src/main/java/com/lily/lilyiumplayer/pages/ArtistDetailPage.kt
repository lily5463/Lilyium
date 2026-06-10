package com.lilyiumplayer.ui.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.lily.lilyiumplayer.api.model.Album
import com.lily.lilyiumplayer.api.model.Artist


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailPage(
    artistId: String,
    onAlbumClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ArtistDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.artist?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            uiState.error != null -> {
                ArtistDetailError(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    message = uiState.error!!,
                    onRetry = viewModel::refresh
                )
            }
            uiState.artist != null -> {
                ArtistDetailContent(
                    modifier = Modifier.padding(innerPadding),
                    artist = uiState.artist!!,
                    starringId = uiState.starringId,
                    onAlbumClick = onAlbumClick,
                    onAlbumStar = viewModel::toggleAlbumStar,
                    getCoverArtUrl = viewModel::getCoverArtUrl
                )
            }
        }
    }
}

@Composable
private fun ArtistDetailContent(
    modifier: Modifier = Modifier,
    artist: Artist,
    starringId: String?,
    onAlbumClick: (String) -> Unit,
    onAlbumStar: (Album) -> Unit,
    getCoverArtUrl: (String) -> String
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // ── Full-width header ────────────────────────────────────────────────
        item(span = { GridItemSpan(2) }) {
            ArtistHeader(
                artist = artist,
                getCoverArtUrl = getCoverArtUrl
            )
        }

        // ── "Albums" section title ───────────────────────────────────────────
        if (artist.album.isNotEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "Albums",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            // ── Album grid items ─────────────────────────────────────────────
            items(
                items = artist.album,
                key = { it.id }
            ) { album ->
                AlbumGridCard(
                    album = album,
                    isStarring = starringId == album.id,
                    getCoverArtUrl = getCoverArtUrl,
                    onClick = { onAlbumClick(album.id) },
                    onToggleStar = { onAlbumStar(album) }
                )
            }
        }
    }
}

// ── Artist header ────────────────────────────────────────────────────────────

@Composable
private fun ArtistHeader(
    artist: Artist,
    getCoverArtUrl: (String) -> String
) {
    Column {
        // Hero image from last.fm (largeImageUrl) or placeholder
        val heroImage = artist.coverArt

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
                AsyncImage(
                    model = getCoverArtUrl(artist.coverArt ?: artist.id),
                    contentDescription = artist.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

            // Scrim gradient at bottom for text legibility
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(80.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, MaterialTheme.colorScheme.surface)
                        )
                    )
            )
        }

        // Stats row
        ArtistStatsRow(artist = artist)
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "kdet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    }


@Composable
private fun ArtistStatsRow(artist: Artist) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (artist.albumCount > 0) {
            StatItem(
                value = artist.albumCount.toString(),
                label = if (artist.albumCount == 1) "Album" else "Albums"
            )
        }
        // Total songs = sum across albums
        val totalSongs = artist.album.sumOf { it.songCount?.toInt() ?: 0  }
        if (totalSongs > 0) {
            StatItem(value = totalSongs.toString(), label = "Songs")
        }
        if (artist.userRating != null) {
            StatItem(value = "★ ${artist.userRating}/5", label = "Rating")
        }
        if (artist.starred != null) {
            StatItem(value = "♥", label = "Favourite")
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ── Album grid card ──────────────────────────────────────────────────────────

@Composable
private fun AlbumGridCard(
    album: Album,
    isStarring: Boolean,
    getCoverArtUrl: (String) -> String,
    onClick: () -> Unit,
    onToggleStar: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
    ) {
        // Cover art
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (album.coverArt != null) {
                AsyncImage(
                    model = getCoverArtUrl(album.coverArt),
                    contentDescription = album.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
            }

            // Star overlay (top-right)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                if (isStarring) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                CircleShape
                            )
                            .padding(3.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75f))
                            .clickable(onClick = onToggleStar),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (album.starred != null) Icons.Filled.Favorite
                            else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (album.starred != null) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = album.title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (album.year != null) {
                Text(
                    text = album.year.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "·",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${album.songCount} songs",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Error state ──────────────────────────────────────────────────────────────

@Composable
private fun ArtistDetailError(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Couldn't load artist", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}