package com.biho.visageverify.presentation.screens

import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.biho.visageverify.data.model.DetectFaceResult
import com.biho.visageverify.presentation.composables.CameraPreview
import com.biho.visageverify.presentation.composables.PreviewOverlay
import com.biho.visageverify.presentation.navigation.BackOnlyTopAppBar

@Composable
fun CameraScreen(
    resultBitmaps: List<DetectFaceResult>,
    isRouteFirstEntry: Boolean,
    onNavigateBack: () -> Unit,
    controller: LifecycleCameraController
) {
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
            CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())
            PreviewOverlay(
                resultBitmaps = resultBitmaps,
                viewWidth = cameraWidth,
                viewHeight = cameraHeight,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}