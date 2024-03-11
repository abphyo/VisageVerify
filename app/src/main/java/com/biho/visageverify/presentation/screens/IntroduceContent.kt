package com.biho.visageverify.presentation.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.biho.visageverify.presentation.composables.DrawFaceOverLay
import com.biho.visageverify.presentation.utils.biggestRect
import com.google.mlkit.vision.face.Face

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun IntroduceContent(
    paddingValues: PaddingValues,
    screenState: DetectScreenState,
    loadingState: LoadingState,
    croppedFace: Bitmap,
    faces: List<Face>,
    imageWidth: Int,
    imageHeight: Int,
    onCropImage: (Rect) -> Unit,
    onRememberImage: (String) -> Unit,
    controller: LifecycleCameraController
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var name by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }

    val nameTextFieldEmpty = rememberSaveable {
        mutableStateOf(false)
    }

    val rememberButtonEnabled = remember(key1 = name) {
        derivedStateOf {
            name.text.isNotBlank()
        }
    }

    val cropButtonEnabled = remember(key1 = faces) {
        derivedStateOf {
            faces.isNotEmpty()
        }
    }

    fun validateTextField(text: String) {
        nameTextFieldEmpty.value = text.isEmpty()
    }

    fun onNameChanged(textFieldValue: TextFieldValue) {
        name = textFieldValue
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
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
        DrawFaceOverLay(
            faces = faces,
            imageHeight = imageHeight,
            imageWidth = imageWidth
        )

        AnimatedVisibility(visible = screenState == DetectScreenState.Cropped) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .graphicsLayer { alpha = 0.95f },
                contentAlignment = Alignment.Center
            ) {
                RememberCard(
                    croppedFace = croppedFace,
                    name = name,
                    nameTextFieldEmpty = nameTextFieldEmpty.value,
                    onNameChanged = ::onNameChanged,
                    validateTextField = ::validateTextField
                )
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
                    enabled = rememberButtonEnabled.value,
                    onClick = { onRememberImage(name.text) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeDefaults.Large
                ) {
                    when (loadingState) {
                        LoadingState.Loading -> CircularProgressIndicator()
                        LoadingState.Idle -> Text(text = "Remember")
                    }
                }
            }
            AnimatedVisibility(visible = screenState == DetectScreenState.Idle) {
                Button(
                    enabled = cropButtonEnabled.value,
                    onClick = { onCropImage(faces.biggestRect()) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeDefaults.Large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    Text(text = "Crop")
                }
            }
        }
    }
}

@Composable
fun RememberCard(
    croppedFace: Bitmap,
    name: TextFieldValue,
    nameTextFieldEmpty: Boolean,
    onNameChanged: (TextFieldValue) -> Unit,
    validateTextField: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                horizontal = 32.dp,
                vertical = 48.dp
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                bitmap = croppedFace.asImageBitmap(),
                contentDescription = "cropped bitmap",
                modifier = Modifier
                    .width(350.dp)
                    .clip(shape = ShapeDefaults.Medium),
                contentScale = ContentScale.FillWidth
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = MaterialTheme.shapes.medium,
                placeholder = {
                    Text(text = "Fill person's name", color = Color.Gray)
                },
                value = name,
                onValueChange = {
                    onNameChanged(it)
                    validateTextField(it.text)
                },
                singleLine = true,
                supportingText = {
                    if (nameTextFieldEmpty)
                        Text(
                            text = "name can't be empty",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth()
                        )
                },
            )
        }
    }
}
