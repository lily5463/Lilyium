package com.lily.lilyiumplayer.pages

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.lily.lilyiumplayer.player.AudioPlayerManager
import com.lily.lilyiumplayer.ui.icons.AppIcons
import ir.mahozad.multiplatform.wavyslider.WaveDirection.HEAD
import ir.mahozad.multiplatform.wavyslider.material3.WaveAnimationSpecs
import ir.mahozad.multiplatform.wavyslider.material3.WavySlider
import kotlinx.coroutines.delay

private data class WaveStyle(val height: Dp, val length: Dp, val velocity: Dp)

private val waveStyles = listOf(
    WaveStyle(14.dp, 20.dp, 12.dp),
    WaveStyle(6.dp,  40.dp, 6.dp),
    WaveStyle(20.dp, 15.dp, 18.dp),
    WaveStyle(8.dp,  30.dp, 8.dp),
    WaveStyle(16.dp, 25.dp, 14.dp),
)

@Composable
fun NowPlayingPage(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val title = AudioPlayerManager.currentTitle ?: "No song"
    val artist = AudioPlayerManager.currentArtist ?: "Unknown artist"
    val coverUrl = AudioPlayerManager.currentCoverUrl
    val isPlaying = AudioPlayerManager.isPlaying
    val isBuffering = AudioPlayerManager.isBuffering
    val progress = AudioPlayerManager.progress
    val duration = AudioPlayerManager.duration
    val currentPosition = AudioPlayerManager.currentPosition

    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    var styleIndex by remember { mutableStateOf(0) }
    val style = waveStyles[styleIndex]

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(10_000L)
            styleIndex = (styleIndex + 1) % waveStyles.size
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "bgRotation")
    val bgAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bgAngle"
    )

    BackHandler { onBackClick() }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Blurred album art background ──────────────────────────────────
        if (coverUrl != null) {
            AsyncImage(
                model = coverUrl,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .requiredSize(1080.dp)
                    .align(Alignment.TopCenter)
                    .graphicsLayer { rotationZ = bgAngle }
                    .blur(69.dp)
            )
        }

        // Dark scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isDark)
                        MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f)
                    else
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(8.dp))

            // ── Top bar ───────────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onBackClick,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Now Playing",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(Modifier.height(32.dp))

            // ── Album art ─────────────────────────────────────────────────
            if (coverUrl != null) {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(20.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Title + artist + heart ────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.basicMarquee()
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = artist,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.basicMarquee()
                    )
                }

                Spacer(Modifier.width(12.dp))

                IconButton(onClick = { /* toggle star */ }) {
                    Icon(
                        Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favourite",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Progress ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isBuffering) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    WavySlider(
                        value = progress,
                        onValueChange = { AudioPlayerManager.seekTo(it) },
                        modifier = Modifier.fillMaxWidth(),
                        waveLength = style.length,
                        waveHeight = if (isPlaying) style.height else 0.dp,
                        waveVelocity = style.velocity to HEAD,
                        waveThickness = 4.dp,
                        trackThickness = 4.dp,
                        incremental = false,
                        animationSpecs = SliderDefaults.WaveAnimationSpecs.copy(
                            waveHeightAnimationSpec = tween(2000),
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    currentPosition.msToTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    duration.msToTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Playback controls ─────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        painterResource(AppIcons.Shuffle),
                        contentDescription = "Shuffle",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                FilledTonalIconButton(
                    onClick = { AudioPlayerManager.playPrevious() },
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }

                FilledIconButton(
                    onClick = { AudioPlayerManager.togglePlayPause() },
                    modifier = Modifier.size(68.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(36.dp)
                    )
                }

                FilledTonalIconButton(
                    onClick = { AudioPlayerManager.playNext() },
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }

                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

private fun Long.msToTime(): String {
    val s = this / 1000
    return "%d:%02d".format(s / 60, s % 60)
}