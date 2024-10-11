package com.example.lunapic.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aws.sdk.kotlin.services.s3.model.Bucket as AwsBucket
import com.example.lunapic.repository.db.data.Bucket as MyBucket
import aws.sdk.kotlin.services.s3.model.BucketAlreadyExists
import com.example.lunapic.network.aws.AWSRepository
import com.example.lunapic.repository.InternalStorageRepository
import com.example.lunapic.repository.db.data.BucketDao
import com.example.lunapic.ui.state.BucketForm
import com.example.lunapic.ui.state.BucketListEvent
import com.example.lunapic.ui.state.BucketListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class BucketListViewModel @Inject constructor(
    private val awsRepository: AWSRepository,
    private val bucketDao: BucketDao,
    private val internalStorage: InternalStorageRepository
) : ViewModel(), AWSRepository by awsRepository {

    private val _state = MutableStateFlow(BucketListState())
    val state = _state.asStateFlow()
    val snackBarHostState = SnackbarHostState()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    buckets = awsRepository.listBuckets(),
                    supportText = "Deve conter de 3 a 63 caracteres, começar e terminar com letra ou número e sem letras maiúsculas."
                )
            }
        }
    }

    fun onEvent(event: BucketListEvent) {
        when (event) {
            is BucketListEvent.CreateBucket -> {
                viewModelScope.launch {
                    try {
                        if (nameValidation(_state.value.bucketForm.bucketName) == 0) {

                            awsRepository.createBucket(_state.value.bucketForm.bucketName)
                            internalStorage.saveBucket(_state.value.bucketForm.bucketName)
                            bucketDao.insertBucket(
                                MyBucket(
                                    name = _state.value.bucketForm.bucketName,
                                    isPrivate = _state.value.bucketForm.isPrivate,
                                    lastUpdatedAt = OffsetDateTime.now()
                                )
                            )

                            val auxList = _state.value.buckets.toMutableList()
                            auxList.add(AwsBucket { name = _state.value.bucketForm.bucketName })

                            _state.update {
                                it.copy(
                                    buckets = auxList.toList(),
                                    bucketForm = BucketForm(),
                                    isCreateBucketDialogOpen = false
                                )
                            }

                            snackBarHostState.showSnackbar("Bucket criado.")
                        } else {
                            _state.update {
                                it.copy(
                                    isError = true,
                                    supportText = "Nome para o bucket inválido."
                                )
                            }
                        }
                    } catch (_: BucketAlreadyExists) {
                        withContext(Dispatchers.Main) {
                            _state.update { it.copy(isCreateBucketDialogOpen = false) }
                            snackBarHostState.showSnackbar("Este bucket já existe.")
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            _state.update { it.copy(isCreateBucketDialogOpen = false) }
                            snackBarHostState.showSnackbar(e.localizedMessage ?: "Houve um erro.")
                        }
                    }
                }
            }

            is BucketListEvent.DeleteBucket -> {
                viewModelScope.launch {
                    try {
                        event.bucket.name?.let { name ->
                            awsRepository.deleteBucket(name)
                            internalStorage.deleteBucket(name)
                            bucketDao.deleteBucket(name)
                        }

                        val auxList = _state.value.buckets.toMutableList()
                        auxList.remove(event.bucket)

                        _state.update {
                            it.copy(
                                buckets = auxList.toList(),
                                selectedBucket = -1,
                                isDeleteBucketDialogOpen = false
                            )
                        }

                        snackBarHostState.showSnackbar("Bucket deletado.")
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            _state.update { it.copy(isDeleteBucketDialogOpen = false) }
                            snackBarHostState.showSnackbar("Houve um erro ao deletar o bucket: ${e.localizedMessage}")
                        }
                    } catch (e : IOException) {
                        withContext(Dispatchers.IO) {
                            _state.update { it.copy(isDeleteBucketDialogOpen = false) }
                            snackBarHostState.showSnackbar("Houve um erro ao deletar o bucket internamente: ${e.localizedMessage}")
                        }
                    }
                }
            }

            is BucketListEvent.SetBucketName -> {
                nameValidation(event.bucketName)
                _state.update {
                    it.copy(bucketForm = it.bucketForm.copy(bucketName = event.bucketName))
                }
            }

            is BucketListEvent.SetIsPrivate -> {
                _state.update {
                    it.copy(bucketForm = it.bucketForm.copy(isPrivate = event.isPrivate))
                }
            }

            is BucketListEvent.SetCreateDialogState -> {
                _state.update {
                    it.copy(isCreateBucketDialogOpen = event.state)
                }
            }

            is BucketListEvent.SetDeleteDialogState -> {
                _state.update {
                    it.copy(isDeleteBucketDialogOpen = event.state)
                }
            }

            is BucketListEvent.SetSelectedBucket -> {
                _state.update {
                    it.copy(selectedBucket = event.selectedBucket)
                }
            }
        }
    }

    private fun nameValidation(name: String): Int {
        if (name.isBlank()) {
            _state.update {
                it.copy(supportText = "Campo obrigatório", isError = true)
            }
            return 1
        } else if (name.length < 3 || name.length > 63) {
            _state.update {
                it.copy(supportText = "Nome deve conter de 3 a 63 caracteres.", isError = true)
            }
            return 1
        } else if (!name[0].isLetterOrDigit() || !name.last().isLetterOrDigit()) {
            _state.update {
                it.copy(
                    supportText = "Nome do bucket deve começar e terminar com letra ou número.",
                    isError = true
                )
            }
            return 1
        } else if (name != name.lowercase()) {
            _state.update {
                it.copy(supportText = "Nome não pode conter letras maiúsculas.", isError = true)
            }
            return 1
        } else if (name.contains(' ')) {
            _state.update {
                it.copy(supportText = "Nome do bucket não pode conter espaços.", isError = true)
            }
            return 1
        } else {
            _state.update {
                it.copy(
                    supportText = "Deve conter de 3 a 63 caracteres, começar e terminar com letra ou número e sem letras maiúsculas.",
                    isError = false
                )
            }
            return 0
        }
    }
}

