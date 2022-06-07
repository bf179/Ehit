package com.mitsuki.ehit.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsuki.ehit.model.entity.db.QuickSearch
import com.mitsuki.ehit.model.entity.GalleryDataMeta
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.model.dao.SearchDao
import com.mitsuki.ehit.model.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class QuickSearchViewModel @Inject constructor(
    @RemoteRepository var repository: Repository,
    val searchDao: SearchDao
) :
    ViewModel() {

    suspend fun quickSearch(): List<QuickSearch> =
        withContext(Dispatchers.IO) { searchDao.queryQuick() }

    suspend fun isQuickSave(key: String, meta: GalleryDataMeta.Type): Boolean =
        withContext(Dispatchers.IO) {
            val list = searchDao.queryQuick(key, meta)
            list.isNotEmpty()
        }

    suspend fun saveSearch(name: String, key: String, meta: GalleryDataMeta.Type) =
        withContext(Dispatchers.IO) {
            if (name.isNotEmpty() && key.isNotEmpty())
                searchDao.saveQuick(name, key, meta)
        }

    suspend fun delSearch(key: String, meta: GalleryDataMeta.Type) = withContext(Dispatchers.IO) {
        viewModelScope.launch {
            searchDao.deleteQuick(key, meta)
        }
    }

    suspend fun swapQuickItem(data: List<QuickSearch>) = withContext(Dispatchers.IO) {
        searchDao.insertQuick(*data.toTypedArray())
    }
}