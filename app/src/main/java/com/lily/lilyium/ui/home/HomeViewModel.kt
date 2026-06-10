package com.lily.lilyium.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lily.lilyium.api.model.Album
import com.lily.lilyium.localDB.AppDatabase
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application).DatabaseDao()



    init {
        loadSongs()
    }
    private val _texts = MutableLiveData<List<Album>>()
    private fun loadSongs() {
        viewModelScope.launch {
            val result = database.getAllSongs()
//            TODO: optimize song list viewmodel
            _texts.postValue(result)
//            _texts.postValue(result.map {it.title})
            Log.d("DB_CHECK", "Albums in DB: $result")
        }
    }


    val texts: LiveData<List<Album>> = _texts

}