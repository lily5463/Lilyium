package com.lily.lilyiumplayer.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.lily.lilyiumplayer.api.model.Song
import com.lily.lilyiumplayer.data.MusicRepository
import com.lily.lilyiumplayer.data.paging.SongsPagingSource

import kotlinx.coroutines.launch

private const val PAGE_SIZE = 40

class SongsViewModel : ViewModel() {

    private val repo = MusicRepository()

    // cachedIn(viewModelScope) keeps the paged data alive across recompositions
    // and reuses already-fetched pages when you scroll back up
    val songs = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            prefetchDistance = 10,   // start fetching next page when 10 items from end
            enablePlaceholders = false
        ),
        initialKey = 0,
        pagingSourceFactory = { SongsPagingSource(repo) }
    ).flow.cachedIn(viewModelScope)

    fun toggleFavourite(song: Song) {
        viewModelScope.launch {
            try {
                if (song.starred != null) repo.unstar(song.id) else repo.star(song.id)
                // NOTE: with Paging 3 you can't mutate the list directly.
                // The cleanest approach is to invalidate and re-fetch, or keep
                // a separate Set<String> of locally-toggled IDs as override state.
            } catch (_: Exception) { }
        }
    }

    fun getCoverArtUrl(id: String) = repo.getCoverArtUrl(id)
    fun getStreamUrl(id: String) = repo.getStreamUrl(id)
}
