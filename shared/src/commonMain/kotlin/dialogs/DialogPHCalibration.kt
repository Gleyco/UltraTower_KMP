package dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gleyco.UltraTower.SharedRes
import components.ButtonLoading
import dev.icerock.moko.resources.compose.stringResource
import domain.BluetoothRequestState
import kotlinx.coroutines.delay


@Composable
fun DialogPHCalibration (
    requestState : BluetoothRequestState?,
    onDismiss : () -> Unit,
    onClickCalibrate : () -> Unit
) {
    LaunchedEffect(key1 = requestState ){
        if (requestState is BluetoothRequestState.Success){
            delay(1000)
            onDismiss()
        }
    }


    Dialog(
        onDismissRequest = onDismiss,
    ) {

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    text = stringResource(SharedRes.strings.frag_ph_title_calib),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(Modifier.size(32.dp))

                Text(
                    text = when(requestState){
                        BluetoothRequestState.Error -> stringResource(SharedRes.strings.frag_ph_calib_error)
                        BluetoothRequestState.Pending ->  stringResource(SharedRes.strings.frag_ph_iscalib)
                        BluetoothRequestState.Success ->  stringResource(SharedRes.strings.frag_ph_calib_success)
                        else -> stringResource(SharedRes.strings.dialog_ph_dscr_calib)
                    }
                    ,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(Modifier.size(16.dp))


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedButton(
                        onClick = {
                            onDismiss()
                        },
                        enabled = requestState !is BluetoothRequestState.Pending,
                        modifier = Modifier.weight(1f)
                    ){
                        Text(
                            text =  stringResource(SharedRes.strings.cancel),
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }

                    Spacer(Modifier.size(8.dp))

                    ButtonLoading(
                        text = stringResource(SharedRes.strings.dialog_ph_btn_calib),
                        shape = RoundedCornerShape(50),
                        onClick = onClickCalibrate,
                        isLoading = requestState is BluetoothRequestState.Pending,
                        modifier = Modifier.weight(1f)
                    )

                }
            }
        }
    }
}