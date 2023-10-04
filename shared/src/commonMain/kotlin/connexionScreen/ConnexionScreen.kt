package connexionScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.gleyco.UltraTower.SharedRes
import components.ButtonLoading
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.resources.compose.stringResource
import towerScreen.TowerScreen

@Composable
fun ConnexionScreen (
    isScanning : Boolean,
    onClickStartScanning : () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {

        Spacer(Modifier.size(32.dp))

        Text(
            text = stringResource(SharedRes.strings.welcome),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical =  32.dp, horizontal = 16.dp)
        )

        Text(
            text = stringResource(SharedRes.strings.frag_connection_descr),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .fillMaxWidth()
                .padding( horizontal = 16.dp)
        )

        Spacer(Modifier.weight(1f))

        ButtonLoading(
            text = stringResource(SharedRes.strings.frag_connection_btn_start_ble),
            shape = RoundedCornerShape(50),
            onClick = onClickStartScanning,
            isLoading = isScanning,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(30.dp)
        )
    }

}



