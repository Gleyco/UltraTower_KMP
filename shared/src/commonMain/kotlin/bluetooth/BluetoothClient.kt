package bluetooth

import kotlinx.coroutines.flow.Flow


expect class BluetoothClient() {
    fun startScanning(): Flow<BluetoothDevice>
    fun stopScanning()
}


data class BluetoothDevice(val name: String, val address: String)

enum class BluetoothStatus {
    NotInit,
    NoBluetooth,
    BluetoothOff,
    Ready
}