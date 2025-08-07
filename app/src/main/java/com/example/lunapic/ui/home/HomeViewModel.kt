package com.example.lunapic.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lunapic.domain.entities.Bucket
import com.example.lunapic.domain.entities.CreateBucketDTO
import com.example.lunapic.domain.usecases.BucketUseCase
import com.example.lunapic.ui.routing.AppNavigationActions
import com.example.lunapic.ui.routing.Navigator
import com.example.lunapic.ui.home.state.HomeScreenEvents
import com.example.lunapic.ui.home.state.HomeScreenState
import com.example.lunapic.ui.home.state.components.CreateBucketForm
import com.example.lunapic.ui.home.state.components.ValidationBucketNameResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val navigator: Navigator,
    private val bucketUseCase: BucketUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state
        .onStart { loadData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), HomeScreenState())

    private fun loadData() {
        viewModelScope.launch {
            val result = bucketUseCase.getBuckets()
            result.onSuccess { bucketsInfo ->
                _state.update {
                    it.copy(
                        buckets = bucketsInfo.buckets,
                        bucketsCreatedOutsideTheApp = bucketsInfo.bucketsToSync
                    )
                }
            }.onFailure { exception ->
                showSnackMessage("Não foi possível carregar os buckets. ${exception.localizedMessage}")
            }
        }
    }

    fun onEvent(event: HomeScreenEvents) {
        when (event) {
            is HomeScreenEvents.CreateBucket -> {
                viewModelScope.launch {
                    val createBucketData = CreateBucketDTO(
                        bucketName = _state.value.createBucketForm.bucketName,
                        isPrivate = _state.value.createBucketForm.isPrivate
                    )
                    val validation =
                        _state.value.createBucketForm.validateBucketName(createBucketData.bucketName)
                    if (validationName(validation) == 0) {
                        val result = bucketUseCase.createBucket(createBucketData)
                        result.onSuccess { _ ->
                            updateUIAfterBucketCreation(createBucketData)
                            showSnackMessage("Bucket criado com sucesso.")
                        }.onFailure { exception ->
                            showSnackMessage("Não foi possível criar o bucket: ${exception.localizedMessage}")
                        }
                    }
                }
            }

            is HomeScreenEvents.DeleteBucket -> {
                viewModelScope.launch {
                    event.bucketName.let { name ->
                        val result = bucketUseCase.deleteBucket(name)
                        result.onSuccess { _ ->
                            showSnackMessage("Bucket deletado com sucesso.")
                            updateUIAfterBucketDelete(name)
                        }.onFailure { e ->
                            showSnackMessage("Não foi possível deletar o bucket: ${e.localizedMessage}")
                        }
                    }
                }
            }

            is HomeScreenEvents.SetBucketName -> {
                _state.update { state ->
                    state.copy(createBucketForm = state.createBucketForm.copy(bucketName = event.bucketName))
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

            HomeScreenEvents.RegisterNewBuckets -> {
                viewModelScope.launch {
                    val bucketsCreatedOutOfApp =
                        _state.value.bucketsCreatedOutsideTheApp ?: emptyList()
                    val result =
                        bucketUseCase.registerBucketsCreatedOutsideTheApp(bucketsCreatedOutOfApp)
                    result.onSuccess { _ ->
                        resetBucketsCreatedOutOfApp()
                        showSnackMessage("Buckets registrados com sucesso.")
                    }.onFailure { e ->
                        showSnackMessage("Não foi possível registrar os novos buckets: ${e.localizedMessage}")
                    }
                }
            }

            is HomeScreenEvents.SetBucketPrivacy -> {
                val bucketsCreatedOutOfApp = _state.value.bucketsCreatedOutsideTheApp
                val updatedBucket =
                    bucketsCreatedOutOfApp?.get(event.index)
                        ?.copy(isPrivate = !bucketsCreatedOutOfApp[event.index].isPrivate)
                _state.update { state ->
                    state.copy(
                        bucketsCreatedOutsideTheApp = bucketsCreatedOutOfApp?.map { bucket ->
                            if (bucket.name == updatedBucket?.name) {
                                updatedBucket
                            } else bucket
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

    private suspend fun showSnackMessage(message: String) {
        _state.value.snackBarHost.showSnackbar(message)
    }

    private fun updateUIAfterBucketCreation(data: CreateBucketDTO) {
        updateUIAfterCreation(data)
        resetCreateBucketForm()
    }

    private fun updateUIAfterCreation(data: CreateBucketDTO) {
        val newBucket = Bucket(data.bucketName, data.isPrivate)
        val auxBucketsList = _state.value.buckets.toMutableList()
        auxBucketsList.add(newBucket)
        _state.update { it.copy(buckets = auxBucketsList) }
    }

    private fun resetCreateBucketForm() {
        _state.update { it.copy(createBucketForm = CreateBucketForm()) }
    }

    private fun updateUIAfterBucketDelete(bucketName: String) {
        updateUIAfterDelete(bucketName)
        resetDeleteBucketInfo()
    }

    private fun updateUIAfterDelete(bucketName: String) {
        val auxBucketsList = _state.value.buckets.filter { bucket -> bucket.name != bucketName }
        _state.update { it.copy(buckets = auxBucketsList) }
    }

    private fun resetDeleteBucketInfo() {
        _state.update { it.copy(selectedBucket = -1, isDeleteBucketDialogOpen = false) }
    }

    private fun resetBucketsCreatedOutOfApp() {
        _state.update { it.copy(bucketsCreatedOutsideTheApp = null) }
    }
}

