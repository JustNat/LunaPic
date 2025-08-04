package com.example.lunapic.ui.media.components

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.lunapic.ui.common.MyAlertDialog
import com.example.lunapic.ui.media.state.MediaListEvents

@Composable
fun DeleteMediaDialog(state: Boolean, onEvent: (MediaListEvents) -> Unit) {
    MyAlertDialog(
        isDialogOpen = state,
        title = "Atenção",
        text = "Tem certeza que deseja excluir as mídias selecionadas?",
        negativeLabel = "Cancelar",
        positiveLabel = "Excluir",
        onDismissRequest = { onEvent(MediaListEvents.SetDeleteDialogState(!state)) },
        onConfirmation = { onEvent(MediaListEvents.DeleteMedias) }
    )
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun DeleteMediaDialogPreview() {
    DeleteMediaDialog(state = true) { }
}