package com.biho.visageverify.presentation

import androidx.lifecycle.ViewModel
import com.biho.visageverify.domain.FaceDetectionUseCase

class DetectViewModel(
    private val detectionUseCase: FaceDetectionUseCase
): ViewModel() {

    val detectFacesPerFrame = detectionUseCase::detectFacePerFrame

}