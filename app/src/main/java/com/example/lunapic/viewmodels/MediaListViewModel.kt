package com.example.lunapic.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lunapic.repository.db.data.BucketDao
import com.example.lunapic.repository.db.data.Media
import com.example.lunapic.repository.db.data.MediaDao
import com.example.lunapic.repository.network.CloudStorageServiceRepository
import com.example.lunapic.storage.InternalStorageRepository
import com.example.lunapic.ui.state.MediaListState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    @Assisted private val bucketName: String
) : ViewModel() {

    private val _state = MutableStateFlow(MediaListState())
    val state = _state.asStateFlow()

    init {
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
                // TODO("ADICIONAR SNACKBAR")
                Log.e("MediaListVM", e.localizedMessage ?: "")
            }
            _state.update {
                it.copy(medias = internalStorage.getMedias(bucketName))
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(bucketName: String): MediaListViewModel
    }
}