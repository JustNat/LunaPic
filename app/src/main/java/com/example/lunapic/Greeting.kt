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
fun Greeting(dateTime: LocalDateTime = LocalDateTime.now(), name : String) {
    val greeting : String =
        if (dateTime.month == Month.DECEMBER && dateTime.dayOfMonth == 25 ) {
            "Feliz Natal, $name"

        } else if (dateTime.month == Month.DECEMBER && dateTime.dayOfMonth == 31) {
            "Feliz ano novo, $name"

        } else if (dateTime.month == Month.MAY && dateTime.dayOfMonth == 22) {
            "Feliz aniversário, GPlays"

        } else {
            when (dateTime.hour) {
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
            Greeting(name = "Gabriel")
        }
    }
}