package com.lily.lilyiumplayer.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lily.lilyiumplayer.api.model.SearchResult2
import com.lily.lilyiumplayer.data.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val repo = MusicRepository()

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        if (query.isBlank()) {
            _uiState.update { it.copy(result = SearchResult2()) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repo.search2(query)
            _uiState.update { it.copy(result = result, isLoading = false) }
        }
    }

    fun getCoverArtUrl(id: String) = repo.getCoverArtUrl(id)
    fun getStreamUrl(id: String) = repo.getStreamUrl(id)
}

data class SearchUiState(
    val query: String = "",
    val result: SearchResult2 = SearchResult2(),
    val isLoading: Boolean = false
)