package towerScreen.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import bluetooth.BluetoothEvent
import bluetooth.UltraTowerModel
import com.gleyco.UltraTower.SharedRes
import components.ButtonLoading
import components.textfield.CustomPasswordTextField
import components.textfield.CustomTextField
import dev.icerock.moko.resources.compose.stringResource
import domain.BluetoothRequestState
import towerScreen.components.CustomSwitch


@Composable
fun ThingSpeakScreen(
    isThingSpeakEnabled : Boolean,
    dataTowerModel: UltraTowerModel?,
    onEventBLE : (BluetoothEvent) -> Unit,
) {

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(Modifier.size(32.dp))

        CustomSwitch(
            text = stringResource(SharedRes.strings.bottomsheet_enable_thingspeak),
            isChecked = isThingSpeakEnabled,
            onCheck = {

                //TODO toast error
                dataTowerModel?.let { data ->
                    if (data.ssid.isNotBlank() && data.apiKeyThingSpeak.isNotBlank()){
                        onEventBLE(
                            BluetoothEvent.SendDataToTower(
                                code = "THENA",
                                value = !isThingSpeakEnabled
                            )
                        )
                    }
                }

            },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(Modifier.size(24.dp))

        Text(
            text = stringResource(SharedRes.strings.bottomsheet_descr_thingspeak),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(Modifier.size(16.dp))

        CustomTextField(
            text = dataTowerModel?.intervalTimeThingSpeak?.toString() ?: "10",
            label = stringResource(SharedRes.strings.bottomsheet_hint_time_thingspeak),
            onValueChange = {
                it.toIntOrNull()?.let { interval ->
                    dataTowerModel?.let { currentData ->
                        onEventBLE(BluetoothEvent.UpdateDataOfTower(
                            currentData.copy(intervalTimeThingSpeak = interval)
                        ))
                    }
                }
            },
            keyboardOptions =  KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = false,
            textError = "",
            modifier = Modifier
        )

        CustomTextField(
            text = dataTowerModel?.ssid ?: "",
            label = stringResource(SharedRes.strings.bottomsheet_hint_ssid_thingspeak),
            onValueChange = {
                dataTowerModel?.let { currentData ->
                    onEventBLE(BluetoothEvent.UpdateDataOfTower(
                        currentData.copy(ssid = it)
                    ))
                }
            },
            keyboardOptions =  KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = false,
            textError = "",
            modifier = Modifier
        )

        var passwordVisible by rememberSaveable { mutableStateOf(false) }

        CustomPasswordTextField(
            text = dataTowerModel?.wifiPassWord ?:  "",
            label = stringResource(SharedRes.strings.bottomsheet_hint_pw_thingspeak),
            onValueChange = {
                dataTowerModel?.let { currentData ->
                    onEventBLE(BluetoothEvent.UpdateDataOfTower(
                        currentData.copy(wifiPassWord = it)
                    ))
                }
            },
            isError = false,
            textError = "",
            isPasswordVisible = passwordVisible,
            onClickPasswordVisible = {
                passwordVisible = !passwordVisible
            },
            modifier = Modifier,


        )

        CustomTextField(
            text =  dataTowerModel?.apiKeyThingSpeak ?: "",
            label = stringResource(SharedRes.strings.bottomsheet_hint_apikey_thingspeak),
            onValueChange = {
                dataTowerModel?.let { currentData ->
                    onEventBLE(BluetoothEvent.UpdateDataOfTower(
                        currentData.copy(apiKeyThingSpeak = it)
                    ))
                }
            },
            keyboardOptions =  KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = false,
            textError = "",
            modifier = Modifier
        )

        CustomTextField(
            text =  dataTowerModel?.channelThingSpeak?.toString() ?: "",
            label = stringResource(SharedRes.strings.bottomsheet_hint_channel_thingspeak),
            onValueChange = {
                it.toLongOrNull()?.let { channel ->
                    dataTowerModel?.let { currentData ->
                        onEventBLE(
                            BluetoothEvent.UpdateDataOfTower(
                                currentData.copy(channelThingSpeak = channel)
                            )
                        )
                    }
                }
            },
            keyboardOptions =  KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = false,
            textError = "",
            modifier = Modifier
        )

        Spacer(Modifier.size(16.dp))

        ButtonLoading(
            text = stringResource(SharedRes.strings.validate),
            shape = RoundedCornerShape(50),
            onClick = {
                //TODO error snack or toast
                dataTowerModel?.let { data ->
                    if (data.ssid.isNotBlank() && data.apiKeyThingSpeak.isNotBlank()) {
                        onEventBLE(
                            BluetoothEvent.SendDataToTower(
                                code = "THING",
                                value = ""
                            )
                        )
                    }
                }
            },
            isLoading = dataTowerModel?.thingSpeakRequestState is BluetoothRequestState.Pending,
            modifier = Modifier.align(Alignment.End)
        )




    }



}
