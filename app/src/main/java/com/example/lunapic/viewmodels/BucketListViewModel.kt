package com.example.lunapic.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aws.sdk.kotlin.services.s3.model.Bucket as AwsBucket
import com.example.lunapic.repository.db.data.Bucket as MyBucket
import aws.sdk.kotlin.services.s3.model.BucketAlreadyExists
import com.example.lunapic.repository.db.data.Bucket
import com.example.lunapic.storage.InternalStorageRepository
import com.example.lunapic.repository.db.data.BucketDao
import com.example.lunapic.repository.network.CloudStorageServiceRepository
import com.example.lunapic.ui.navigation.AppNavigationActions
import com.example.lunapic.ui.navigation.Navigator
import com.example.lunapic.ui.state.bucket.BucketListEvent
import com.example.lunapic.ui.state.bucket.BucketListState
import com.example.lunapic.ui.state.bucket.CreateBucketForm
import com.example.lunapic.ui.state.bucket.ValidationBucketNameResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class BucketListViewModel @Inject constructor(
    private val s3Manager: CloudStorageServiceRepository,
    private val bucketDao: BucketDao,
    private val internalStorage: InternalStorageRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _state = MutableStateFlow(BucketListState())
    val state = _state
        .onStart { loadData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), BucketListState())

    private fun loadData() {
        // TODO("QUANDO SEM INTERNET, PUXAR DO INTERNAL STORAGE OS BUCKETS")
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(
                        buckets = s3Manager.listBuckets(),
                        supportText = CreateBucketForm.DEFAULT_MESSAGE
                    )
                }

                val bucketsToSetPrivacy = mutableListOf<MyBucket>()
                _state.value.buckets.forEach { bucket ->
                    if (bucketDao.isBucketRegistered(bucket.name ?: "") == 0) {
                        bucketDao.insertBucket(
                            Bucket(
                                name = bucket.name ?: "",
                                isPrivate = false,
                                lastUpdatedAt = OffsetDateTime.now(ZoneId.of("America/Sao_Paulo"))
                            )
                        )
                        bucketsToSetPrivacy.add(
                            MyBucket(
                                bucket.name ?: "",
                                false,
                                OffsetDateTime.now(ZoneId.of("America/Sao_Paulo"))
                            )
                        )
                        _state.update {
                            it.copy(
                                isSetBucketsPrivacyDialog = true,
                                bucketsPrivacy = bucketsToSetPrivacy.toList()
                            )
                        }
                    }
                }

                _state.value.buckets.forEach { bucket ->
                    internalStorage.saveBucket(bucket.name.toString())
                }
            } catch (e: Exception) {
                _state.value.snackBarHost.showSnackbar("Houve um erro ao carregar os buckets. ${e.localizedMessage}")
            }
        }
    }

    fun onEvent(event: BucketListEvent) {
        when (event) {
            is BucketListEvent.CreateBucket -> {
                viewModelScope.launch {
                    try {
                        val validation =
                            _state.value.createBucketForm.validateBucketName(_state.value.createBucketForm.bucketName)
                        if (validationName(validation) == 0) {
                            s3Manager.createBucket(_state.value.createBucketForm.bucketName)
                            internalStorage.saveBucket(_state.value.createBucketForm.bucketName)
                            bucketDao.insertBucket(
                                MyBucket(
                                    name = _state.value.createBucketForm.bucketName,
                                    isPrivate = _state.value.createBucketForm.isPrivate,
                                    lastUpdatedAt = OffsetDateTime.now(ZoneId.of("America/Sao_Paulo"))
                                )
                            )

                            val auxList = _state.value.buckets.toMutableList()
                            auxList.add(AwsBucket {
                                name = _state.value.createBucketForm.bucketName
                            })

                            _state.update {
                                it.copy(
                                    buckets = auxList.toList(),
                                    createBucketForm = CreateBucketForm(),
                                )
                            }

                            _state.value.snackBarHost.showSnackbar("Bucket criado.")
                        }
                    } catch (_: BucketAlreadyExists) {
                        withContext(Dispatchers.Main) {
                            _state.update {
                                it.copy(
                                    createBucketForm = it.createBucketForm.copy(
                                        isCreateBucketDialog = false
                                    )
                                )
                            }
                            _state.value.snackBarHost.showSnackbar("Este bucket já existe.")
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            _state.update {
                                it.copy(
                                    createBucketForm = it.createBucketForm.copy(
                                        isCreateBucketDialog = false
                                    )
                                )
                            }
                            _state.value.snackBarHost.showSnackbar(
                                e.localizedMessage ?: "Houve um erro."
                            )
                        }
                    }
                }
            }

            is BucketListEvent.DeleteBucket -> {
                viewModelScope.launch {
                    try {
                        event.bucket.name?.let { name ->
                            s3Manager.deleteBucket(name)
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

                        _state.value.snackBarHost.showSnackbar("Bucket deletado.")
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            _state.update { it.copy(isDeleteBucketDialogOpen = false) }
                            _state.value.snackBarHost.showSnackbar("Houve um erro ao deletar o bucket: ${e.localizedMessage}")
                        }
                    } catch (e: IOException) {
                        withContext(Dispatchers.IO) {
                            _state.update { it.copy(isDeleteBucketDialogOpen = false) }
                            _state.value.snackBarHost.showSnackbar("Houve um erro ao deletar o bucket internamente: ${e.localizedMessage}")
                        }
                    }
                }
            }

            is BucketListEvent.SetBucketName -> {
                _state.update {
                    it.copy(createBucketForm = it.createBucketForm.copy(bucketName = event.bucketName))
                }
                val validation =
                    (_state.value.createBucketForm.validateBucketName(event.bucketName))
                validationName(validation)
            }

            is BucketListEvent.SetIsPrivate -> {
                _state.update {
                    it.copy(createBucketForm = it.createBucketForm.copy(isPrivate = event.isPrivate))
                }
            }

            is BucketListEvent.SetCreateDialogState -> {
                _state.update {
                    it.copy(createBucketForm = it.createBucketForm.copy(isCreateBucketDialog = event.state))
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

            is BucketListEvent.NavigateToMediaScreen -> {
                navigator.navigate(
                    AppNavigationActions.BucketsScreen.bucketScreenToMediasScreen(
                        event.bucketName
                    )
                )
            }

            BucketListEvent.RegisterBucketsPrivacy -> {
                viewModelScope.launch {
                    try {
                        _state.value.bucketsPrivacy.forEach {
                            bucketDao.updateBucket(it)
                        }
                    } catch (e: Exception) {
                        _state.value.snackBarHost.showSnackbar("Houve um erro ao registrar os buckets: ${e.localizedMessage}")
                    }
                }
                _state.update { it.copy(isSetBucketsPrivacyDialog = false) }
            }

            is BucketListEvent.SetBucketPrivacy -> {
                val buckets = _state.value.bucketsPrivacy.toMutableList()
                val newIsPrivate =
                    buckets[event.index].copy(isPrivate = !_state.value.bucketsPrivacy[event.index].isPrivate)
                buckets[event.index] = newIsPrivate
                _state.update { state -> state.copy(bucketsPrivacy = buckets.toList()) }
            }

            is BucketListEvent.SetBucketsPrivacyDialogState -> {
                _state.update { it.copy(isSetBucketsPrivacyDialog = event.state) }
            }
        }
    }

    private fun validationName(validation: ValidationBucketNameResponse): Int {
        when (validation) {
            is ValidationBucketNameResponse.HasCapitalLetters -> {
                _state.update {
                    it.copy(
                        isError = true,
                        supportText = validation.message
                    )
                }
                return 1
            }

            is ValidationBucketNameResponse.IsBlank -> {
                _state.update {
                    it.copy(
                        isError = true,
                        supportText = validation.message
                    )
                }
                return 1
            }

            is ValidationBucketNameResponse.IsEndingWithLetterOrDigit -> {
                _state.update {
                    it.copy(
                        isError = true,
                        supportText = validation.message
                    )
                }
                return 1
            }

            is ValidationBucketNameResponse.IsTooShortOrLong -> {
                _state.update {
                    it.copy(
                        isError = true,
                        supportText = validation.message
                    )
                }
                return 1
            }

            is ValidationBucketNameResponse.Ok -> {
                _state.update {
                    it.copy(
                        isError = false,
                        supportText = validation.message
                    )
                }
                return 0
            }
        }
    }
}

