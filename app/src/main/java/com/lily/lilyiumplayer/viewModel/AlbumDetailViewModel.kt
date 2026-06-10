package com.lily.lilyiumplayer.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lily.lilyiumplayer.api.model.AlbumDetail
import com.lily.lilyiumplayer.api.model.Song
import com.lily.lilyiumplayer.data.MusicRepository
import kotlinx.coroutines.launch

class AlbumDetailViewModel : ViewModel() {
    private val repo = MusicRepository()

    var album by mutableStateOf<AlbumDetail?>(null)
        private set

    fun loadAlbum(albumId: String) {
        viewModelScope.launch {
            album = repo.getAlbum(albumId)
        }
    }

    fun toggleFavourite(song: Song) {
        viewModelScope.launch {
            try {
                if (song.starred != null) repo.unstar(song.id) else repo.star(song.id)
            } catch (_: Exception) { }
        }
    }

    fun getCoverArtUrl(id: String) = repo.getCoverArtUrl(id)
    fun getStreamUrl(id: String) = repo.getStreamUrl(id)

}