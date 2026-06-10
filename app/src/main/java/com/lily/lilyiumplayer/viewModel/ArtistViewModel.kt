package com.lilyiumplayer.ui.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lily.lilyiumplayer.api.model.Artist
import com.lily.lilyiumplayer.api.model.Index
import com.lily.lilyiumplayer.data.MusicRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ArtistUiState(
    val isLoading: Boolean = true,
    val indexes: List<Index> = emptyList(),   // grouped — used for sticky headers
    val error: String? = null,
    val starringId: String? = null                  // ID currently being starred/unstarred
)

class ArtistViewModel : ViewModel() {

    private val repo = MusicRepository()

    private val _uiState = MutableStateFlow(ArtistUiState())
    val uiState: StateFlow<ArtistUiState> = _uiState.asStateFlow()

    init { loadArtists() }

    private fun loadArtists() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val indexes = repo.getArtistIndexes()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    indexes = indexes
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load artists"
                )
            }
        }
    }

    fun refresh() = loadArtists()

    fun toggleStar(artist: Artist) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(starringId = artist.id)
            try {
                if (artist.starred != null) {
                    repo.unstar(artist.id)
                } else {
                    repo.star(artist.id)
                }
                // Optimistically update the in-memory list
                val updatedIndexes = _uiState.value.indexes.map { index ->
                    index.copy(
                        artist = index.artist.map { a ->
                            if (a.id == artist.id) {
                                a.copy(starred = if (artist.starred != null) null else "starred")
                            } else a
                        }
                    )
                }
                _uiState.value = _uiState.value.copy(
                    indexes = updatedIndexes,
                    starringId = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(starringId = null)
            }
        }
    }

    fun getCoverArtUrl(id: String) = repo.getCoverArtUrl(id)
}