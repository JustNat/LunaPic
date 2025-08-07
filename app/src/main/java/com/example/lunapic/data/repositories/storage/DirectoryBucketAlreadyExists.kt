package com.example.lunapic.data.repositories.storage

import java.io.IOException

class DirectoryAlreadyExists(
    message: String = "O diretório já existe",
    override val cause: Throwable? = null
) : IOException(message, cause)