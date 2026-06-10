package com.lily.lilyiumplayer.ui.components.clickable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.lily.lilyiumplayer.api.model.Song


@Composable
fun SongRow(
    song: Song,
    onPlay: () -> Unit,
    onToggleFavourite: () -> Unit,
    getCoverUrl: (String) -> String,
    modifier: Modifier = Modifier,
    showCover: Boolean = true,
    tonalElevation: Dp = 1.dp,
) {
    val isFavourite = song.starred != null

    val heartScale by animateFloatAsState(
        targetValue = if (isFavourite) 1.2f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "heartScale"
    )

    val heartColor by animateColorAsState(
        targetValue = if (isFavourite) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "heartColor"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onPlay() },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = tonalElevation,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Cover art ──────────────────────────────────────────────
            if (showCover) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = getCoverUrl(song.coverArt ?: song.id),
                        contentDescription = song.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Subtle vignette so white album art doesn't blow out
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.18f))
                                )
                            )
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            // ── Text block ─────────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                )
                Spacer(modifier = Modifier.height(4.dp))
                SongMeta(song = song)
            }


            // ── Heart ──────────────────────────────────────────────────
            IconButton(
                onClick = {onToggleFavourite() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFavourite) "Unfavourite" else "Favourite",
                    tint = heartColor.copy(alpha = if (isFavourite) 1f else 0.5f),
                    modifier = Modifier
                        .scale(heartScale)
                        .size(18.dp)
                )
            }
        }
    }
}

// ── Internal helpers ───────────────────────────────────────────────────────────

@Composable
private fun SongMeta(song: Song) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = song.duration.toMinuteString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        song.playCount?.takeIf { it > 0 }?.let { count ->
            MetaDot()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.PlayArrow, null,
                    Modifier.size(11.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    count.toShortString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MetaDot() {
    Box(
        modifier = Modifier
            .size(3.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
    )
}

private fun Long.toMinuteString() = "%d:%02d".format(this / 60, this % 60)
private fun Long.toShortString() = when {
    this >= 1_000_000 -> "%.1fM".format(this / 1_000_000f)
    this >= 1_000     -> "%.1fk".format(this / 1_000f)
    else              -> "$this"
}