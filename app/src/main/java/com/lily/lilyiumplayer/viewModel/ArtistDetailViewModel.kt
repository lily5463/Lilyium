package com.lilyiumplayer.ui.artist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lily.lilyiumplayer.api.model.Album
import com.lily.lilyiumplayer.api.model.Artist
import com.lily.lilyiumplayer.data.MusicRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ArtistDetailUiState(
    val isLoading: Boolean = true,
    val artist: Artist? = null,
//    val artistInfo: ArtistInfo2? = null,    // bio + last.fm image (may be null if API fails)
    val error: String? = null,
    val starringId: String? = null          // album being starred
)

class ArtistDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val artistId: String = checkNotNull(savedStateHandle["artistId"])
    private val repo = MusicRepository()
    private val _uiState = MutableStateFlow(ArtistDetailUiState())
    val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()

    init { loadArtist() }

    private fun loadArtist() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Load both in parallel; artistInfo2 is non-critical so swallow its errors
                val artistDeferred = async { repo.getArtist(artistId) }
//                val infoDeferred = async {
//                    try { repo.getArtistInfo2(artistId) } catch (e: Exception) { null }
//                }
                val artist = artistDeferred.await()
//                val info = infoDeferred.await()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    artist = artist,
//                    artistInfo = info
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load artist"
                )
            }
        }
    }

    fun refresh() = loadArtist()

    fun toggleAlbumStar(album: Album) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(starringId = album.id)
            try {
                if (album.starred != null) repo.unstar(album.id) else repo.star(album.id)
                val updatedAlbums = _uiState.value.artist?.album?.map { a ->
                    if (a.id == album.id) a.copy(starred = if (album.starred != null) null else "starred")
                    else a
                } ?: emptyList()
                _uiState.value = _uiState.value.copy(
                    artist = _uiState.value.artist?.copy(album = updatedAlbums),
                    starringId = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(starringId = null)
            }
        }
    }

    fun getCoverArtUrl(id: String) = repo.getCoverArtUrl(id)
}