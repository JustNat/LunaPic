package com.example.lunapic.viewmodels

import androidx.lifecycle.ViewModel
import com.example.lunapic.repository.InternalStorageRepository
import com.example.lunapic.ui.state.MediaListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaListViewModel(private val internalStorage: InternalStorageRepository) : ViewModel(),
    InternalStorageRepository by internalStorage {

    private val _state = MutableStateFlow(MediaListState())
    val state = _state.asStateFlow()
}