package com.lily.lilyiumplayer.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.lily.lilyiumplayer.data.model.QueueSong
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AudioPlayerManager {
    private var controller: MediaController? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    var currentSongId by mutableStateOf<String?>(null)
        private set

    var currentTitle by mutableStateOf<String?>(null)
        private set

    var currentArtist by mutableStateOf<String?>(null)
        private set

    var currentCoverUrl by mutableStateOf<String?>(null)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var hasSong by mutableStateOf(false)
        private set

    var progress by mutableStateOf(0f)
        private set

    var duration by mutableStateOf(0L)
        private set

    var currentPosition by mutableStateOf(0L)
        private set

    var isBuffering by mutableStateOf(false)
        private set

    private fun startProgressTicker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (true) {
                controller?.let {
                    duration = it.duration.takeIf { d -> d > 0 } ?: 0L
                    currentPosition = it.currentPosition.coerceAtLeast(0L)
                    progress = if (duration > 0) currentPosition / duration.toFloat() else 0f
                }
                delay(100)
            }
        }
    }

    private fun stopProgressTicker() {
        progressJob?.cancel()
        progressJob = null
    }

    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            val mediaItem = player.currentMediaItem
            val metadata = mediaItem?.mediaMetadata

            currentSongId = mediaItem?.mediaId
            currentTitle = metadata?.title?.toString()
            currentArtist = metadata?.artist?.toString()
            currentCoverUrl = metadata?.artworkUri?.toString()
            isPlaying = player.isPlaying
            hasSong = player.mediaItemCount > 0
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            isBuffering = playbackState == Player.STATE_BUFFERING
        }

        override fun onIsPlayingChanged(playing: Boolean) {
            isPlaying = playing
            if (playing) startProgressTicker() else stopProgressTicker()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val metadata = mediaItem?.mediaMetadata
            currentSongId = mediaItem?.mediaId
            currentTitle = metadata?.title?.toString()
            currentArtist = metadata?.artist?.toString()
            currentCoverUrl = metadata?.artworkUri?.toString()
            hasSong = mediaItem != null
            // Reset progress on track change
            progress = 0f
            currentPosition = 0L
            duration = 0L
        }
    }

    fun init(context: Context, onReady: (() -> Unit)? = null) {
        if (controller != null) {
            onReady?.invoke()
            return
        }

        val sessionToken = SessionToken(
            context.applicationContext,
            ComponentName(context.applicationContext, PlaybackService::class.java)
        )

        val controllerFuture = MediaController.Builder(context.applicationContext, sessionToken).buildAsync()

        controllerFuture.addListener({
            controller = controllerFuture.get()
            controller?.addListener(playerListener)

            controller?.let { player ->
                val mediaItem = player.currentMediaItem
                val metadata = mediaItem?.mediaMetadata

                currentSongId = mediaItem?.mediaId
                currentTitle = metadata?.title?.toString()
                currentArtist = metadata?.artist?.toString()
                currentCoverUrl = metadata?.artworkUri?.toString()
                isPlaying = player.isPlaying
                hasSong = player.mediaItemCount > 0

                if (player.isPlaying) startProgressTicker()
            }

            onReady?.invoke()
        }, MoreExecutors.directExecutor())
    }

    private fun QueueSong.toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id)
            .setUri(streamUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setArtworkUri(coverUrl?.toUri())
                    .build()
            )
            .build()
    }

    fun playSong(
        id: String,
        title: String,
        artist: String?,
        coverUrl: String?,
        streamUrl: String
    ) {
        val mediaItem = MediaItem.Builder()
            .setMediaId(id)
            .setUri(streamUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setArtworkUri(coverUrl?.let { Uri.parse(it) })
                    .build()
            )
            .build()

        controller?.apply {
            setMediaItems(listOf(mediaItem), 0, 0L)
            prepare()
            play()
        }
    }

    fun playQueue(songs: List<QueueSong>, startIndex: Int) {
        if (songs.isEmpty()) return
        Log.d("AudioPlayerManager", "playQueue: controller=$controller, songs=${songs.size}")

        val mediaItems = songs.map { it.toMediaItem() }

        controller?.apply {
            setMediaItems(mediaItems, startIndex, 0L)
            prepare()
            play()
        }
    }

    fun seekTo(fraction: Float) {
        val posMs = (fraction * duration).toLong()
        isBuffering = true          // optimistic: show loading immediately
        controller?.seekTo(posMs)
    }

    fun playNext() {
        controller?.seekToNextMediaItem()
    }

    fun playPrevious() {
        controller?.seekToPreviousMediaItem()
    }

    fun pause() {
        controller?.pause()
    }

    fun resume() {
        controller?.play()
    }

    fun togglePlayPause() {
        controller?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun stop() {
        stopProgressTicker()
        controller?.stop()

        isPlaying = false
        hasSong = false
        currentSongId = null
        currentTitle = null
        currentArtist = null
        currentCoverUrl = null
        progress = 0f
        duration = 0L
        currentPosition = 0L
    }

    fun release() {
        stopProgressTicker()
        scope.cancel()
        controller?.removeListener(playerListener)
        controller?.release()
        controller = null
    }
}