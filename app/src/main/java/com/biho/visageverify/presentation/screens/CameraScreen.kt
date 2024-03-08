package com.biho.visageverify.presentation.screens

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.biho.visageverify.presentation.composables.DrawFaceOverLay
import com.biho.visageverify.presentation.navigation.BackOnlyTopAppBar
import com.google.mlkit.vision.face.Face

@Composable
fun CameraScreen(
    faces: List<Face>,
    imageWidth: Int,
    imageHeight: Int,
    isRouteFirstEntry: Boolean,
    onNavigateBack: () -> Unit,
    controller: LifecycleCameraController
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var cameraWidth by remember { mutableIntStateOf(0) }
    var cameraHeight by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            BackOnlyTopAppBar(
                isRouteFirstEntry = isRouteFirstEntry,
                onNavigateBack = onNavigateBack,
                text = "Introduce me someone"
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                cameraWidth = size.width
                cameraHeight = size.height
            }
            .padding(paddingValues),
            contentAlignment = Alignment.BottomCenter
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    PreviewView(it).apply {
                        this.controller = controller
                        controller.bindToLifecycle(lifecycleOwner)
                    }
                }
            )
            DrawFaceOverLay(faces = faces, imageHeight, imageWidth, cameraWidth, cameraHeight)
        }
    }
}