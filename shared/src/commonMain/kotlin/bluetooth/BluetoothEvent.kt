package bluetooth


sealed interface BluetoothEvent {

  //  data class DeleteProductFavorite (val idProduct : String) : BluetoothEvent

    data object StartDiscoverDevices : BluetoothEvent

    data object DisconnectTower : BluetoothEvent

    data class SendDataToTower (val code : String, val value : Any) : BluetoothEvent

    data class UpdateDataOfTower (val data : UltraTowerModel?) : BluetoothEvent

    data object ClearPHRequest : BluetoothEvent

}