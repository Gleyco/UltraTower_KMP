package connexionScreen


import childScope
import com.juul.kable.Advertisement
import com.juul.kable.Scanner
import com.juul.kable.logs.Logging
import com.juul.kable.logs.SystemLogEngine
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull


internal class ConnexionViewModel(
    val permissionsController: PermissionsController
) : ViewModel() {

    private val scanScope = viewModelScope.childScope()
    private val SCAN_DURATION_MILLIS = 10000L

    private val _status = MutableStateFlow<ScanStatus>(ScanStatus.Stopped)
    val status = _status.asStateFlow()


    private val _state: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val state: StateFlow<Boolean> get() = _state

    init {
        viewModelScope.launch {
            _state.value = permissionsController.isPermissionGranted(Permission.BLUETOOTH_SCAN)
        }
    }


    fun onEvent(event: ConnexionEvent) {
        when (event) {

            ConnexionEvent.StartDiscoverDevices -> {
                viewModelScope.launch {
                    try {

                        permissionsController.providePermission(Permission.BLUETOOTH_SCAN)
                        permissionsController.providePermission(Permission.BLUETOOTH_CONNECT)

                        _state.value = true

                        scan()
                    } catch (exc: RequestCanceledException) {
                        _state.value = false
                    } catch (exc: DeniedException) {
                        _state.value = false
                    }
                }
            }

            ConnexionEvent.ClearStatus -> {
                _status.value = ScanStatus.Stopped
            }

        }
    }


    private suspend fun scan() {
        if (_status.value == ScanStatus.Scanning) return // Scan already in progress.
        _status.value = ScanStatus.Scanning

        scanScope.launch {
            withTimeoutOrNull(SCAN_DURATION_MILLIS) {
                Scanner {
                    filters = null
                    logging {
                        engine = SystemLogEngine
                        level = Logging.Level.Warnings
                        format = Logging.Format.Multiline
                    }
                }
                    .advertisements
                    .catch { cause ->
                        _status.value =
                            ScanStatus.Failed(cause.message ?: "Unknown error")
                    }
                    .onCompletion { cause ->
                        if (cause == null || cause is CancellationException) _status.value =
                            ScanStatus.Stopped
                    }
                    .collectLatest { advertisement ->
                        println("Bluetooth scan : ${advertisement.name} , ${advertisement.identifier.toString()}, ")

                        if (advertisement.name?.startsWith("Ultra") == true) {
                            println("Bluetooth scan -> Found ultratower")
                           /* val peripheral = viewModelScope.peripheral(advertisement) {
                                // Set peripheral configuration.
                                println("Bluetooth scan connected")
                            }*/



                          //  ultraTowerPeripheral = peripheral
                            // connectScope.enableAutoReconnect()
                           // connectScope.connect()
                            _status.value = ScanStatus.Success(advertisement)
                          //  scanScope.cancelChildren()
                        }
                    }
            }
        }
    }


    sealed class ScanStatus {
        data object Stopped : ScanStatus()
        data object Scanning : ScanStatus()
        data class Success(val advertisement: Advertisement) : ScanStatus()
        data class Failed(val message: CharSequence) : ScanStatus()
    }
}