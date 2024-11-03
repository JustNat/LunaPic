package com.example.lunapic.ui.state.media

import java.io.File

data class MediaState(
    val file: File,
    val isSelected: Boolean = false
)
