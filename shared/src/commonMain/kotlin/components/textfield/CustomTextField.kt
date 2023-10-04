package components.textfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp



@Composable
fun CustomTextField (
    text : String,
    label: String,
    onValueChange : (String) -> Unit,
    keyboardOptions : KeyboardOptions,
    isError : Boolean = false,
    textError : String,
    modifier: Modifier,
) {

    OutlinedTextField(
        value = text,
        label = {
            Text(
                text = label,
            )
        },
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        isError = isError,
        supportingText = {
            if (isError) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    text = textError,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )

}
