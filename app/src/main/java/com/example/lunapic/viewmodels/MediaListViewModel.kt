package com.example.lunapic.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lunapic.repository.db.data.BucketDao
import com.example.lunapic.repository.db.data.Media
import com.example.lunapic.repository.db.data.MediaDao
import com.example.lunapic.repository.network.CloudStorageServiceRepository
import com.example.lunapic.storage.InternalStorageRepository
import com.example.lunapic.ui.navigation.AppNavigationActions
import com.example.lunapic.ui.navigation.Navigator
import com.example.lunapic.ui.state.media.MediaListEvent
import com.example.lunapic.ui.state.media.MediaListState
import com.example.lunapic.ui.state.media.MediaState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneId

@HiltViewModel(assistedFactory = MediaListViewModel.Factory::class)
class MediaListViewModel @AssistedInject constructor(
    private val internalStorage: InternalStorageRepository,
    private val s3Manager: CloudStorageServiceRepository,
    private val mediaDao: MediaDao,
    private val bucketDao: BucketDao,
    private val navigator: Navigator,
    @Assisted val bucketName: String
) : ViewModel() {

    private val _state = MutableStateFlow(MediaListState())
    val state = _state
        .onStart { loadData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), MediaListState())

    private fun loadData() {
        // TODO("Verificar primeiro se as mídias estão presentes no armazenamento do app")
        viewModelScope.launch {
            try {
                bucketDao.updateLastUpdatedAt(
                    bucketName,
                    OffsetDateTime.now(ZoneId.of("America/Sao_Paulo"))
                )
                val medias = s3Manager.getObjects(bucketName)
                medias.forEach { media ->
                    internalStorage.saveMedia(
                        fileName = media.name,
                        fileBody = media.body,
                        bucketName = media.bucket,
                        size = media.size
                    )
                    if (mediaDao.isMediaRegistered(media.name) == 0) {
                        mediaDao.insertMedia(Media(name = media.name, bucket = bucketName))
                    }
                }
            } catch (e: Exception) {
                _state.value.snackBarHostState.showSnackbar("Ocorreu um erro: {${e.localizedMessage}}")
            }
            _state.update {
                it.copy(medias = internalStorage.getMedias(bucketName).map { mediaFile ->
                    MediaState(file = mediaFile)
                })
            }
        }
    }

    fun onEvent(event: MediaListEvent) {
        when (event) {
            is MediaListEvent.GoBack -> {
                navigator.navigate(AppNavigationActions.PopBack)
            }

            is MediaListEvent.DeselectAll -> {
                _state.update {
                    it.copy(medias = it.medias.map { mediaState ->
                        mediaState.copy(
                            isSelected = false
                        )
                    })
                }
            }

            MediaListEvent.DeleteMedias -> {
                // TODO
            }

            is MediaListEvent.SetSelectedMedia -> {
                val auxList = _state.value.medias.map {
                    if (event.mediaState == it) {
                        it.copy(isSelected = !event.mediaState.isSelected)
                    } else it
                }
                _state.update { it.copy(medias = auxList) }
            }

            MediaListEvent.SelectAll -> {
                _state.update {
                    it.copy(medias = it.medias.map { mediaState -> mediaState.copy(isSelected = true) })
                }
            }

            is MediaListEvent.SetScrollableState -> {
                _state.update { it.copy(isLazyGridScrollable = event.isScrollable) }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(bucketName: String): MediaListViewModel
    }

}

