package com.andr.zahar2.blitzcrikmanager.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andr.zahar2.blitzcrikmanager.ui.second.SecondActivity
import com.andr.zahar2.blitzcrikmanager.ui.theme.BlitzCrikManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*try {
            val mNotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!mNotificationManager.isNotificationPolicyAccessGranted) {
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                startActivity(intent)
            }
        } catch (e: Exception) { }*/

        setContent {

            val viewModel = hiltViewModel<MainActivityViewModel>()

            var host by remember { mutableStateOf(viewModel.host) }
            var port by remember { mutableStateOf(viewModel.port.toString()) }

            Content(
                host = host,
                port = port,
                hostChange = { host = it },
                portChange = { port = it }
            ) {
                viewModel.onButtonClick(host, port) {
                    startActivity(Intent(this, SecondActivity::class.java))
                }
            }
        }
    }
}

@Composable
private fun Content(
    host: String,
    port: String,
    hostChange: (String) -> Unit,
    portChange: (String) -> Unit,
    onClick: () -> Unit
) {
    BlitzCrikManagerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row {
                    TextField(
                        value = host,
                        onValueChange = hostChange,
                        label = { Text("Хост") },
                        modifier = Modifier
                            .width(200.dp)
                            .focusable()
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    TextField(
                        value = port,
                        onValueChange = portChange,
                        label = { Text("Порт") },
                        modifier = Modifier.focusable()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusable()
                        .focusTarget()
                ) {
                    Text("Далее")
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.TABLET
)
@Composable
fun DefaultPreview() {
        Content(
            host = "192.168.10.89",
            port = "2207",
            hostChange = { },
            portChange = { }
        ) {

        }
}