package towerScreen.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bluetooth.UltraTowerModel
import com.gleyco.UltraTower.SharedRes
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import towerScreen.TowerScreenViewModel
import towerScreen.components.MainTile


@OptIn(ExperimentalResourceApi::class)
@Composable
fun MainTowerScreen(
    towerData : State<UltraTowerModel?>,
    onNavigateTo : (TowerScreenViewModel.NavigationTower) -> Unit
) {
    val spaceTile = 16.dp


    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(Modifier.size(32.dp))

        MainTile(
            iconPainter = painterResource("ic_fog.xml"),
            color = Color(0xFFBFDDDB),
            onClick = {
                onNavigateTo(TowerScreenViewModel.NavigationTower.MisterScreen)
            },
            modifier = Modifier.fillMaxWidth(),
        ){

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = towerData.value?.statusMister?.let {
                        if (it){
                            stringResource(SharedRes.strings.mister_on)
                        }else{
                            stringResource(SharedRes.strings.mister_off)
                        }
                    } ?:  stringResource(SharedRes.strings.no_data),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(vertical = 32.dp)
                )

                Text(
                    text = "Misters",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

        }

        Spacer(Modifier.size(spaceTile))

        Row (

            Modifier.fillMaxWidth()

        ) {

            Column(

                Modifier.weight(1f)

            ) {

                MainTile(
                    iconPainter = painterResource("ic_ph.xml"),
                    color = Color(0xFFE6DFF1),
                    onClick = {
                        onNavigateTo(TowerScreenViewModel.NavigationTower.PHScreen)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ){

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text =   stringResource(SharedRes.strings.no_data),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(vertical = 32.dp)
                        )

                        Text(
                            text = "pH",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                }


                Spacer(Modifier.size(spaceTile))

                MainTile(
                    iconPainter = painterResource("ic_wifi.xml"),
                    color = Color(0xFFF1DFDF),
                    onClick = {
                        onNavigateTo(TowerScreenViewModel.NavigationTower.ThingSpeakScreen)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ){

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "ThingSpeak",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                }

            }

            Spacer(Modifier.size(spaceTile))


            Column (

                Modifier.weight(1f)

            ) {

                MainTile(
                    iconPainter = painterResource("ic_error.xml"),
                    color = Color(0xFFef9a9a),
                    onClick = {
                        //onNavigateTo(TowerScreenViewModel.NavigationTower.MisterScreen)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ){

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "No error",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                }

                Spacer(Modifier.size(spaceTile))

                MainTile(
                    iconPainter = painterResource("ic_ec.xml"),
                    color = Color(0xFFF2EEE8),
                    onClick = {
                       // onNavigateTo(TowerScreenViewModel.NavigationTower.MisterScreen)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ){

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text =   stringResource(SharedRes.strings.no_data),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(vertical = 32.dp)
                        )


                        Text(
                            text = "EC",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                }

            }
        }

        Spacer(Modifier.size(spaceTile))

        MainTile(
            iconPainter = painterResource("ic_thermostat.xml"),
            color = Color(0xFFD9DBD1),
            onClick = {
               // onNavigateTo(TowerScreenViewModel.NavigationTower.MisterScreen)
            },
            modifier = Modifier.fillMaxWidth(),
        ){

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text =   stringResource(SharedRes.strings.no_data),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(vertical = 32.dp)
                )

                Text(
                    text = "Temp is coming soon",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

        }

        Spacer(Modifier.size(spaceTile))

    }


}