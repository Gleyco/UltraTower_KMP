package connexionScreen


sealed interface ConnexionEvent  {


    data object StartDiscoverDevices : ConnexionEvent

    data object ClearStatus : ConnexionEvent

}