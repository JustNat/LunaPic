package com.example.lunapic

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.lunapic.ui.theme.LunaPicTheme
import java.time.LocalDateTime
import java.time.Month

@Composable
fun Greeting(name : String) {
    val date = LocalDateTime.now()
    val greeting : String =
        if (date.month == Month.DECEMBER && date.dayOfMonth == 25 ) {
            "Feliz Natal, $name"

        } else if (date.month == Month.DECEMBER && date.dayOfMonth == 31) {
            "Feliz ano novo, $name"

        } else if (date.month == Month.MAY && date.dayOfMonth == 22) {
            "Feliz aniversário, GPlays"

        } else {
            when (date.hour) {
                in 0..11 -> {
                    "Bom dia, $name"
                }

                in 12..19 -> {
                    "Boa tarde, $name"
                }

                else -> "Boa noite, $name"
            }
        }

    Text(
        text = greeting,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

@Preview(
    "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun GreetingPreview() {
    LunaPicTheme {
        Surface {
            Greeting("Gabriel")
        }
    }
}