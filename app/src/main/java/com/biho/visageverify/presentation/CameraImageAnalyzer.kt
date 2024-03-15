package com.biho.visageverify.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.biho.visageverify.presentation.utils.rotateBitmap
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.face.Face
import kotlin.math.log

@OptIn(ExperimentalGetImage::class)
class CameraImageAnalyzer(
    private val detectFacePerFrame: (Bitmap, Int) -> Task<MutableList<Face>>,
    private val onFaceDetected: (result: List<Face>, frame: Bitmap, width: Int, height: Int) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        // Limit detection per n frames
        image.image?.let {
            val bitmap = image.toBitmap()
//            val croppedBitmap = bitmap.centerCrop(300, 400)
            val rotationDegrees = image.imageInfo.rotationDegrees
            val trueBitmap = bitmap.rotateBitmap(rotationDegrees.toFloat())
            detectFacePerFrame(bitmap, rotationDegrees)
                .addOnSuccessListener { faces ->
                    onFaceDetected(faces, trueBitmap, trueBitmap.width, trueBitmap.height)
                }
                .addOnCompleteListener {
                    image.image?.close()
                    image.close()
                }
        }
    }
}

class GalleryImageAnalyzer(
    private val detectFaceFromPhoto: (Bitmap, Int) -> Task<MutableList<Face>>,
    private val onFaceDetected: (result: List<Face>, photo: Bitmap) -> Unit
) {
    fun analyze(photo: Bitmap) {
        Log.d("DEBUG", "analyze: $photo")
        photo.let {
            detectFaceFromPhoto(it, 90)
                .addOnSuccessListener { faces ->
                    Log.d("DEBUG", "analyze: $faces")
                    onFaceDetected(faces, photo)
                }
        }
    }
}
