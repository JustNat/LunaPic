package com.example.lunapic.ui.state.bucket

sealed interface ValidationBucketNameResponse {
    data class IsBlank(val message: String) : ValidationBucketNameResponse
    data class IsTooShortOrLong(val message: String) : ValidationBucketNameResponse
    data class IsEndingWithLetterOrDigit(val message: String) : ValidationBucketNameResponse
    data class HasCapitalLetters(val message: String) : ValidationBucketNameResponse
    data class Ok(val message: String) : ValidationBucketNameResponse
}