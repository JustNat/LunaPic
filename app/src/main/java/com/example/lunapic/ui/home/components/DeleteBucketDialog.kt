package com.example.lunapic.ui.home.components

import androidx.compose.runtime.Composable
import com.example.lunapic.ui.common.MyAlertDialog
import com.example.lunapic.ui.home.state.HomeScreenEvents

@Composable
fun DeleteBucketDialog(isOpen: Boolean, bucket: String, onEvent: (HomeScreenEvents) -> Unit) {
    MyAlertDialog(
        isDialogOpen = isOpen,
        title = "Atenção",
        text = "Deseja mesmo excluir o bucket ${bucket}?",
        negativeLabel = "Cancelar",
        positiveLabel = "Confirmar",
        onDismissRequest = {
            onEvent(HomeScreenEvents.SetDeleteDialogState(false))
            onEvent(HomeScreenEvents.SetSelectedBucket(null))
        }
    ) {
        onEvent(HomeScreenEvents.DeleteBucket(bucket))
    }
}