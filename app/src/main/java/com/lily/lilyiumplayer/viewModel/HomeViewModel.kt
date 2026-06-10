package com.lily.lilyiumplayer.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lily.lilyiumplayer.api.model.Album
import com.lily.lilyiumplayer.api.model.Song
import com.lily.lilyiumplayer.data.MusicRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val albums: List<Album> = emptyList(),
    val recentlyAddedAlbum: List<Album> = emptyList(),
    val recentlyPlayedAlbum: List<Album> = emptyList(),
    val randomAlbum: List<Album> = emptyList(),
    val favorites: List<Song> = emptyList(),
    val randomSongs: List<Song> = emptyList(),
    val rated: List<Song> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class HomeViewModel : ViewModel() {

    private val repo = MusicRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // fetch all in parallel instead of sequentially
                val albumsDeferred = async { repo.getAlbumList("alphabeticalByName", 20) }
                val randomAlbumDeferred = async { repo.getAlbumList("random", 20) }
                val recentlyAddedDeferred = async { repo.getAlbumList("newest", 20) }
                val recentlyPlayedDeferred = async { repo.getAlbumList("recent", 20) }
                val staredDeferred = async { repo.getStared2() }
                val randomSongsDeferred = async { repo.getRandomSongs(50) }
                val stared = staredDeferred.await()

                _uiState.update {
                    it.copy(
                        albums = albumsDeferred.await(),
                        recentlyAddedAlbum = recentlyAddedDeferred.await(),
                        recentlyPlayedAlbum = recentlyPlayedDeferred.await(),
                        randomAlbum = randomAlbumDeferred.await(),
                        favorites = stared,
                        rated = stared,
                        randomSongs = randomSongsDeferred.await(),
                        isLoading = false,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Something went wrong",
                    )
                }
            }
        }
    }

    fun refreshRandomSongs() {
        viewModelScope.launch {
            val newRandomSongs = repo.getRandomSongs(50)
            _uiState.update { it.copy(randomSongs = newRandomSongs) }
        }
    }
    fun getCoverArtUrl(id: String) = repo.getCoverArtUrl(id)
    fun getStreamUrl(id: String) = repo.getStreamUrl(id)
}