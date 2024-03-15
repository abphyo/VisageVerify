package com.biho.visageverify.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Preview
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.biho.visageverify.R
import com.biho.visageverify.presentation.composables.DrawFaceOverLay
import com.biho.visageverify.presentation.composables.DrawNameOverLay
import com.biho.visageverify.presentation.navigation.BackOnlyTopAppBar
import com.biho.visageverify.presentation.utils.biggestRect
import com.google.mlkit.vision.face.Face

@Composable
fun HomeScreen(
    isRouteFirstEntry: Boolean = true,
    onNavigateIntroduce: () -> Unit,
    onNavigateBack: () -> Unit,
    persons: List<Pair<String?, Face>>,
    imageWidth: Int,
    imageHeight: Int,
    cameraController: LifecycleCameraController
) {
    val lifecycle = LocalLifecycleOwner.current

    val pickedImage = remember {
        mutableStateOf<Uri?>(value = null)
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->

        }
    )

    Scaffold(
        topBar = {
            BackOnlyTopAppBar(isRouteFirstEntry = isRouteFirstEntry, onNavigateBack = onNavigateBack, text = "Hello")
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.BottomCenter,
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    PreviewView(it).apply {
                        controller = cameraController
                        cameraController.bindToLifecycle(lifecycle)
                    }
                }
            )
            DrawNameOverLay(pairs = persons, imageWidth = imageWidth, imageHeight = imageHeight)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painter = painterResource(id = R.drawable.add_a_photo_24px), contentDescription = null)
                }
                Button(
                    onClick = { onNavigateIntroduce() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeDefaults.ExtraLarge
                ) {
                    Text(text = "Introduce someone")
                }
            }
        }
    }

}