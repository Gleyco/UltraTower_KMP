package towerScreen.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bluetooth.BluetoothEvent
import com.gleyco.UltraTower.SharedRes
import dev.icerock.moko.resources.compose.stringResource
import dialogs.DialogNumberPicker
import dialogs.DialogPHCalibration
import domain.BluetoothRequestState
import towerScreen.components.CustomSwitch


@Composable
fun PHTowerScreen(
    phValue : String?,
    isPHEnabled : Boolean,
    onEventBLE : (BluetoothEvent) -> Unit,
    phRequestState: BluetoothRequestState?

) {


    //******************** DIALOGS ******************************

    var isDialogPH4Open by remember { mutableStateOf(false) }
    if (isDialogPH4Open) {

        //pH Calibration 4 dialog
        DialogPHCalibration(
            requestState = phRequestState,
            onDismiss = { isDialogPH4Open = false },
            onClickCalibrate = {
                onEventBLE(
                    BluetoothEvent.SendDataToTower(
                        code = "PHCAL4",
                        value = ""
                    )
                )
            }
        )

    }

    var isDialogPH9Open by remember { mutableStateOf(false) }
    if (isDialogPH9Open) {

        //pH Calibration 9 dialog
        DialogPHCalibration(
            requestState = phRequestState,
            onDismiss = { isDialogPH9Open = false },
            onClickCalibrate = {
                onEventBLE(
                    BluetoothEvent.SendDataToTower(
                        code = "PHCAL9",
                        value = ""
                    )
                )
            }
        )
    }



    //******************** UI ******************************

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(Modifier.size(32.dp))

        Text(
            text = phValue?.let { "pH $it" } ?: "pH --",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(Modifier.size(16.dp))


        CustomSwitch(
            text = stringResource(SharedRes.strings.frag_ph_enable),
            isChecked = isPHEnabled,
            onCheck = {
                onEventBLE(BluetoothEvent.SendDataToTower(
                    code = "PHENA",
                    value = !isPHEnabled
                ))
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(Modifier.size(24.dp))


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ){
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ){

                Text(
                    text = stringResource( SharedRes.strings.frag_ph_title_calib),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                )

                Spacer(Modifier.size(16.dp))

                Text(
                    text =  stringResource( SharedRes.strings.frag_ph_descr_calib),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(Modifier.size(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ){
                    OutlinedButton(
                        onClick = {
                            onEventBLE(BluetoothEvent.ClearPHRequest)
                            isDialogPH4Open = true
                        }
                    ){
                        Text(
                            text =  "pH 4",
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            onEventBLE(BluetoothEvent.ClearPHRequest)
                            isDialogPH9Open = true
                        }
                    ){
                        Text(
                            text =  "pH 9",
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }



            }
        }
    }
}