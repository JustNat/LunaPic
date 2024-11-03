package com.example.lunapic.ui.state.media

sealed interface MediaListEvent {
    data object GoBack : MediaListEvent
    data class SetSelectedMedia(val mediaState: MediaState) : MediaListEvent
    data object DeselectAll : MediaListEvent
    data object DeleteMedias : MediaListEvent
    data object SelectAll : MediaListEvent
    data class SetScrollableState(val isScrollable: Boolean) : MediaListEvent
}