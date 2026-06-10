package com.lily.lilyiumplayer.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lily.lilyiumplayer.api.model.Song
import com.lily.lilyiumplayer.data.MusicRepository


private const val PAGE_SIZE = 40   // songs per network call

class SongsPagingSource(
    private val repo: MusicRepository
) : PagingSource<Int, Song>() {

    override fun getRefreshKey(state: PagingState<Int, Song>): Int? {
        // When refreshing, restart from the page nearest the current scroll position
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(PAGE_SIZE)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(PAGE_SIZE)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        val offset = params.key ?: 0
        return try {
            val songs = repo.getAllSongs(offset = offset, count = PAGE_SIZE)
            LoadResult.Page(
                data = songs,
                prevKey = if (offset == 0) null else offset - PAGE_SIZE,
                nextKey = if (songs.size < PAGE_SIZE) null else offset + PAGE_SIZE
                //         ↑ if we got fewer than a full page, we've hit the end
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}