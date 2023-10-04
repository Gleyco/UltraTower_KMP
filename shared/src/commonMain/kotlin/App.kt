import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import bluetooth.BluetoothEvent
import bluetooth.BluetoothViewModel
import cafe.adriel.voyager.navigator.Navigator
import connexionScreen.ConnexionEvent
import connexionScreen.ConnexionScreen
import connexionScreen.ConnexionViewModel
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import theme.UltraTowerTheme
import towerScreen.TowerScreen


@Composable
fun App() {
    UltraTowerTheme {

        val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
        val viewModel: BluetoothViewModel = getViewModel(
            key = "ble_viewmodel",
            factory = viewModelFactory { BluetoothViewModel(factory.createPermissionsController()) }
        )

        val bleStatus = viewModel.bleStatus.collectAsState()
        //val towerData = viewModel.currentStateDataTower.collectAsState()

        BindEffect(viewModel.permissionsController)

        LaunchedEffect(key1 = bleStatus.value){
            println("bleStatut -> ${bleStatus.value}")
        }





        if (bleStatus.value == BluetoothViewModel.BLEStatus.Connected || bleStatus.value == BluetoothViewModel.BLEStatus.Disconnected  ){
            TowerScreen(
                towerData = viewModel.currentStateDataTower.collectAsState(),
                onEventBLE = viewModel::onEvent,
                onBackNavigation = {
                    viewModel.onEvent(BluetoothEvent.DisconnectTower)
                }
            )
        }else{
            ConnexionScreen(
                isScanning = bleStatus.value == BluetoothViewModel.BLEStatus.Scanning,
                onClickStartScanning = { viewModel.onEvent(BluetoothEvent.StartDiscoverDevices) }
            )
        }

    }
}


