package com.lilyiumplayer.ui.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.lily.lilyiumplayer.data.MusicRepository
import com.lilyiumplayer.data.paging.AlbumsPagingSource

class AlbumsViewModel : ViewModel() {

    private val repo = MusicRepository()

    val albums = Pager(
        config = PagingConfig(
            pageSize = AlbumsPagingSource.PAGE_SIZE,
            enablePlaceholders = false,
            prefetchDistance = AlbumsPagingSource.PAGE_SIZE / 2
        ),
        pagingSourceFactory = {
            AlbumsPagingSource(repo = repo, type = "alphabeticalByName")
        }
    ).flow.cachedIn(viewModelScope)

    fun getCoverArtUrl(id: String) = repo.getCoverArtUrl(id)
}