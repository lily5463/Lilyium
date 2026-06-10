package com.lily.lilyiumplayer.player

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.lily.lilyiumplayer.MainActivity

class PlaybackService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            Log.d("PlaybackService", "onMediaItemTransition " +
                    "title=${mediaItem?.mediaMetadata?.title} " +
                    "artist=${mediaItem?.mediaMetadata?.artist} " +
                    "art=${mediaItem?.mediaMetadata?.artworkUri}"
            )
        }

        override fun onEvents(player: Player, events: Player.Events) {
            Log.d("PlaybackService", "onEvents " +
                    "current=${player.currentMediaItem?.mediaMetadata?.title} " +
                    "index=${player.currentMediaItemIndex} " +
                    "count=${player.mediaItemCount} " +
                    "isPlaying=${player.isPlaying}"
            )
        }
    }

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this).build().also {
            it.addListener(playerListener)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(pendingIntent)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (!player.playWhenReady) stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        player.removeListener(playerListener)
        player.release()
        mediaSession.release()
        super.onDestroy()
    }
}