package domain

sealed interface BluetoothRequestState{
    data object Error : BluetoothRequestState
    data object Pending : BluetoothRequestState
    data object Success : BluetoothRequestState
}

