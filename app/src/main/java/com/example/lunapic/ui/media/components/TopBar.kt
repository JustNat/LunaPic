package com.example.lunapic.ui.media.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.lunapic.ui.media.state.MediaListEvents
import com.example.lunapic.ui.media.state.MediaState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    medias: List<MediaState>,
    bucketName: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onEvent: (MediaListEvents) -> Unit
) {

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { onEvent(MediaListEvents.GoBack) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Voltar"
                )
            }
        },
        title = { Text(text = bucketName, overflow = TextOverflow.Ellipsis, maxLines = 1) },
        scrollBehavior = scrollBehavior,
        actions = {
            AnimatedVisibility(
                visible = medias.any { it.isSelected },
                enter = scaleIn(animationSpec = tween(durationMillis = 100)),
                exit = scaleOut(animationSpec = tween(durationMillis = 100))
            ) {
                RoundCheckbox(
                    checked = medias.all { it.isSelected },
                    onCheckedChange = { onEvent(MediaListEvents.SelectAll) },
                    size = 18.dp,
                    iconSize = 16.dp
                )
            }
            AnimatedVisibility(
                visible = medias.any { it.isSelected },
                enter = scaleIn(animationSpec = tween(durationMillis = 100)),
                exit = scaleOut(animationSpec = tween(durationMillis = 100))
            ) {
                IconButton(onClick = { onEvent(MediaListEvents.SetDeleteDialogState(true)) }) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Excluir"
                    )
                }
            }
            AnimatedVisibility(
                visible = medias.any { it.isSelected },
                enter = scaleIn(animationSpec = tween(durationMillis = 100)),
                exit = scaleOut(animationSpec = tween(durationMillis = 100))
            ) {
                IconButton(onClick = { onEvent(MediaListEvents.DeselectAll) }) {
                    Icon(
                        imageVector = Icons.Outlined.Clear,
                        contentDescription = "Tirar seleção"
                    )
                }
            }
        }
    )
}