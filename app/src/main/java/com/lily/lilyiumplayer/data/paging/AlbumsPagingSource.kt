package com.lilyiumplayer.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lily.lilyiumplayer.api.model.Album
import com.lily.lilyiumplayer.data.MusicRepository


class AlbumsPagingSource(
    private val repo: MusicRepository,
    private val type: String = "alphabeticalByName"
) : PagingSource<Int, Album>() {

    companion object {
        const val PAGE_SIZE = 40
    }

    override fun getRefreshKey(state: PagingState<Int, Album>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(PAGE_SIZE)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(PAGE_SIZE)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Album> {
        val offset = params.key ?: 0
        return try {
            val albums = repo.getAlbumList(
                type = type,
                size = PAGE_SIZE,
                offset = offset
            )

            LoadResult.Page(
                data = albums,
                prevKey = if (offset == 0) null else offset - PAGE_SIZE,
                nextKey = if (albums.size < PAGE_SIZE) null else offset + PAGE_SIZE
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}