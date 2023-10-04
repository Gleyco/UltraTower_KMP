package towerScreen.screens

import domain.Utils
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bluetooth.BluetoothEvent
import com.gleyco.UltraTower.SharedRes
import dev.icerock.moko.resources.compose.stringResource
import dialogs.DialogNumberPicker
import towerScreen.components.CustomSwitch
import towerScreen.components.SecondaryTile

@Composable
fun MistersTowerScreen(
    isMistersEnable : Boolean,
    frequencyMister : Int?,
    timeCycleOnMister : Int?,
    timeCycleOffMister : Int?,
    onEventBLE : (BluetoothEvent) -> Unit

) {



    //******************** DIALOGS ******************************

    var isDialogFrequencyOpen by remember { mutableStateOf(false) }
    if (isDialogFrequencyOpen) {

        //Frequency dialog
        DialogNumberPicker(
            title = stringResource(SharedRes.strings.alert_mist_freq_title),
            currentValue = frequencyMister?.let { (it/1000) } ?: 108,
            range = 100..113,
            onDismiss = {isDialogFrequencyOpen = false},
            onClickValidate = {
                onEventBLE(BluetoothEvent.SendDataToTower(
                    code = "MFREQ",
                    value = it * 1000
                ))

                isDialogFrequencyOpen = false
            }
        )

    }


    var isDialogCycleOnOpen by remember { mutableStateOf(false) }
    if (isDialogCycleOnOpen) {

        //CycleOn dialog
        DialogNumberPicker(
            title = stringResource(SharedRes.strings.alert_mist_cycle_title),
            currentValue = timeCycleOnMister?.let { Utils.formatMillisecondsToMin(it) } ?: 10,
            range = 5..60,
            onDismiss = {isDialogCycleOnOpen = false},
            onClickValidate = {
                onEventBLE(BluetoothEvent.SendDataToTower(
                    code = "MCYCL",
                    value = Pair(Utils.formatMinToMilliseconds(it), timeCycleOffMister)
                ))

                isDialogCycleOnOpen = false
            }
        )

    }

    var isDialogCycleOffOpen by remember { mutableStateOf(false) }
    if (isDialogCycleOffOpen) {

        //CycleOn dialog
        DialogNumberPicker(
            title = stringResource(SharedRes.strings.alert_mist_cycle_title),
            currentValue = timeCycleOffMister?.let { Utils.formatMillisecondsToMin(it) } ?: 10,
            range = 0..60,
            onDismiss = {isDialogCycleOffOpen = false},
            onClickValidate = {
                onEventBLE(BluetoothEvent.SendDataToTower(
                    code = "MCYCL",
                    value = Pair(timeCycleOnMister, Utils.formatMinToMilliseconds(it))
                ))

                isDialogCycleOffOpen = false
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

        CustomSwitch(
            text = stringResource(SharedRes.strings.frag_mist_enable),
            isChecked = isMistersEnable,
            onCheck = {
                  onEventBLE(BluetoothEvent.SendDataToTower(
                      code = "MENA",
                      value = !isMistersEnable
                  ))
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(Modifier.size(16.dp))

        SecondaryTile(
            title = stringResource(SharedRes.strings.frag_mist_freq_title),
            value = frequencyMister?.let { (it/1000).toString() } ,
            unit = stringResource(SharedRes.strings.frag_mist_khz),
            description = stringResource(SharedRes.strings.frag_mist_freq_descr),
            onClick = { isDialogFrequencyOpen = true },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.size(16.dp))

        SecondaryTile(
            title = stringResource(SharedRes.strings.frag_mist_cycle_on_title),
            value = timeCycleOnMister?.let{ Utils.formatMillisecondsToMin(it).toString() },
            unit = stringResource(SharedRes.strings.frag_mist_min),
            description = stringResource(SharedRes.strings.frag_mist_cycle_on_descr),
            onClick = {isDialogCycleOnOpen = true},
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.size(16.dp))

        SecondaryTile(
            title = stringResource(SharedRes.strings.frag_mist_cycle_off_title),
            value = timeCycleOffMister?.let{ Utils.formatMillisecondsToMin(it).toString() },
            unit = stringResource(SharedRes.strings.frag_mist_min),
            description = stringResource(SharedRes.strings.frag_mist_cycle_off_descr),
            onClick = {isDialogCycleOffOpen = true},
            modifier = Modifier.fillMaxWidth(),
        )
    }

}