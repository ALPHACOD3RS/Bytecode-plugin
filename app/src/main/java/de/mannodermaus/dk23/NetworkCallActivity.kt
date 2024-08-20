package de.mannodermaus.dk23

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.mannodermaus.dk23.service.ApiService

class NetworkCallActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NetworkCallScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkCallScreen() {
    var responseText by remember { mutableStateOf("API Response will appear here") }
    var urlText by remember { mutableStateOf("URL will appear here") }
    var headersText by remember { mutableStateOf("Headers will appear here") }

    fun makeApiCall() {
        ApiService.makeApiCall(object : ApiService.ApiCallback {
            override fun onSuccess(response: String, headers: String) {
                responseText = response
                headersText = headers
            }

            override fun onFailure(errorMessage: String) {
                responseText = "Failed to make API call: $errorMessage"
                headersText = "No headers available"
            }
        })
    }

    LaunchedEffect(Unit) {
        makeApiCall()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Network Call") }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = urlText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = headersText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = responseText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun NetworkCallScreenPreview() {
    NetworkCallScreen()
}
