package com.lilyiumplayer.ui.artist

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.lily.lilyiumplayer.api.model.Artist
import com.lily.lilyiumplayer.api.model.Index


@Composable
fun ArtistPage(
    onArtistClick: (String) -> Unit,
    viewModel: ArtistViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> ArtistLoadingState()
        uiState.error != null -> ArtistErrorState(
            message = uiState.error!!,
            onRetry = viewModel::refresh
        )
        else -> ArtistList(
            indexes = uiState.indexes,
            starringId = uiState.starringId,
            onArtistClick = onArtistClick,
            getCoverArtUrl = viewModel::getCoverArtUrl
        )
    }
}

@Composable
private fun ArtistList(
    indexes: List<Index>,
    starringId: String?,
    onArtistClick: (String) -> Unit,
    getCoverArtUrl: (String) -> String
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 30.dp,
            start = 10.dp,
            end = 10.dp
        ),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
            ) {
                Icon(
                    painter = painterResource(androidx.media3.session.R.drawable.media3_icon_artist),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )

                Text(
                    text = "All Artist",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        indexes.forEach { index ->
            stickyHeader(key = "header_${index.name}") {
                ArtistIndexHeader(letter = index.name)
            }

            // Pair artists into rows of 2 for the grid
            val rows = index.artist.chunked(2)
            items(
                items = rows,
                key = { row -> "row_${row.first().id}" }
            ) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    row.forEach { artist ->
                        ArtistCard(
                            modifier = Modifier.weight(1f),
                            artist = artist,
                            onClick = { onArtistClick(artist.id) }
                        )
                    }
                    // Fill empty slot if odd number of artists in group
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtistIndexHeader(letter: String) {
    // Match SongPage's content horizontal padding (10.dp) but bleed full width as sticky
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Offset the parent LazyColumn's horizontal padding so header is edge-to-edge
            .offset(x = (-10).dp)
            .width(LocalConfiguration.current.screenWidthDp.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = letter,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.2.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ArtistCard(
    artist: Artist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                val imageUrl: String? = null
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "${artist.name} cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                )
                if (artist.albumCount > 0) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${artist.albumCount} ${if (artist.albumCount == 1) "album" else "albums"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ── Loading / Error states ────────────────────────────────────────────────────

@Composable
private fun ArtistLoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ArtistErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Couldn't load artists", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}