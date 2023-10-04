package dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gleyco.UltraTower.SharedRes
import components.NumberPicker
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun DialogNumberPicker (
    title : String,
    currentValue : Int,
    range : IntRange,
    onDismiss : () -> Unit,
    onClickValidate : (Int) -> Unit
){

    val newValue = remember { mutableStateOf(currentValue) }

    Dialog(
        onDismissRequest = onDismiss,
    ){

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor =  Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(Modifier.size(32.dp))

                NumberPicker(
                    state = newValue,
                    range = range,
                    textStyle = MaterialTheme.typography.titleLarge,
                    onStateChanged = {newValue.value = it},
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.size(16.dp))

                TextButton(
                    onClick = { onClickValidate(newValue.value) },
                    modifier = Modifier.align(Alignment.End)

                ){
                    Text(
                        text = stringResource(SharedRes.strings.validate),
                    )

                }
            }
        }
    }
}


