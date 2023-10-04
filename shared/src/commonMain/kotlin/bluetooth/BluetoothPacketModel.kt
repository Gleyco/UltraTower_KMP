package bluetooth
import kotlinx.serialization.Serializable

@Serializable
data class BluetoothPacketModel(
    val CMD: String,
    val DATA: Boolean? = null,

    val DATA_FREQ : Int? = null,

    val DATA_ON : Int? = null,
    val DATA_OFF : Int? = null,

    val DATA_AMB : String? = null,
    val DATA_ROOT : String? = null,
    val DATA_TANK : String? = null,

    val DATA_ENA : String? = null,
    val TIME : String? = null,
    val SSID : String? = null,
    val PW : String? = null,
    val APIKEY : String? = null,
    val CHANNEL : String? = null,

    val DATA4 : String? = null,
    val DATA9 : String? = null,

)





