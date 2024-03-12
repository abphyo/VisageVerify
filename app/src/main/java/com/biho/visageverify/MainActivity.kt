package com.biho.visageverify

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.biho.visageverify.presentation.navigation.MainNavGraph
import com.biho.visageverify.presentation.composables.CameraPermissionTextProvider
import com.biho.visageverify.presentation.composables.PermissionDialog
import com.biho.visageverify.presentation.utils.LocalApplicationContext
import com.biho.visageverify.presentation.utils.LocalPermissionChannel
import com.biho.visageverify.presentation.utils.LocalPermissionGrantedChannel
import com.biho.visageverify.ui.theme.VisageVerifyTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext
import java.util.UUID

val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

class MainActivity : ComponentActivity() {

    private val permissionChannel = Channel<String>()
    private val permissionGrantedChannel = Channel<String>()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                lifecycleScope.launch {
                    permissionGrantedChannel.send(UUID.randomUUID().toString())
                }
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                lifecycleScope.launch {
                    permissionChannel.send(UUID.randomUUID().toString())
                }
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            when {
                !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ->
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ->
                    lifecycleScope.launch {
                        permissionGrantedChannel.send(UUID.randomUUID().toString())
                    }
            }
        }
        setContent {
            KoinContext {
                VisageVerifyTheme {
                    CompositionLocalProvider(
                        values = arrayOf(
                            LocalPermissionChannel provides permissionChannel,
                            LocalPermissionGrantedChannel provides permissionGrantedChannel,
                            LocalApplicationContext provides applicationContext
                        )
                    ) {
                        val viewModel = koinViewModel<MainViewModel>()

                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val navHostController = rememberNavController()
                            MainNavGraph(navController = navHostController)
                        }
                        LaunchedEffect(key1 = permissionChannel) {
                            permissionChannel.consumeEach {
                                viewModel.showDialog()
                            }
                        }
                        if (viewModel.permissionDialogState)
                            PermissionDialog(
                                permissionTextProvider = CameraPermissionTextProvider(),
                                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                    Manifest.permission.CAMERA
                                ),
                                onDismiss = viewModel::dismissDialog,
                                onOkClick = {
                                    viewModel.dismissDialog()
                                    requestPermissionLauncher.launch(
                                        Manifest.permission.CAMERA
                                    )
                                },
                                onGoToAppSettingsClick = {
                                    viewModel.dismissDialog()
                                    openAppSettings()
                                }
                            )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.CAMERA
            ) -> lifecycleScope.launch {
                permissionGrantedChannel.send(UUID.randomUUID().toString())
            }
            else -> { }
        }
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VisageVerifyTheme {
        Greeting("Android")
    }
}