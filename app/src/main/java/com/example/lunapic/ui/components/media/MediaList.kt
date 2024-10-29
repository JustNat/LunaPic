package com.example.lunapic.ui.components.media

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.lunapic.ui.state.media.MediaListEvent
import com.example.lunapic.ui.state.media.MediaListState
import com.example.lunapic.ui.theme.LunaPicTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaList(state: MediaListState, bucketName: String, onEvent: (MediaListEvent) -> Unit) {

    Scaffold(
        snackbarHost = { SnackbarHost(state.snackBarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onEvent(MediaListEvent.GoBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                title = { Text(text = bucketName, overflow = TextOverflow.Ellipsis, maxLines = 1) },
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.medias.isNotEmpty()) {
                LazyVerticalStaggeredGrid(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    columns = StaggeredGridCells.Fixed(2),
                    verticalItemSpacing = 4.dp,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(state.medias) { media ->
                        AsyncImage(
                            model = media,
                            contentDescription = media.name,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Bucket vazio.")
                }
            }
        }
    }

}

@Composable
@Preview
@Preview("darkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@PreviewScreenSizes
private fun MediaListPreview() {

    LunaPicTheme {
        MediaList(
            state = MediaListState(),
            bucketName = "gplaysdoceuribeirodasilvaaraelebruceebavamosimboramachoebaaaaaa",
            onEvent = {}
        )
    }
}