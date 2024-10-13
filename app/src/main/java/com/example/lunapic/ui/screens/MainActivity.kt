package com.example.lunapic.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lunapic.ui.components.main.BucketList
import com.example.lunapic.ui.components.main.Greeting
import com.example.lunapic.ui.theme.LunaPicTheme
import com.example.lunapic.viewmodels.BucketListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LunaPicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val bucketListViewModel: BucketListViewModel by viewModels()
                    val state by bucketListViewModel.state.collectAsStateWithLifecycle()
                    val snackBar = bucketListViewModel.snackBarHostState

                    BucketList(
                        state = state,
                        onEvent = bucketListViewModel::onEvent,
                        snackBar = snackBar
                    )
                }
            }
        }
    }
}