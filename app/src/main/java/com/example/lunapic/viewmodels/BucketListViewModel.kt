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

    init {
        viewModelScope.launch {
            _buckts.value = AWSUtils.listBuckts().toMutableList()
        }
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
            } catch (e : Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }

        }
    }

    fun createBucket(bucketName: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                AWSUtils.createBuckt(bucketName)
                val tempList = _buckts.value.toMutableList()
                tempList.add(Bucket{ name = bucketName })
                _buckts.value = tempList.toList()
                onSuccess()
            } catch (e : BucketAlreadyExists) {
                onError(Exception("Este bucket já existe"))
            } catch (e : Exception) {
                onError(e)
            }
        }
    }

    fun changeDeleteBucketDialogState(state : Boolean) {
        _isDeleteBucketDialogOpen.value = state
    }

}

