package com.lily.lilyiumplayer.data

import android.util.Log
import com.lily.lilyiumplayer.api.model.Album
import com.lily.lilyium.api.service.ApiProvider
import com.lily.lilyium.api.service.NavidromeApi
import com.lily.lilyiumplayer.api.model.AlbumDetail
import com.lily.lilyiumplayer.api.model.Artist
import com.lily.lilyiumplayer.api.model.Index
import com.lily.lilyiumplayer.api.model.SearchResult2
import com.lily.lilyiumplayer.api.model.Song
import com.lily.lilyiumplayer.util.SessionManager
import kotlin.collections.emptyList

class MusicRepository (
    private val api: NavidromeApi = ApiProvider.getApi()
){
    suspend fun getAlbumList(type: String, size: Int, offset: Int? = 0): List<Album> {
        return try {
            api.getAlbumList(type, size, offset).subsonicResponse.albumList?.album ?: emptyList()
        } catch (e: Exception) {
            Log.e("MusicRepository", "getAlbumList error: ${e.message}", e)
            emptyList()
        }
    }

//    suspend fun getAlbumListNewest(): List<Album> {
//        return try {
//            api.getAlbumListNewest().subsonicResponse.albumList?.album ?: emptyList()
//        } catch (e: Exception) {
//            Log.e("MusicRepository", "getAlbumListNewest error: ${e.message}", e)
//            emptyList()
//        }
//    }


    suspend fun getStared2(): List<Song> {
        return try {
            api.getStarred2().subsonicResponse.starred2?.song ?: emptyList()
        } catch (e: Exception) {
            Log.e("MusicRepository", "getStared2 error: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getAllSongs(offset: Int, count: Int): List<Song>  {
        return try {
            api.search3(query = "", songOffset = offset, songCount = count).subsonicResponse.searchResult3?.song ?: emptyList()
        } catch (e: Exception) {
            Log.e("MusicRepository", "getAllSong (search3) error: ${e.message}", e)
            emptyList()
        }
    }
    suspend fun getRandomSongs(size: Int): List<Song> {
        return try {
            api.getRandomSongs(size).subsonicResponse.randomSongs?.song ?: emptyList()
        } catch (e: Exception) {
            Log.e("MusicRepository", "getRandomSongs error: ${e.message}", e)
            emptyList()
        }
    }

//    suspend fun getAlbum(albumId: String): AlbumDetail? {
//        return try {
//            api.getAlbum(albumId).subsonicResponse.album
//        } catch (e: Exception) {
//            null
//        }
//    }

    suspend fun getAlbum(albumId: String): AlbumDetail? {
        return try {
            val response = api.getAlbum(albumId)
            Log.d("getAlbum", "full response: $response")
            Log.d("getAlbum", "subsonicResponse: ${response.subsonicResponse}")
            Log.d("getAlbum", "album: ${response.subsonicResponse.album}")
            response.subsonicResponse.album
        } catch (e: Exception) {
            Log.e("getAlbum", "error: ${e.message}", e)
            null
        }
    }

    suspend fun search2(query: String): SearchResult2 {
        return try {
            api.search2(query).subsonicResponse.searchResult2 ?: SearchResult2()
        } catch (e: Exception) {
            Log.e("MusicRepository", "search2 error: ${e.message}", e)
            SearchResult2()
        }
    }

    suspend fun getArtistIndexes(): List<Index> {
        return try {
            api.getArtists().subsonicResponse.artists?.index ?: emptyList()
        } catch (e: Exception) {
            Log.e("MusicRepository", "getArtistIndexes error: ${e.message}", e)
            emptyList()
        }
    }

    // flat list for search/filtering if needed
    suspend fun getAllArtists(): List<Artist> {
        return getArtistIndexes().flatMap { it.artist }
    }

    suspend fun getArtist(id: String): Artist? {
        return try {
            api.getArtist(id = id).subsonicResponse.artist
        } catch (e: Exception) {
            Log.e("MusicRepository", "getArtist error: ${e.message}", e)
            null
        }
    }

    suspend fun star(id: String) = api.star(id = id)
    suspend fun unstar(id: String) = api.unstar(id = id)

    val s get() = SessionManager.active ?: throw IllegalStateException("No active server")

    fun getCoverArtUrl(id: String): String {
        return "${s.baseUrl}/rest/getCoverArt.view" +
                "?id=$id" +
                "&u=${s.username}" +
                "&t=${s.token}" +
                "&s=${s.salt}" +
                "&v=1.16.1" +
                "&c=lilyium"
    }

    fun getStreamUrl(id: String): String {
        return "${s.baseUrl}/rest/stream.view" +
                "?id=$id" +
                "&u=${s.username}" +
                "&t=${s.token}" +
                "&s=${s.salt}" +
                "&v=1.16.1" +
                "&c=lilyium"
    }
}