package com.example.lunapic.ui.media.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.lunapic.ui.media.state.MediaListEvents
import com.example.lunapic.ui.media.state.MediaState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Media(medias: List<MediaState>, media: MediaState, onEvent: (MediaListEvents) -> Unit) {
    AsyncImage(
        modifier = Modifier
            .combinedClickable(
                onClick = {
                    if (medias.any { mediaState -> mediaState.isSelected }) {
                        onEvent(MediaListEvents.SetSelectedMedia(media))
                    }
                    // TODO("Abrir tela de foto única")
                },
                onLongClick = {
                    if (medias.any { mediaState -> !mediaState.isSelected }) {
                        onEvent(MediaListEvents.SetSelectedMedia(media))
                    }
                }
            )
            .clip(RoundedCornerShape(16.dp)),
        model = media.file,
        contentDescription = media.file.name,
        contentScale = ContentScale.Crop,
    )
    Box(contentAlignment = Alignment.TopStart) {
        AnimatedVisibility(
            visible = medias.any { mediaState -> mediaState.isSelected },
            enter = scaleIn(tween(durationMillis = 100)),
            exit = scaleOut(tween(durationMillis = 100))
        ) {
            RoundCheckbox(
                checked = media.isSelected,
                onCheckedChange = {
                    onEvent(MediaListEvents.SetSelectedMedia(media))
                }
            )
        }
    }
}