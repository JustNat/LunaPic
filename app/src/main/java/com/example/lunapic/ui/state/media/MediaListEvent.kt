package com.example.lunapic.ui.state.media

sealed interface MediaListEvent {
    data object GoBack : MediaListEvent
}