package bluetooth

import cancelChildren
import childScope
import com.juul.kable.ConnectionLostException
import com.juul.kable.ConnectionRejectedException
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.logs.Logging
import com.juul.kable.logs.SystemLogEngine
import com.juul.kable.peripheral
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import domain.BluetoothRequestState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


internal class BluetoothViewModel(
    val permissionsController: PermissionsController
) : ViewModel() {

    private val scanScope = viewModelScope.childScope()
    private val SCAN_DURATION_MILLIS = 10_000L


    private val _bleStatus = MutableStateFlow<BLEStatus>(BLEStatus.WaitingForScan)
    val bleStatus = _bleStatus.asStateFlow()



    //CONNEXION VARIABLES
    private val connectScope = viewModelScope.childScope()
    private var ultraTowerPeripheral :  Peripheral? = null
    private var ultraTowerDevice : UltraTower? = null
    private var connectionAttempt = 0


    //BLE OPERATION
    private val _operationQueue : MutableStateFlow<MutableList<String>> = MutableStateFlow(mutableListOf())
    private val _pendingOperation : MutableStateFlow<String?> = MutableStateFlow(null)


    val currentStateDataTower: StateFlow<UltraTowerModel?> get() =  ultraTowerDevice?.dataUltraTower ?: MutableStateFlow(null)





    fun  onEvent (event: BluetoothEvent) {
        when (event) {

            BluetoothEvent.StartDiscoverDevices -> {
                viewModelScope.launch {
                    try {

                        permissionsController.providePermission(Permission.BLUETOOTH_SCAN)
                        permissionsController.providePermission(Permission.BLUETOOTH_CONNECT)

                        scan()
                    } catch (exc: RequestCanceledException) {
                        _bleStatus.value = BLEStatus.ScanningFailed("error_permission")
                    } catch (exc: DeniedException) {
                        _bleStatus.value = BLEStatus.ScanningFailed("error_permission")
                    }
                }
            }

            BluetoothEvent.DisconnectTower -> {
                viewModelScope.launch {
                    stopConnection()
                }

            }

            is BluetoothEvent.SendDataToTower -> {

                viewModelScope.launch {
                    ultraTowerDevice?.let { ultraTower ->
                        ultraTower.dataUltraTower.value?.let { currentDataTower ->
                            when(event.code){
                                //Enable or disable the mister(s)
                                "MENA" -> {

                                    ultraTower.updateDataTower(
                                        data = currentDataTower.copy(mistEnable = event.value as Boolean)
                                    )

                                    enqueueOperation("MENA")

                                }

                                "PHENA" -> {

                                    ultraTower.updateDataTower(
                                        data = currentDataTower.copy(
                                            phEnable = event.value as Boolean,
                                            phData = null
                                        )
                                    )

                                    enqueueOperation("PHENA")

                                }

                                "MFREQ" -> {

                                    ultraTower.updateDataTower(
                                        data = currentDataTower.copy(mistFrequency = event.value as Int)
                                    )

                                    enqueueOperation("MFREQ")

                                }

                                "MCYCL" -> {

                                    val data = event.value as Pair<*, *>

                                    ultraTower.updateDataTower(
                                        data = currentDataTower.copy(
                                             timeCycleOnMist = data.first as Int,
                                            timeCycleOffMist = data.second as Int
                                        )
                                    )

                                    enqueueOperation("MCYCL")

                                }

                                "PHCAL4" -> {

                                    ultraTower.updateDataTower(
                                        data = currentDataTower.copy(
                                            phRequestState = BluetoothRequestState.Pending
                                        )
                                    )

                                    enqueueOperation("PHCAL4")

                                }

                                "PHCAL9" -> {

                                    ultraTower.updateDataTower(
                                        data = currentDataTower.copy(
                                            phRequestState = BluetoothRequestState.Pending
                                        )
                                    )

                                    enqueueOperation("PHCAL9")

                                }

                                "THENA" -> {

                                    ultraTower.updateDataTower(
                                        data = currentDataTower.copy(
                                            thingSpeakEnable = event.value as Boolean,
                                        )
                                    )

                                    enqueueOperation("THENA")

                                }

                                "THING" -> {

                                    ultraTower.updateDataTower(
                                        data = currentDataTower.copy(
                                            thingSpeakRequestState = BluetoothRequestState.Pending
                                        )
                                    )

                                    enqueueOperation("THING")

                                }
                            }
                        }
                    }
                }
            }

            //Set request to null when open new calibration ph dialog
            BluetoothEvent.ClearPHRequest -> {
                viewModelScope.launch {
                    ultraTowerDevice?.let { ultraTower ->
                        ultraTower.dataUltraTower.value?.let { currentDataTower ->
                            ultraTower.updateDataTower(
                                data = currentDataTower.copy(
                                    phRequestState = null,
                                )
                            )
                        }
                    }
                }
            }

            //Set request to null when open new calibration ph dialog
            is BluetoothEvent.UpdateDataOfTower -> {
                event.data?.let { newData ->

                    ultraTowerDevice?.updateDataTower(
                        data = newData
                    )

                }

            }

        }
    }




    //***************************************************************************
    //************************** SCANNING FUNCTIONS *****************************


    private suspend fun scan() {
        if (_bleStatus.value == BLEStatus.Scanning || _bleStatus.value == BLEStatus.Connected) return // Scan already in progress or already connected.
        _bleStatus.value = BLEStatus.Scanning

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
                        _bleStatus.value = BLEStatus.ScanningFailed(cause.message ?: "Unknown error")
                    }
                    .onCompletion { cause ->
                        println("bleStatut cause -> ${cause?.message}")

                        if (cause == null || cause is TimeoutCancellationException)  _bleStatus.value = BLEStatus.WaitingForScan
                    }
                    .collectLatest { advertisement ->
                        println("Bluetooth scan : ${advertisement.name} , ${advertisement.identifier.toString()}, ")

                        if (advertisement.name?.startsWith("Ultra") == true) {
                            println("Bluetooth scan -> Found ultratower")

                            //Init connexion with UltraTower
                            ultraTowerPeripheral = connectScope.peripheral(advertisement)
                            ultraTowerPeripheral?.let { ultraTowerDevice = UltraTower(it) }
                            connectScope.connect()


                        }
                    }
            }
        }
    }



    //***************************************************************************
    //************************** CONNECTED FUNCTIONS ****************************

    private fun CoroutineScope.connect() {

        connectionAttempt++
        launch {
            println( "connect" )
            try {
                ultraTowerPeripheral?.connect()
                _bleStatus.value = BLEStatus.Connected
                getFlowData()

                connectionAttempt = 0
                enqueueOperation("read")
                enqueueOperation("MISON")

                //Stop the scan coroutine
                scanScope.cancelChildren()

            } catch (e: ConnectionLostException) {
                println( "Connection attempt failed")

                _bleStatus.value = BLEStatus.Disconnected

                if (connectionAttempt >= 3){
                    _bleStatus.value = BLEStatus.WaitingForScan
                    stopConnection()
                }
            } catch (e : ConnectionRejectedException){
                _bleStatus.value = BLEStatus.WaitingForScan
                stopConnection()
            }


        }
    }

   /* private fun CoroutineScope.connect() {
        if (ultraTowerPeripheral == null) return
        connectionAttempt++
        launch {
            println( "connect" )
            try {
                ultraTowerPeripheral!!.connect()
                ultraTowerDevice = UltraTower(ultraTowerPeripheral!!)
                getFlowData()
                ultraTowerDevice?.readJson()
                connectionAttempt = 0

              /*  println( "datatower -> try send packet")
                val packetToSend = BluetoothPacketModel(
                    CMD = "MENA",
                    DATA = "0"
                )
                ultraTowerDevice
                    ?.writeToTower(
                        Json.encodeToString(packetToSend)
                    )*/




            } catch (e: ConnectionLostException) {
                println( "Connection attempt failed")

                if (connectionAttempt >= 3){
                    stopRetryConnection()
                }
            }
        }
    }*/

    private suspend fun getFlowData() {
        connectScope.launch {
            _pendingOperation.collectLatest { dataToSend ->
                println("datatower -> _pendingOperation -> collectLatest -> $dataToSend")
                when (dataToSend) {
                    "read" -> ultraTowerDevice?.readJson()

                    "MFREQ" -> {
                        currentStateDataTower.value?.mistFrequency?.let {
                            val packetToSend = BluetoothPacketModel(
                                CMD = "MFREQ",
                                DATA_FREQ = it
                            )

                            ultraTowerDevice
                                ?.writeToTower(
                                    Json.encodeToString(packetToSend)
                                )
                        }
                    }

                    "MCYCL" -> {
                        currentStateDataTower.value?.let { dataTower ->
                            val packetToSend = BluetoothPacketModel(
                                CMD = "MCYCL",
                                DATA_ON = dataTower.timeCycleOnMist,
                                DATA_OFF = dataTower.timeCycleOffMist
                            )

                            ultraTowerDevice
                                ?.writeToTower(
                                    Json.encodeToString(packetToSend)
                                )
                        }
                    }

                    "MENA" -> {
                        currentStateDataTower.value?.mistEnable?.let {
                            val packetToSend = BluetoothPacketModel(
                                CMD = "MENA",
                                DATA = it
                            )

                            ultraTowerDevice
                                ?.writeToTower(
                                    Json.encodeToString(packetToSend)
                                )
                        }
                    }

                    "PHENA" -> {
                        currentStateDataTower.value?.phEnable?.let {
                            val packetToSend = BluetoothPacketModel(
                                CMD = "PHENA",
                                DATA = it
                            )

                            ultraTowerDevice
                                ?.writeToTower(
                                    Json.encodeToString(packetToSend)
                                )
                        }
                    }

                    "MISON" -> {
                        val packetToSend = BluetoothPacketModel(
                            CMD = "MISON"
                        )

                        ultraTowerDevice
                            ?.writeToTower(
                                Json.encodeToString(packetToSend)
                            )
                    }

                    "PHCAL4" -> {
                        val packetToSend = BluetoothPacketModel(
                            CMD = "PHCAL4"
                        )

                        ultraTowerDevice
                            ?.writeToTower(
                                Json.encodeToString(packetToSend)
                            )
                    }

                    "PHCAL9" -> {
                        val packetToSend = BluetoothPacketModel(
                            CMD = "PHCAL9"
                        )

                        ultraTowerDevice
                            ?.writeToTower(
                                Json.encodeToString(packetToSend)
                            )
                    }

                    "THENA" -> {
                        currentStateDataTower.value?.phEnable?.let {
                            val packetToSend = BluetoothPacketModel(
                                CMD = "THENA",
                                DATA = it
                            )

                            ultraTowerDevice
                                ?.writeToTower(
                                    Json.encodeToString(packetToSend)
                                )
                        }
                    }

                    "THING" -> {

                        currentStateDataTower.value?.let {
                            val packetToSend = BluetoothPacketModel(
                                CMD = "THING",
                                TIME = it.intervalTimeThingSpeak.toString(),
                                SSID = it.ssid,
                                APIKEY = it.apiKeyThingSpeak,
                                CHANNEL = it.channelThingSpeak.toString(),
                                PW = it.wifiPassWord
                            )

                            ultraTowerDevice
                                ?.writeToTower(
                                    Json.encodeToString(packetToSend)
                                )
                        }

                    }
                }
            }
        }

        connectScope.launch {
            ultraTowerDevice
                ?.dataTower
                ?.collectLatest { message ->


                    val data = message.replace("[^A-Za-z0-9:.]".toRegex(), "")
                    println("datatower -> data clear -> $data")

                    val separatedData: List<String> = data.split(":")

                    separatedData.getOrNull(0)?.let { code ->
                        when (code) {
                            "MON" -> {}
                            "MOFF" -> {}
                            "MISON" -> {

                                separatedData.getOrNull(1)?.let { value ->
                                    currentStateDataTower.value?.let { currentDataTower ->
                                        ultraTowerDevice?.updateDataTower(
                                            data = currentDataTower.copy(
                                                statusMister = value.toBoolean()
                                            )
                                        )
                                    }
                                }
                                signalEndOfOperation(code)
                            }

                            "PHVAL"-> {
                                separatedData.getOrNull(1)?.let { value ->
                                    value.toDoubleOrNull()?.let {
                                        val phValue = when (it) {
                                            in 0.0..3.9 -> {
                                                "< 4"
                                            }

                                            in 4.0..10.0 -> {
                                                it.toString()
                                            }

                                            in 10.1..14.0 -> {
                                                "> 10"
                                            }
                                            -1.0 -> "Error"
                                            else ->  "Error"
                                        }

                                        currentStateDataTower.value?.let { currentDataTower ->
                                            ultraTowerDevice?.updateDataTower(
                                                data = currentDataTower.copy(
                                                    phData = phValue
                                                )
                                            )
                                        }

                                    }
                                }
                            }

                            "PHCAL4" -> {
                                currentStateDataTower.value?.let { currentDataTower ->
                                    ultraTowerDevice?.updateDataTower(
                                        data = currentDataTower.copy(
                                            phRequestState = BluetoothRequestState.Success
                                        )
                                    )
                                }
                            }

                            "PHCAL9" -> {
                                currentStateDataTower.value?.let { currentDataTower ->
                                    ultraTowerDevice?.updateDataTower(
                                        data = currentDataTower.copy(
                                            phRequestState = BluetoothRequestState.Success
                                        )
                                    )
                                }
                            }

                            "THING" -> {
                                separatedData.getOrNull(1)?.let { value ->
                                    val isSuccess = value == "1"

                                    println("datatower -> THING -> $isSuccess")

                                    currentStateDataTower.value?.let { currentDataTower ->
                                        ultraTowerDevice?.updateDataTower(
                                            data = currentDataTower.copy(
                                                thingSpeakRequestState = if (isSuccess) BluetoothRequestState.Success else BluetoothRequestState.Error,
                                                thingSpeakEnable = isSuccess
                                            )
                                        )
                                    }


                                }

                            }



                            /*"MFREQ" -> signalEndOfOperation(code)

                            "MCYCL" -> signalEndOfOperation(code)*/

                            else -> {signalEndOfOperation(code)}

                        }
                    }
                }

        }
    }


    private suspend fun stopConnection() {
        _operationQueue.value.clear()
        _pendingOperation.value = null
        ultraTowerPeripheral?.disconnect()
        connectScope.cancelChildren()
        ultraTowerPeripheral = null
        ultraTowerDevice = null
        _bleStatus.value = BLEStatus.WaitingForScan
    }




    //BLE LOGIC
    fun enqueueOperation(operation: String) {
       // println("BLE OPERATION ENQUEUE -> $operation")

        println("********** BLE OPERATION ENQUEUE **********")
        _operationQueue.value.add(operation)

        _operationQueue.value.forEach {
            println("BLE OPERATION QUEUE -> $it")
        }

        println("******************************************")

        if (_pendingOperation.value == null) {
            doNextOperation()
        }
    }


    private fun doNextOperation() {
        println("BLE OPERATION DO NEXT")

        if (_pendingOperation.value != null) {
            println("BLE OPERATION DO NEXT ABOARD")
            return
        }

        _operationQueue.value.removeFirstOrNull()?.let { nextOperation ->
            _pendingOperation.value = nextOperation

            if (nextOperation == "THING"){
                //If this code give more time to succeed
                initiateJob(20L)
            }else{
                initiateJob()
            }

        }

    }


    private fun signalEndOfOperation(code : String?) {
        println("BLE OPERATION END -> ${_pendingOperation.value}")
        code?.let {



            if (_pendingOperation.value == code){
                cancelJob()
                _pendingOperation.value = null
                if (_operationQueue.value.isNotEmpty()) {
                    doNextOperation()
                }
            }
        }

    }


    private var taskJob: Job? = null

    private fun initiateJob(
        delayInSec : Long = 4L
    ) {
        cancelJob() // optional if you want to start afresh

        // Create a new Job and assign it to our variable
        taskJob = viewModelScope.launch {

            delay(delayInSec * 1000) // Task will be performed after the delay

            if (_pendingOperation.value == "PHCAL4" || _pendingOperation.value == "PHCAL9"){

                ultraTowerDevice?.let { ultraTower ->
                    ultraTower.dataUltraTower.value?.let { currentDataTower ->
                        if (currentDataTower.phRequestState is BluetoothRequestState.Pending){
                            ultraTower.updateDataTower(
                                data = currentDataTower.copy(
                                    phRequestState = BluetoothRequestState.Error,
                                )
                            )
                        }
                    }
                }


            }


            signalEndOfOperation(_pendingOperation.value)


        }
    }

    fun cancelJob() {
        taskJob?.cancel()
    }





    sealed class BLEStatus {
        data object WaitingForScan : BLEStatus()
        data object Scanning : BLEStatus()
        data class ScanningFailed(val message: CharSequence) : BLEStatus()
        data object Connected : BLEStatus()
        data object Disconnected : BLEStatus()
    }
}