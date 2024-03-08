package com.biho.visageverify.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import com.biho.visageverify.data.model.DetectFaceResult
import com.biho.visageverify.domain.FaceDetectionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DetectViewModel(
    detectionUseCase: FaceDetectionUseCase
): ViewModel() {

    private val _resultBitmaps = MutableStateFlow<List<DetectFaceResult>>(emptyList())
    val resultBitmaps: StateFlow<List<DetectFaceResult>> = _resultBitmaps.asStateFlow()
    val cameraImageAnalyzer = CameraImageAnalyzer(
        detectFacePerFrame = detectionUseCase::detectFacePerFrame,
        onResult = { list ->
            Log.d("DEBUG", "detectedFace: $list")
            _resultBitmaps.update {
                list
            }
        },
        onResultNoFound = {
            /* return message on no result after a specific time */
        }
    )

    fun clearResult() {
        _resultBitmaps.update {
            emptyList()
        }
    }

}