package com.biho.visageverify.presentation.screens

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.biho.visageverify.presentation.composables.DrawFaceOverLay
import com.biho.visageverify.presentation.navigation.BackOnlyTopAppBar
import com.biho.visageverify.presentation.utils.biggestRect
import com.google.mlkit.vision.face.Face

@Composable
fun DetectionScreen(
    screenState: DetectScreenState,
    loadingState: LoadingState,
    croppedFace: Bitmap,
    faces: List<Face>,
    imageWidth: Int,
    imageHeight: Int,
    isRouteFirstEntry: Boolean,
    onNavigateBack: () -> Unit,
    onCropImage: (Rect) -> Unit,
    onRememberImage: (String) -> Unit,
    controller: LifecycleCameraController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val screenWidth by remember { mutableIntStateOf(context.resources.displayMetrics.widthPixels) }
    val screenHeight by remember { mutableIntStateOf(context.resources.displayMetrics.heightPixels) }

    val isButtonEnabled = rememberSaveable {
        mutableStateOf(false)
    }

    val name = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }

    val nameTextFieldEmpty = rememberSaveable {
        mutableStateOf(false)
    }

    fun validateTextField(text: String) {
        nameTextFieldEmpty.value = text.isEmpty()
    }

    LaunchedEffect(key1 = screenState == DetectScreenState.Success) {
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            BackOnlyTopAppBar(
                isRouteFirstEntry = isRouteFirstEntry,
                onNavigateBack = onNavigateBack,
                text = "Introduce me someone"
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(visible = screenState == DetectScreenState.Idle) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        PreviewView(it).apply {
                            this.controller = controller
                            controller.bindToLifecycle(lifecycleOwner)
                        }
                    }
                )
                DrawFaceOverLay(faces = faces, imageHeight, imageWidth, screenWidth, screenHeight)
            }
            AnimatedVisibility(visible = screenState == DetectScreenState.Cropped) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(alpha = 0.8f),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 300.dp, horizontal = 100.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                bitmap = croppedFace.asImageBitmap(),
                                contentDescription = "cropped bitmap",
                                modifier = Modifier.width(350.dp),
                                contentScale = ContentScale.FillWidth
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                shape = MaterialTheme.shapes.medium,
                                placeholder = {
                                    Text(text = "Fill person's name", color = Color.Gray)
                                },
                                value = name.value,
                                onValueChange = {
                                    name.value = it
                                    validateTextField(it.text)
                                },
                                supportingText = {
                                    if (nameTextFieldEmpty.value)
                                        Text(
                                            text = "name can't be empty",
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                },
                            )
                            AnimatedVisibility(visible = loadingState == LoadingState.Loading) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
                AnimatedVisibility(visible = screenState == DetectScreenState.Cropped) {
                    Button(
                        enabled = !nameTextFieldEmpty.value,
                        onClick = { onRememberImage(name.value.text) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = ShapeDefaults.Medium
                    ) {
                        Text(text = "Remember")
                    }
                }
                AnimatedVisibility(visible = screenState == DetectScreenState.Idle) {
                    Button(
                        enabled = isButtonEnabled.value,
                        onClick = { onCropImage(faces.biggestRect()) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = ShapeDefaults.Medium
                    ) {
                        Text(text = "Crop")
                    }
                }
            }
        }
    }
}