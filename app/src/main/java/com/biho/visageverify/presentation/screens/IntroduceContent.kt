package com.biho.visageverify.presentation.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.biho.visageverify.presentation.utils.BackHandler
import com.biho.visageverify.presentation.composables.DrawFaceOverLay
import com.biho.visageverify.presentation.composables.ErrorCard
import com.biho.visageverify.presentation.composables.RememberCard
import com.biho.visageverify.presentation.composables.SuccessCard
import com.biho.visageverify.presentation.utils.biggestRect
import com.google.mlkit.vision.face.Face

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun IntroduceContent(
    paddingValues: PaddingValues,
    screenState: DetectScreenState,
    loadingState: LoadingState,
    croppedFace: Bitmap?,
    faces: List<Face>,
    imageWidth: Int,
    imageHeight: Int,
    onCropImage: (Rect) -> Unit,
    onRememberImage: (String) -> Unit,
    onClearClick: () -> Unit,
    onFinish: () -> Unit,
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

    BackHandler(isEnabled = true) {
        if (screenState == DetectScreenState.Cropped) onClearClick()
        else onFinish()
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn {
                item {
                    AnimatedContent(
                        targetState = screenState, label = "",
                        transitionSpec = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = EaseIn
                                )
                            ).togetherWith(
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = EaseIn
                                    )
                                )
                            )
                        }
                    ) { state ->
                        when (state) {
                            is DetectScreenState.Cropped -> {
                                RememberCard(
                                    croppedFace = croppedFace,
                                    name = name,
                                    nameTextFieldEmpty = nameTextFieldEmpty.value,
                                    onNameChanged = ::onNameChanged,
                                    validateTextField = ::validateTextField,
                                    onRememberImage = onRememberImage,
                                    rememberButtonEnabled = rememberButtonEnabled.value,
                                    loadingState = loadingState,
                                    onClearClick = onClearClick
                                )
                            }

                            is DetectScreenState.Success -> {
                                SuccessCard(
                                    description = {
                                        Text(
                                            text = "${name.text} is remembered.",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    },
                                    onDoneClick = onFinish
                                )
                            }

                            is DetectScreenState.Error -> {
                                ErrorCard(
                                    message = {
                                        Text(
                                            text = "recognization failed: ${state.message}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    },
                                    onDoneClick = onFinish
                                )
                            }

                            else -> {}
                        }
                    }
                }
            }
        }

        AnimatedVisibility(visible = screenState == DetectScreenState.Idle) {
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
                Button(
                    enabled = cropButtonEnabled.value,
                    onClick = { onCropImage(faces.biggestRect()) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeDefaults.ExtraLarge,
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
