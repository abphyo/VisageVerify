package com.biho.visageverify.presentation

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.face.Face

@OptIn(ExperimentalGetImage::class)
class CameraImageAnalyzer(
    private val detectFacePerFrame: (Bitmap, Int) -> Task<MutableList<Face>>,
    private val onFaceDetected: (result: List<Face>, width: Int, height: Int) -> Unit
) : ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0

    override fun analyze(image: ImageProxy) {
        // Limit detection per n frames
        if (frameSkipCounter % 60 == 0)
            image.image.let {
                val bitmap = image.toBitmap()
                val rotationDegrees = image.imageInfo.rotationDegrees
                detectFacePerFrame(bitmap, rotationDegrees).addOnCompleteListener { faces ->
                    onFaceDetected(faces.result, image.width, image.height)
                    image.image?.close()
                    image.close()
                }
            }
        frameSkipCounter++
    }
}
