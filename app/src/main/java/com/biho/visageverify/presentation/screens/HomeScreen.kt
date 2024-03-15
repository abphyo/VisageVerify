package com.biho.visageverify.presentation.screens

<<<<<<< HEAD
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
=======
import androidx.camera.core.Preview
import androidx.camera.view.CameraController
>>>>>>> parent of 04e77e9 (Fix:/ handled detect screen back presses)
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
<<<<<<< HEAD
import com.biho.visageverify.R
=======
import com.biho.visageverify.presentation.composables.DrawFaceOverLay
>>>>>>> parent of 04e77e9 (Fix:/ handled detect screen back presses)
import com.biho.visageverify.presentation.composables.DrawNameOverLay
import com.biho.visageverify.presentation.navigation.BackOnlyTopAppBar
import com.google.mlkit.vision.face.Face

@Composable
fun HomeScreen(
    isRouteFirstEntry: Boolean = true,
    onNavigateIntroduce: () -> Unit,
    onNavigateBack: () -> Unit,
    persons: List<Pair<String?, Face>>,
    imageWidth: Int,
    imageHeight: Int,
    cameraController: LifecycleCameraController,
    onPickImageUri: (Uri?) -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current

<<<<<<< HEAD
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            onPickImageUri(it)
        }
    )

=======
>>>>>>> parent of 04e77e9 (Fix:/ handled detect screen back presses)
    Scaffold(
        topBar = {
            BackOnlyTopAppBar(
                isRouteFirstEntry = isRouteFirstEntry,
                onNavigateBack = onNavigateBack,
                text = "Hello"
            )
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
<<<<<<< HEAD
                IconButton(
                    onClick = {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_a_photo_24px),
                        contentDescription = null
                    )
=======
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
>>>>>>> parent of 04e77e9 (Fix:/ handled detect screen back presses)
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