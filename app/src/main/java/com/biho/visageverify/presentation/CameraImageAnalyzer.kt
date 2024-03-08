package com.biho.visageverify.presentation

import android.graphics.Bitmap
import android.util.Log
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

    override fun analyze(image: ImageProxy) {
        // Limit detection per n frames
        image.image?.let {
            val bitmap = image.toBitmap()
            val rotationDegrees = image.imageInfo.rotationDegrees
            detectFacePerFrame(bitmap, rotationDegrees).addOnSuccessListener { faces ->
                Log.d("DEBUG", "analyze: $faces")
                onFaceDetected(faces, image.width, image.height)
            }.addOnCompleteListener {
                image.close()
            }
        }
    }
}
