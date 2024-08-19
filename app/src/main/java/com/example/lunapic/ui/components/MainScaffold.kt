package com.example.lunapic.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.lunapic.ui.theme.LunaPicTheme

@Composable
fun MainScaffold(onFabClick: () -> Unit, content: @Composable () -> Unit) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onFabClick() },
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "adicionar bucket")
                Text(text = "Adicionar")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ){
        Box(modifier = Modifier.padding(it)) {
            content()
        }
    }
}

@Composable
@Preview
@Preview(name = "darkTheme", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun MainScaffoldPreview() {
    LunaPicTheme {
        Surface {
            MainScaffold(onFabClick = {}, {})
        }
    }
}