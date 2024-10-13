package com.example.lunapic.ui.components.media

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.lunapic.ui.state.MediaListState

@Composable
fun MediaList(state : MediaListState) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {

    }
}