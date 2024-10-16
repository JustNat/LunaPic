package com.example.lunapic.viewmodels

import androidx.lifecycle.ViewModel
import com.example.lunapic.storage.InternalStorageRepository
import com.example.lunapic.ui.state.MediaListState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel(assistedFactory = MediaListViewModel.Factory::class)
class MediaListViewModel @AssistedInject constructor(
    private val internalStorage: InternalStorageRepository,
    @Assisted private val bucketName: String
) : ViewModel() {

    private val _state = MutableStateFlow(MediaListState())
    val state = _state.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(bucketName: String): MediaListViewModel
    }
}