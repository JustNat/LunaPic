package com.example.lunapic.ui.components.shared

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.lunapic.ui.theme.LunaPicTheme

@Composable
fun MyCustomDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                content()
            }
        }
    }
}

@Composable
@Preview
@Preview("darkTheme", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun MyCustomDialogPreview() {
    LunaPicTheme {
        Surface {
            MyCustomDialog(onDismissRequest = {}) {}
        }
    }
}