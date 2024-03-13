package com.biho.visageverify.presentation.screens

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biho.visageverify.domain.usecases.SavePersonUseCase
import com.biho.visageverify.presentation.utils.cropBitmapRec
import kotlinx.coroutines.launch

class IntroduceViewModel(
    private val savePersonUseCase: SavePersonUseCase
) : ViewModel() {
    var screenState = mutableStateOf<DetectScreenState>(DetectScreenState.Idle)
        private set

    var loadingState = mutableStateOf(LoadingState.Idle)
        private set

    val croppedBitmap: MutableState<Bitmap?> =
        mutableStateOf(null)

    // get and feedback final cropped image before tfLite
    fun cropFaceFromFrame(frame: Bitmap, box: Rect) {
        val bitmap = frame.cropBitmapRec(box)
        croppedBitmap.value = bitmap
        screenState.value = DetectScreenState.Cropped
    }

    // feed to tfLite and save
    fun rememberFace(name: String) {
        loadingState.value = LoadingState.Loading
        viewModelScope.launch {
            if (croppedBitmap.value != null)
                savePersonUseCase.invoke(
                    croppedBitmap = croppedBitmap.value!!,
                    name = name
                ).onSuccess {
                    loadingState.value = LoadingState.Idle
                    screenState.value = DetectScreenState.Success
                    println("saved to Realm")
                }.onFailure {
                    screenState.value = DetectScreenState.Error(message = it.message!!)
                    loadingState.value = LoadingState.Idle
                    println("realm failed: ${it.message}")
                }
        }
    }

    fun clearCropState() {
        croppedBitmap.value = null
        screenState.value = DetectScreenState.Idle
    }

}