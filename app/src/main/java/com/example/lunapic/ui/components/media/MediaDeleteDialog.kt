package com.example.lunapic.ui.components.media

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.lunapic.ui.components.shared.MyAlertDialog
import com.example.lunapic.ui.state.media.MediaListEvent

@Composable
fun MediaDeleteDialog(state: Boolean, onEvent: (MediaListEvent) -> Unit) {
    MyAlertDialog(
        isDialogOpen = state,
        title = "Atenção",
        text = "Tem certeza que deseja excluir as mídias selecionadas?",
        negativeLabel = "Cancelar",
        positiveLabel = "Excluir",
        onDismissRequest = { onEvent(MediaListEvent.SetDeleteDialogState(!state)) },
        onConfirmation = { onEvent(MediaListEvent.DeleteMedias) }
    )
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun MediaDeleteDialogPreview() {
    MediaDeleteDialog(state = true) { }
}