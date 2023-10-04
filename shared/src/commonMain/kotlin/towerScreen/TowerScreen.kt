package towerScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bluetooth.BluetoothEvent
import bluetooth.UltraTowerModel
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import towerScreen.components.ToolBar
import towerScreen.screens.MainTowerScreen
import towerScreen.screens.MistersTowerScreen
import towerScreen.screens.PHTowerScreen
import towerScreen.screens.ThingSpeakScreen


@Composable
fun TowerScreen(
    towerData : State<UltraTowerModel?>,
    onEventBLE : (BluetoothEvent) -> Unit,
    onBackNavigation : () -> Unit
) {

    val viewModel: TowerScreenViewModel = getViewModel(
        key = "tower_viewmodel",
        factory = viewModelFactory { TowerScreenViewModel() }
    )

    val currentNavigationScreen = viewModel.currentNavigation.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        ToolBar(
            title = "UltraTower",
            onBackNavigation = {
                if (currentNavigationScreen.value != TowerScreenViewModel.NavigationTower.MainScreen){
                    viewModel.onEvent(TowerScreenEvent.OnNavigateTo(TowerScreenViewModel.NavigationTower.MainScreen))
                }else{
                    onBackNavigation()
                }

            }
        )

        when(currentNavigationScreen.value){

            TowerScreenViewModel.NavigationTower.MainScreen -> {
                MainTowerScreen(
                    towerData = towerData,
                    onNavigateTo = {
                        viewModel.onEvent(TowerScreenEvent.OnNavigateTo(it))
                    }
                )
            }

            TowerScreenViewModel.NavigationTower.MisterScreen -> {
                MistersTowerScreen(
                    isMistersEnable = towerData.value?.mistEnable ?: false,
                    frequencyMister = towerData.value?.mistFrequency,
                    timeCycleOnMister = towerData.value?.timeCycleOnMist,
                    timeCycleOffMister = towerData.value?.timeCycleOffMist,
                    onEventBLE = onEventBLE
                )

            }

            TowerScreenViewModel.NavigationTower.PHScreen -> {
                PHTowerScreen(
                    phValue = towerData.value?.phData,
                    isPHEnabled = towerData.value?.phEnable ?: false,
                    onEventBLE = onEventBLE,
                    phRequestState = towerData.value?.phRequestState
                )
            }

            TowerScreenViewModel.NavigationTower.ThingSpeakScreen -> {
                ThingSpeakScreen(
                    isThingSpeakEnabled = towerData.value?.thingSpeakEnable ?: false,
                    onEventBLE = onEventBLE,
                    dataTowerModel = towerData.value
                )
            }
        }

     /*   Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())

        ) {

            Spacer(Modifier.size(32.dp))

            MainTile(
                iconPainter = painterResource("ic_fog.xml"),
                color = Color(0xFFBFDDDB),
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            ){

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = towerData.value?.statusMister?.let {
                            if (it){
                                stringResource(SharedRes.strings.mister_on)
                            }else{
                                stringResource(SharedRes.strings.mister_off)
                            }
                        } ?:  stringResource(SharedRes.strings.no_data),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(vertical = 32.dp)
                    )

                    Text(
                        text = "Misters",
                        style = MaterialTheme.typography.bodyMedium,

                    )
                }

            }

        }*/





    }
}
