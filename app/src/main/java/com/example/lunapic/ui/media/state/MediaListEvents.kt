package com.example.lunapic.ui.media.state

sealed interface MediaListEvents {
    data object GoBack : MediaListEvents
    data class SetSelectedMedia(val mediaState: MediaState) : MediaListEvents
    data object DeselectAll : MediaListEvents
    data object SelectAll : MediaListEvents
    data object DeleteMedias : MediaListEvents
    data class SetScrollableState(val isScrollable: Boolean) : MediaListEvents
    data class SetDeleteDialogState(val state: Boolean) : MediaListEvents
}