package com.example.lunapic.ui.components.media

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.lunapic.ui.state.MediaListState

@Composable
fun MediaList(state : MediaListState) {

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Inicial")
    }

    /*LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {

    }*/
}