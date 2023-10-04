package bluetooth

// Implémentation spécifique à la plate-forme Android (androidMain)
import android.annotation.SuppressLint
import kotlinx.coroutines.flow.Flow
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.registerReceiver
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import splitties.init.appCtx


actual class BluetoothClient actual constructor() {
    private var bluetoothAdapter: BluetoothAdapter? = null

    private val _bluetoothStatus : MutableSharedFlow<BluetoothStatus> = MutableStateFlow(BluetoothStatus.NotInit)
    private val _deviceFlow: MutableSharedFlow<bluetooth.BluetoothDevice> = MutableSharedFlow()



    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {

            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.

                    val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    } else {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    }

                    device?.let {


                        val bluetoothDevice = BluetoothDevice(device.name ?: "null", device.address)
                        Log.i("BluetoothClient", "New device : $bluetoothDevice , alias : ${device.alias}")
                        _deviceFlow.tryEmit(bluetoothDevice)
                    }

                }
            }
        }
    }





  /*  private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (BluetoothDevice.ACTION_FOUND == intent?.action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                device?.let {
                    val bluetoothDevice = BluetoothDevice(device.name, device.address)
                    _deviceFlow.tryEmit(bluetoothDevice)
                }
            }
        }
    }*/

    init {
        Log.i("BluetoothClient", "Init")

        val bluetoothManager: BluetoothManager? = getSystemService(appCtx, BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager?.adapter
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }
    }




    @SuppressLint("MissingPermission")
    actual fun startScanning() : Flow<bluetooth.BluetoothDevice>{
        Log.i("BluetoothClient", "StartScanning")

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        appCtx.registerReceiver(receiver, filter)

        bluetoothAdapter?.startDiscovery()



        //  bluetoothAdapter?.cancelDiscovery()
      //  applicationContext.unregisterReceiver(broadcastReceiver)

        return _deviceFlow.distinctUntilChanged()
    }


    actual fun stopScanning() {
      //  bluetoothAdapter?.cancelDiscovery()
      //  applicationContext.unregisterReceiver(broadcastReceiver)
    }
}

