package towerScreen

import bluetooth.BluetoothEvent
import bluetooth.BluetoothViewModel
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.RequestCanceledException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class TowerScreenViewModel(

) : ViewModel() {

    private val _currentNavigation = MutableStateFlow<NavigationTower>(NavigationTower.MainScreen)
    val currentNavigation = _currentNavigation.asStateFlow()


    fun  onEvent (event: TowerScreenEvent) {
        when (event) {
            is TowerScreenEvent.OnNavigateTo -> {
                _currentNavigation.value = event.screen
            }
        }
    }


    enum class NavigationTower {
        MainScreen,
        MisterScreen,
        PHScreen,
        ThingSpeakScreen
    }

}