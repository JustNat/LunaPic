package com.example.lunapic.ui.home.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.lunapic.ui.theme.LunaPicTheme
import java.time.LocalDateTime
import java.time.Month

@Composable
fun Greeting(
    dateTime: LocalDateTime = LocalDateTime.now(),
    name : String,
    hPadding : Dp = 0.dp,
    vPadding : Dp = 0.dp
) {
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
    Box(modifier = Modifier.padding(vertical = vPadding, horizontal = hPadding)) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
    }
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