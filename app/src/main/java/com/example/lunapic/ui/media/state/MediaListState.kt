package com.example.lunapic.ui.media.state

import androidx.compose.material3.SnackbarHostState
import java.io.File

data class MediaListState(
    val medias : List<MediaState> = emptyList(),
    val snackBarHostState: SnackbarHostState = SnackbarHostState(),
    val isLazyGridScrollable: Boolean = false,
    val deleteDialogState: Boolean = false
)

data class MediaState(
    val file: File,
    val isSelected: Boolean = false
)