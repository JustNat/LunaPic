package com.example.lunapic.ui.state.bucket

data class CreateBucketForm(
    val isCreateBucketDialog: Boolean = false,
    val bucketName: String = "",
    val isPrivate: Boolean = false,
    val isError: Boolean = false,
    val supportText: String = DEFAULT_MESSAGE
) {
    companion object {
        const val IS_BLANK_MESSAGE = "Nome do bucket está vazio."
        const val IS_TOO_SHORT_OR_LONG_MESSAGE = "Nome deve conter de 3 a 63 caracteres."
        const val IS_ENDING_WITH_LETTER_OR_DIGIT_MESSAGE =
            "Nome deve iniciar e terminar com uma letra ou número."
        const val HAS_CAPITAL_LETTERS_MESSAGE = "Nome não pode conter letras maiúsculas."
        const val DEFAULT_MESSAGE =
            "Deve conter de 3 a 63 caracteres, começar e terminar com letra ou número e sem letras maiúsculas."
    }

    fun validateBucketName(bucketName: String): ValidationBucketNameResponse {
        return if (bucketName.isBlank()) {
            ValidationBucketNameResponse.IsBlank(IS_BLANK_MESSAGE)
        } else if (bucketName.length < 3 || bucketName.length > 63) {
            ValidationBucketNameResponse.IsTooShortOrLong(IS_TOO_SHORT_OR_LONG_MESSAGE)
        } else if (!bucketName[0].isLetterOrDigit() || !bucketName.last().isLetterOrDigit()) {
            ValidationBucketNameResponse.IsEndingWithLetterOrDigit(
                IS_ENDING_WITH_LETTER_OR_DIGIT_MESSAGE
            )
        } else if (bucketName != bucketName.lowercase()) {
            ValidationBucketNameResponse.HasCapitalLetters(HAS_CAPITAL_LETTERS_MESSAGE)
        } else {
            ValidationBucketNameResponse.Ok(DEFAULT_MESSAGE)
        }
    }
}