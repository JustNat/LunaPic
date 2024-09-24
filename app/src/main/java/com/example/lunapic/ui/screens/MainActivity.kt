package com.example.lunapic.ui.screens

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lunapic.ui.components.BucketList
import com.example.lunapic.ui.components.Greeting
import com.example.lunapic.ui.theme.LunaPicTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LunaPicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App() {
    var isDialogOpen by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = { isDialogOpen = true }) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "adicionar bucket")
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Greeting(name = "Gabriel", vpadding = 6.dp)
                BucketList(
                    createBucketDialogOpen = isDialogOpen,
                    closeCreateBucketDialog = { isDialogOpen = false },
                    onCreatedBucket = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Bucket criado")
                        }
                    },
                    onDeletedBucket = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Bucket deletado")
                        }
                    },
                    onErrorCreatingBucket = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                it.localizedMessage ?: "Houve um erro ao criar o bucket"
                            )
                        }
                    },
                    onErrorDeletingBucket = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                it.localizedMessage ?: "Houve um erro ao excluir o bucket"
                            )
                        }
                    }
                )
            }
        }
    }
}

@Preview("Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview("Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AppPreview() {
    LunaPicTheme {
        Surface {
            App()
        }
    }
}