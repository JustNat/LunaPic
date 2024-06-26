package com.example.lunapic

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.lunapic.ui.theme.LunaPicTheme
import java.time.LocalDateTime

@Composable
fun Greeting() {
    val greeting : String = when (LocalDateTime.now().hour) {
        in 0..11 -> {
            "Bom dia, Gabriel"
        }
        in 12..19 -> {
            "Boa tarde, Gabriel"
        }
        else -> "Boa noite, Gabriel"
    }
    Text(
        text = greeting,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

@Preview("Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun GreetingPreview() {
    LunaPicTheme {
        Surface {
            Greeting()
        }
    }
}