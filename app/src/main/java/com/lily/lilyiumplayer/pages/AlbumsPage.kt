package com.lilyiumplayer.ui.albums

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.lily.lilyiumplayer.api.model.Album


@Composable
fun AlbumsPage(
    onAlbumClick: (String) -> Unit,
    viewModel: AlbumsViewModel = viewModel(),
    // topBar inset — pass WindowInsets.statusBars height so grid starts below the floating TopBar
    topBarHeightDp: Int = 64
) {
    val albums = viewModel.albums.collectAsLazyPagingItems()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            // Initial load — full-screen spinner
            albums.loadState.refresh is LoadState.Loading && albums.itemCount == 0 -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Error on first load
            albums.loadState.refresh is LoadState.Error && albums.itemCount == 0 -> {
                val e = (albums.loadState.refresh as LoadState.Error).error
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Couldn't load albums",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = e.localizedMessage ?: "Unknown error",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = { albums.retry() }) {
                        Text("Retry")
                    }
                }
            }

            else -> {

                AlbumsGrid(
                    albums = albums,
                    topBarHeightDp = topBarHeightDp,
                    getCoverArtUrl = viewModel::getCoverArtUrl,
                    onAlbumClick = onAlbumClick
                )
            }
        }
    }
}

@Composable
private fun AlbumsGrid(
    albums: LazyPagingItems<Album>,
    topBarHeightDp: Int,
    getCoverArtUrl: (String) -> String,
    onAlbumClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            top = 80.dp,
            start = 10.dp,
            end = 10.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            count = albums.itemCount,
            key = { index -> albums[index]?.id ?: index }
        ) { index ->
            val album = albums[index]
            if (album != null) {
                AlbumCard(
                    album = album,
                    coverArtUrl = getCoverArtUrl(album.coverArt ?: album.id),
                    onClick = { onAlbumClick(album.id) }
                )
            } else {
                AlbumCardPlaceholder()
            }
        }

        // Append load state — spinner row at the bottom
        when (albums.loadState.append) {
            is LoadState.Loading -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(strokeWidth = 2.dp)
                    }
                }
            }
            is LoadState.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Failed to load more",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = { albums.retry() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            else -> Unit
        }
    }
}

@Composable
private fun AlbumCard(
    album: Album,
    coverArtUrl: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        // Square cover art — fills column width
        AsyncImage(
            model = coverArtUrl,
            contentDescription = album.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = album.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = album.artist ?: "",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun AlbumCardPlaceholder() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {}
        }
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {}
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(10.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {}
    }
}