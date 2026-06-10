import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.lily.lilyiumplayer.player.AudioPlayerManager
import com.lily.lilyiumplayer.player.AudioPlayerManager.currentArtist
import com.lily.lilyiumplayer.player.AudioPlayerManager.currentCoverUrl
import com.lily.lilyiumplayer.player.AudioPlayerManager.currentSongId
import com.lily.lilyiumplayer.player.AudioPlayerManager.currentTitle
import kotlin.math.roundToInt

@Composable
fun MiniPlayer(
    onClick: () -> Unit,
) {
    val isPlaying = AudioPlayerManager.isPlaying
    val currentSong = currentSongId

    if (currentSong == null) return

    val title = currentTitle ?: ""
    val artist = currentArtist ?: ""
    val coverUrl = currentCoverUrl

    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        label = "swipeOffset"
    )

    val dismissThreshold = 500f
    val toggleThreshold = 500f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .offset { IntOffset(animatedOffset.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            offsetX < -dismissThreshold -> {
                                AudioPlayerManager.stop()
                                offsetX = 0f
                            }
                            offsetX > toggleThreshold -> {
                                AudioPlayerManager.togglePlayPause()
                                offsetX = 0f
                            }
                            else -> offsetX = 0f
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX += dragAmount
                    }
                )
            }
    ) {
        // Blurred background
        if (coverUrl != null) {
            AsyncImage(
                model = coverUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(20.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
        )

        // Content
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(0.dp))
                .clickable { onClick() }
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (coverUrl != null) {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray.copy(alpha = 0.4f))
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = artist,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            IconButton(
                onClick = { AudioPlayerManager.playPrevious() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(
                onClick = { AudioPlayerManager.togglePlayPause() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(
                onClick = { AudioPlayerManager.playNext() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Pause
//import androidx.compose.material.icons.filled.PlayArrow
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import coil3.compose.AsyncImage
//
//@Composable
//fun MiniPlayer(
//    title: String,
//    artist: String,
//    coverUrl: String?,
//    isPlaying: Boolean,
//    onPlayPause: () -> Unit,
//    onClick: () -> Unit
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(64.dp)
//            .clickable { onClick() }
//            .background(MaterialTheme.colorScheme.surface)
//            .padding(horizontal = 12.dp, vertical = 8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        if (coverUrl != null) {
//            AsyncImage(
//                model = coverUrl,
//                contentDescription = title,
//                modifier = Modifier
//                    .size(48.dp)
//                    .clip(RoundedCornerShape(8.dp))
//            )
//        } else {
//            Box(
//                modifier = Modifier
//                    .size(48.dp)
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(Color.Gray)
//            )
//        }
//
//        Spacer(modifier = Modifier.width(12.dp))
//
//        Column(
//            modifier = Modifier.weight(1f)
//        ) {
//            Text(
//                text = title,
//                maxLines = 1
//            )
//            Text(
//                text = artist,
//                maxLines = 1
//            )
//        }
//
//        IconButton(onClick = onPlayPause) {
//            Icon(
//                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
//                contentDescription = "Play Pause"
//            )
//        }
//    }
//}