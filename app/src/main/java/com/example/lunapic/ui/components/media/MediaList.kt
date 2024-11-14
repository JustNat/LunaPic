package com.example.lunapic.ui.components.media

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lunapic.ui.state.media.MediaListEvent
import com.example.lunapic.ui.state.media.MediaListState
import com.example.lunapic.ui.state.media.MediaState
import com.example.lunapic.ui.theme.LunaPicTheme
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaList(state: MediaListState, bucketName: String, onEvent: (MediaListEvent) -> Unit) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val gridState = rememberLazyStaggeredGridState()

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.canScrollForward }
            .collect { canScroll -> onEvent(MediaListEvent.SetScrollableState(canScroll)) }
    }
    Scaffold(
        modifier = if (state.isLazyGridScrollable) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) else Modifier,
        snackbarHost = { SnackbarHost(state.snackBarHostState) },
        topBar = {
            MediaListTopBar(
                medias = state.medias,
                bucketName = bucketName,
                scrollBehavior = scrollBehavior,
                onEvent = onEvent
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            if (state.medias.isNotEmpty()) {
                LazyVerticalStaggeredGrid(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalItemSpacing = 16.dp,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(state.medias) { media ->
                        Media(medias = state.medias, media = media, onEvent = onEvent)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Bucket vazio.")
                }
            }
        }
        MediaDeleteDialog(state = state.deleteDialogState, onEvent = onEvent)
    }

}

@Composable
@Preview
@Preview("darkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun MediaListPreview() {

    LunaPicTheme {
        MediaList(
            state = MediaListState(medias = listOf(MediaState(File(""), true))),
            bucketName = "oieba",
            onEvent = {}
        )
    }
}