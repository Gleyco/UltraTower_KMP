package bluetooth

import com.juul.kable.Peripheral
import com.juul.kable.WriteType
import com.juul.kable.characteristicOf
import domain.BluetoothRequestState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class UltraTower (
    private val peripheral: Peripheral
) : Peripheral by peripheral {

    val dataUltraTower : MutableStateFlow<UltraTowerModel?> = MutableStateFlow(null)

    private val _rxCharacteristic = characteristicOf(
        service = UUID_SERVICE,
        characteristic = UUID_JSON_CHAR,
    )

    private val _txCharacteristic = characteristicOf(
        service = UUID_SERVICE,
        characteristic = UUID_TX,
    )


    @OptIn(ExperimentalStdlibApi::class)
    suspend fun readJson(): String {
        val value = peripheral.read(_rxCharacteristic)
        println( "json → read → value = ${value.toHexString()}" )

        val str = value.decodeToString()
        println( "json → read → value = $str")
        dataUltraTower.value = Json.decodeFromString(str)
        println( "json → read → value = $str")
        println( "json → read → value = ${value.toHexString()}" )
        return ""
    }


    suspend fun writeToTower(data: String) {
        println( "Writing to tower with data : $data" )
        peripheral.write(_rxCharacteristic, data.encodeToByteArray(), WriteType.WithResponse)
        println( "Writing to tower complete" )
    }


    fun updateDataTower(data: UltraTowerModel) {
        dataUltraTower.value = data
    }


    val dataTower : Flow<String> = peripheral
        .observe(_txCharacteristic)
        .map {
            it.decodeToString()
        }





    companion object{
        const val UUID_SERVICE = "367fa97d-b107-49e0-9503-3409d2c247c9"
        const val UUID_JSON_CHAR = "367fa97d-b108-49e0-9503-3409d2c247c9"
        const val UUID_TX =  "367fa97d-b109-49e0-9503-3409d2c247c9"
    }
}


@Serializable
data class UltraTowerModel(

    @SerialName("MENA")
    val mistEnable : Boolean,

    @SerialName("MFREQ")
    val mistFrequency : Int,


    @SerialName("MON")
    val timeCycleOnMist : Int,

    @SerialName("MOFF")
    val timeCycleOffMist : Int,


    @SerialName("PHENA")
    val phEnable : Boolean,

    @SerialName("PHCAL")
    val phCalib : Boolean,

  //  @SerialName("PHCALIB4")
  //  val ph4 : Int,

  //  @SerialName("PHCALIB9")
  //  val ph9 : Int,

    @SerialName("TCELSIUS")
    val isCelsius : Boolean,

    @SerialName("TTANKENA")
    val tempTankEnable : Boolean,

    @SerialName("TROOTENA")
    val tempRootEnable : Boolean,

    @SerialName("TAMBENA")
    val tempAmbEnable : Boolean,


    @SerialName("TROOTOFFSET")
    val offsetRootTemp : Float,

    @SerialName("TTANKOFFSET")
    val offsetTankTemp : Float,

    @SerialName("TAMBOFFSET")
    val offsetAmbTemp : Float,


  //  @SerialName("ADRESSTANK")
  //  val addressTankTemp : Byte,

   // @SerialName("ADRESSROOT")
   // val addressRootTemp : Byte,


    @SerialName("THINGENA")
    val thingSpeakEnable : Boolean,

    @SerialName("SSID")
    val ssid : String,

    @SerialName("THINGAPIKEY")
    val apiKeyThingSpeak : String,

    @SerialName("THINGCHANNEL")
    val channelThingSpeak : Long,

    @SerialName("THINGTIME")
    val intervalTimeThingSpeak : Int,


    @SerialName("ECENA")
    val ecEnable : Boolean,

    @SerialName("ECCAL")
    val ecCalib : Boolean,

    @SerialName("V")
    val version : Double,



    //Changing property
    val statusMister : Boolean? = null,
    val phData : String? = null,

    val wifiPassWord : String = "",

    val phRequestState : BluetoothRequestState? = null,
    val thingSpeakRequestState : BluetoothRequestState? = null



)
