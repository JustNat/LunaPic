package com.example.lunapic.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aws.sdk.kotlin.services.s3.model.Bucket as AwsBucket
import com.example.lunapic.repositories.db.data.Bucket as MyBucket
import aws.sdk.kotlin.services.s3.model.BucketAlreadyExists
import com.example.lunapic.repositories.storage.InternalStorageRepository
import com.example.lunapic.repositories.db.data.BucketDao
import com.example.lunapic.repositories.network.CloudStorageRepository
import com.example.lunapic.ui.routing.AppNavigationActions
import com.example.lunapic.ui.routing.Navigator
import com.example.lunapic.ui.home.state.HomeScreenEvents
import com.example.lunapic.ui.home.state.HomeScreenState
import com.example.lunapic.ui.home.state.components.CreateBucketForm
import com.example.lunapic.ui.home.state.components.ValidationBucketNameResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
class HomeViewModel @Inject constructor(
    private val cloudRepository: CloudStorageRepository,
    private val bucketDao: BucketDao,
    private val internalStorage: InternalStorageRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state
        .onStart { loadData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), HomeScreenState())

    private fun loadData() {
        // TODO("QUANDO SEM INTERNET, PUXAR DO INTERNAL STORAGE OS BUCKETS")
        viewModelScope.launch {
            try {
                _state.update { it.copy(buckets = cloudRepository.listBuckets()) }
                val bucketsToSetPrivacy = mutableListOf<MyBucket>()
                _state.value.buckets.forEach { bucket ->
                    if (bucketDao.isBucketRegistered(bucket.name.toString()) == 0) {
                        bucketDao.insertBucket(
                            MyBucket(
                                bucket.name.toString(),
                                false,
                                OffsetDateTime.now(ZoneId.of("America/Sao_Paulo"))
                            )
                        )
                        bucketsToSetPrivacy.add(
                            MyBucket(
                                bucket.name.toString(),
                                false,
                                OffsetDateTime.now(ZoneId.of("America/Sao_Paulo"))
                            )
                        )
                    }

                }

                _state.update { it.copy(bucketsPrivacy = bucketsToSetPrivacy) }

                _state.value.buckets.forEach { bucket ->
                    launch { internalStorage.saveBucket(bucket.name.toString()) }
                }
            } catch (e: Exception) {
                _state.value.snackBarHost.showSnackbar("Houve um erro ao carregar os buckets. ${e.localizedMessage}")
            }
        }
    }

    fun onEvent(event: HomeScreenEvents) {
        when (event) {
            is HomeScreenEvents.CreateBucket -> {
                viewModelScope.launch {
                    try {
                        val validation =
                            _state.value.createBucketForm.validateBucketName(_state.value.createBucketForm.bucketName)
                        if (validationName(validation) == 0) {
                            cloudRepository.createBucket(_state.value.createBucketForm.bucketName)
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

            is HomeScreenEvents.DeleteBucket -> {
                viewModelScope.launch {
                    try {
                        event.bucket.name?.let { name ->
                            cloudRepository.deleteBucket(name)
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

            is HomeScreenEvents.SetBucketName -> {
                _state.update {
                    it.copy(createBucketForm = it.createBucketForm.copy(bucketName = event.bucketName))
                }
                val validation =
                    (_state.value.createBucketForm.validateBucketName(event.bucketName))
                validationName(validation)
            }

            is HomeScreenEvents.SetIsPrivate -> {
                _state.update {
                    it.copy(createBucketForm = it.createBucketForm.copy(isPrivate = event.isPrivate))
                }
            }

            is HomeScreenEvents.SetCreateDialogState -> {
                _state.update {
                    it.copy(createBucketForm = it.createBucketForm.copy(isCreateBucketDialog = event.state))
                }
            }

            is HomeScreenEvents.SetDeleteDialogState -> {
                _state.update {
                    it.copy(isDeleteBucketDialogOpen = event.state)
                }
            }

            is HomeScreenEvents.SetSelectedBucket -> {
                _state.update {
                    it.copy(selectedBucket = event.selectedBucket)
                }
            }

            is HomeScreenEvents.NavigateToMediaScreen -> {
                navigator.navigate(
                    AppNavigationActions.HomeScreen.homeScreenToMediasScreen(
                        event.bucketName
                    )
                )
            }

            HomeScreenEvents.RegisterBucketsPrivacy -> {
                viewModelScope.launch {
                    try {
                        _state.value.bucketsPrivacy.map {
                            async { bucketDao.updateBucket(it) }
                        }.awaitAll()

                        _state.update {
                            it.copy(bucketsPrivacy = emptyList())
                        }
                    } catch (e: Exception) {
                        _state.value.snackBarHost.showSnackbar("Houve um erro ao registrar os buckets: ${e.localizedMessage}")
                    }
                }
            }

            is HomeScreenEvents.SetBucketPrivacy -> {
                val buckets = _state.value.bucketsPrivacy
                val newIsPrivate = buckets[event.index].copy(isPrivate = !buckets[event.index].isPrivate)
                _state.update { state ->
                    state.copy(
                        bucketsPrivacy = buckets.map {
                            if (it.name == newIsPrivate.name) {
                                newIsPrivate
                            } else it
                        }
                    )
                }
            }

        }
    }

    private fun validationName(validation: ValidationBucketNameResponse): Int {
        when (validation) {
            is ValidationBucketNameResponse.HasCapitalLetters -> {
                _state.update {
                    it.copy(
                        createBucketForm = it.createBucketForm.copy(
                            isError = true,
                            supportText = validation.message
                        )
                    )
                }
                return 1
            }

            is ValidationBucketNameResponse.IsBlank -> {
                _state.update {
                    it.copy(
                        createBucketForm = it.createBucketForm.copy(
                            isError = true,
                            supportText = validation.message
                        )
                    )
                }
                return 1
            }

            is ValidationBucketNameResponse.IsEndingWithLetterOrDigit -> {
                _state.update {
                    it.copy(
                        createBucketForm = it.createBucketForm.copy(
                            isError = true,
                            supportText = validation.message
                        )
                    )
                }
                return 1
            }

            is ValidationBucketNameResponse.IsTooShortOrLong -> {
                _state.update {
                    it.copy(
                        createBucketForm = it.createBucketForm.copy(
                            isError = true,
                            supportText = validation.message
                        )
                    )
                }
                return 1
            }

            is ValidationBucketNameResponse.Ok -> {
                _state.update {
                    it.copy(
                        createBucketForm = it.createBucketForm.copy(
                            isError = false,
                            supportText = validation.message
                        )
                    )
                }
                return 0
            }
        }
    }
}

