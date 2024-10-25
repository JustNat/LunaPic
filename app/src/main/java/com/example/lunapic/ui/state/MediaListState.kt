package com.example.lunapic.ui.state

import androidx.compose.material3.SnackbarHostState
import java.io.File

data class MediaListState(
    val medias : List<File> = emptyList(),
    val snackBarHostState: SnackbarHostState = SnackbarHostState()
)
