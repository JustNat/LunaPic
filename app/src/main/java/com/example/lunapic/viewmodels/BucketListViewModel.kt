package com.example.lunapic.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aws.sdk.kotlin.services.s3.model.Bucket
import aws.sdk.kotlin.services.s3.model.BucketAlreadyExists
import com.example.lunapic.aws.AWSUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BucketListViewModel : ViewModel() {
    private val _selectedItem = MutableStateFlow(-1)
    val selectedItem = _selectedItem.asStateFlow()
    private val _buckts = MutableStateFlow(listOf<Bucket>())
    val buckts = _buckts.asStateFlow()
    private val _isDeleteBucketDialogOpen = MutableStateFlow(false)
    val isDeleteBucketDialogOpen = _isDeleteBucketDialogOpen.asStateFlow()
    private val _bucketName = MutableStateFlow("")
    val bucketName = _bucketName.asStateFlow()
    private val _supportText = MutableStateFlow("")
    val supportText = _supportText.asStateFlow()
    private val _isError = MutableStateFlow(false)
    val isError = _isError.asStateFlow()

    init {
        viewModelScope.launch {
            _buckts.value = AWSUtils.listBuckts()
        }
        _supportText.value = "Deve conter de 3 a 63 caracteres, começar e terminar com letra ou número e sem letras maiúsculas."
    }

    fun changeSelectedItem(index: Int) {
        _selectedItem.value = index
    }

    fun deleteBucket(bucket: Bucket, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        changeSelectedItem(-1)
        viewModelScope.launch {
            try {
                AWSUtils.deleteBuckt(bucket.name ?: "")
                val tempList = _buckts.value.toMutableList()
                tempList.remove(bucket)
                _buckts.value = tempList.toList()
                onSuccess()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }

        }
    }

    fun createBucket(bucketName: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                if (bucketNameValidation(bucketName) == 0) {
                    AWSUtils.createBuckt(bucketName)
                    val tempList = _buckts.value.toMutableList()
                    tempList.add(Bucket { name = bucketName })
                    _buckts.value = tempList.toList()
                    onSuccess()
                } else {
                    _isError.value = true
                    _supportText.value = "Bucket Name inválido"
                }
            } catch (e: BucketAlreadyExists) {
                onError(Exception("Este bucket já existe"))
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun changeDeleteBucketDialogState(state: Boolean) {
        _isDeleteBucketDialogOpen.value = state
    }

    fun changeBucketName(name: String) {
        _bucketName.value = name
        bucketNameValidation(name)
    }

    private fun bucketNameValidation(name: String) : Int {
        if (name.isBlank()) {
            _supportText.value = "Campo obrigatório."
            _isError.value = true
            return 1
        } else if (name.length < 3 || name.length > 63) {
            _supportText.value = "Nome deve conter de 3 a 63 caracteres."
            _isError.value = true
            return 1
        } else if (!name[0].isLetterOrDigit() || !name.last().isLetterOrDigit()) {
            _supportText.value = "Nome do bucket deve começar e terminar com letra ou número."
            _isError.value = true
            return 1
        } else if (name != name.lowercase()) {
            _supportText.value = "Nome não pode conter letras maiúsculas."
            _isError.value = true
            return 1
        } else if (name.contains(' ')) {
            _supportText.value = "Nome do bucket não pode conter espaços."
            _isError.value = true
            return 1
        } else {
            _supportText.value = "Deve conter de 3 a 63 caracteres, começar e terminar com letra ou número e sem letras maiúsculas."
            _isError.value = false
            return 0
        }
    }

    fun onCreateBucketDialogClosed() {
        _bucketName.value = ""
        _isError.value = false
        _supportText.value = "Deve conter de 3 a 63 caracteres, começar e terminar com letra ou número e sem letras maiúsculas."
    }

}

