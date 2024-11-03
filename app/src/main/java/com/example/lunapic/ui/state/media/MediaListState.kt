package com.example.lunapic.ui.state.media

import androidx.compose.material3.SnackbarHostState

data class MediaListState(
    val medias : List<MediaState> = emptyList(),
    val snackBarHostState: SnackbarHostState = SnackbarHostState(),
    val isLazyGridScrollable: Boolean = false
)
