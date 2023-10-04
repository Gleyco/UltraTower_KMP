package components.textfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalResourceApi::class)
@Composable
fun CustomPasswordTextField (
    text : String,
    label: String,
    onValueChange : (String) -> Unit,
    isPasswordVisible : Boolean = false,
    onClickPasswordVisible : () -> Unit,
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {


            val description = if (isPasswordVisible) "Hide password" else "Show password"

            IconButton(
                onClick = onClickPasswordVisible
            ){
                Icon(
                    painter = if (isPasswordVisible) painterResource("baseline_visibility_24.xml") else painterResource("baseline_visibility_off_24.xml"),
                    description
                )
            }
        },
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

