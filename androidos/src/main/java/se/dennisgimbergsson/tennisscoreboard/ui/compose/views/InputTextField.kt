package se.dennisgimbergsson.tennisscoreboard.ui.compose.views

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import se.dennisgimbergsson.tennisscoreboard.R

@Composable
fun InputTextField(
    text: String,
    onValueChanged: (String) -> Unit
) = OutlinedTextField(
    value = text,
    textStyle = TextStyle(
        color = colorResource(R.color.white),
    ),
    onValueChange = { newText ->
        onValueChanged(newText)
    },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    label = { Text("Enter Number") }
)