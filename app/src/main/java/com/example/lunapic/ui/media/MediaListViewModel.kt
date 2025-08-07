package com.example.lunapic.ui.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lunapic.db.data.BucketDao
import com.example.lunapic.db.data.Media
import com.example.lunapic.db.data.MediaDao
import com.example.lunapic.data.repositories.network.CloudStorageRepository
import com.example.lunapic.data.repositories.storage.InternalStorageRepository
import com.example.lunapic.ui.routing.AppNavigationActions
import com.example.lunapic.ui.routing.Navigator
import com.example.lunapic.ui.media.state.MediaListEvents
import com.example.lunapic.ui.media.state.MediaListState
import com.example.lunapic.ui.media.state.MediaState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
    private val cloudRepository: CloudStorageRepository,
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
                val medias = cloudRepository.getObjects(bucketName)
                medias.map { media ->
                    async {
                        internalStorage.saveMedia(
                            fileName = media.name,
                            fileBody = media.body,
                            bucketName = media.bucket,
                            size = media.size
                        )

                    }.also {
                        launch {
                            if (mediaDao.isMediaRegistered(media.name) == 0) {
                                mediaDao.insertMedia(Media(name = media.name, bucket = bucketName))
                            }
                        }
                    }
                }.awaitAll()
            } catch (e: Exception) {
                _state.value.snackBarHostState.showSnackbar("Ocorreu um erro: {${e.localizedMessage}}")
            }
            _state.update {
                it.copy(
                    medias = internalStorage.getMedias(bucketName).map { mediaFile ->
                        MediaState(file = mediaFile)
                    }
                )
            }
            bucketDao.updateLastUpdatedAt(
                bucketName, OffsetDateTime.now(ZoneId.of("America/Sao_Paulo"))
            )
        }
    }

    fun onEvent(event: MediaListEvents) {
        when (event) {
            is MediaListEvents.GoBack -> {
                navigator.navigate(AppNavigationActions.PopBack)
            }

            is MediaListEvents.DeselectAll -> {
                _state.update {
                    it.copy(medias = it.medias.map { mediaState ->
                        mediaState.copy(
                            isSelected = false
                        )
                    })
                }
            }

            MediaListEvents.DeleteMedias -> {
                viewModelScope.launch {
                    try {
                        onEvent(MediaListEvents.SetDeleteDialogState(false))

                        val mediasToDelete = _state.value.medias.filter { it.isSelected }
                        val mediaKeysToDelete = mediasToDelete.map { it.file.name }

                        val response = async {
                            cloudRepository.deleteObjects(
                                bucketName = bucketName,
                                keys = mediaKeysToDelete
                            ).deleted?.map { it.key ?: "" } ?: emptyList()
                        }.await()

                        mediasToDelete.map {
                            async {
                                internalStorage.deleteMedia(it.file)
                                mediaDao.deleteMedia(Media(it.file.name, bucketName))
                            }
                        }.awaitAll()

                        _state.update {
                            it.copy(
                                medias = it.medias.filterNot { mediaState ->
                                    mediasToDelete.contains(
                                        mediaState
                                    )
                                },
                                deleteDialogState = false
                            )
                        }

                        if (mediaKeysToDelete.containsAll(response) && mediasToDelete.size == response.size) {
                            _state.value.snackBarHostState.showSnackbar("Itens deletados com sucesso.")
                        }

                    } catch (e: Exception) {
                        _state.value.snackBarHostState.showSnackbar(e.localizedMessage ?: "")
                    }
                }
            }

            is MediaListEvents.SetSelectedMedia -> {
                val auxList = _state.value.medias.map {
                    if (event.mediaState == it) {
                        it.copy(isSelected = !event.mediaState.isSelected)
                    } else it
                }
                _state.update { it.copy(medias = auxList) }
            }

            MediaListEvents.SelectAll -> {
                if (_state.value.medias.all { it.isSelected }) {
                    _state.update {
                        it.copy(medias = it.medias.map { mediaState -> mediaState.copy(isSelected = false) })
                    }
                } else {
                    _state.update {
                        it.copy(medias = it.medias.map { mediaState -> mediaState.copy(isSelected = true) })
                    }
                }
            }

            is MediaListEvents.SetScrollableState -> {
                _state.update { it.copy(isLazyGridScrollable = event.isScrollable) }
            }

            is MediaListEvents.SetDeleteDialogState -> {
                _state.update { it.copy(deleteDialogState = event.state) }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(bucketName: String): MediaListViewModel
    }

}

