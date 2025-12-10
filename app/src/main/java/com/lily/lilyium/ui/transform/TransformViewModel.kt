package com.lily.lilyium.ui.transform

import android.app.Application
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.lily.lilyium.localDB.AppDatabase
import com.lily.lilyium.localDB.service.DatabaseDao
import com.lily.lilyium.localDB.SongRepo
import com.lily.lilyium.localDB.model.testSong
import kotlinx.coroutines.launch

class TransformViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application).DatabaseDao()



    init {
        loadSongs()
    }
    private val _texts = MutableLiveData<List<String>>()
    private fun loadSongs() {
        viewModelScope.launch {
            val result = database.getAllSongs()
//            TODO: optimize song list viewmodel
            _texts.postValue(result.map {it.title})
        }
    }


    val texts: LiveData<List<String>> = _texts
}