package com.biho.visageverify.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.graphics.toRectF
import com.biho.visageverify.data.model.DetectFaceResult
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.face.Face

class CameraImageAnalyzer(
    private val detectFacePerFrame: (Bitmap, Int) -> Task<MutableList<Face>>,
    private val onResult: (List<DetectFaceResult>) -> Unit,
    private val onResultNoFound: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0

    private val croppedImagesPerFrame: MutableList<DetectFaceResult> = mutableListOf()

    override fun analyze(image: ImageProxy) {

        // Limit detection per n frames
        if (frameSkipCounter % 60 == 0) {
            val bitmap = image.toBitmap()
            Log.d("DEBUG", "capturing: $bitmap")
            val rotationDegrees = image.imageInfo.rotationDegrees
            detectFacePerFrame(bitmap, rotationDegrees).addOnSuccessListener { faces ->
                // Continue processing here,
                // including Cropping image from BoundingBox (which available inside `image` Object above)
                for (face in faces) {
                    // Get the bounding box of the face
                    val boundingBox = face.boundingBox

                    // Crop the face from the original image
                    val croppedBitmap = Bitmap.createBitmap(
                        bitmap,
                        boundingBox.left,
                        boundingBox.top,
                        boundingBox.width(),
                        boundingBox.height()
                    )

                    // Convert to RecF
                    val boundingBoxF = boundingBox.toRectF()

                    Log.d("DEBUG", "croppedBitmap: $croppedBitmap")
                    croppedImagesPerFrame.add(
                        DetectFaceResult(
                            croppedBitmap = croppedBitmap,
                            boundingBox = boundingBoxF
                        )
                    )
                }
            }
            Log.d("DEBUG", "detected-faces: ${croppedImagesPerFrame.map { it.croppedBitmap }}")
            onResult(croppedImagesPerFrame)
        }
        frameSkipCounter++
        image.close()
    }
}

fun Bitmap.centerCrop(desiredWidth: Int, desiredHeight: Int): Bitmap {
    val xStart = (width - desiredWidth) / 2
    val yStart = (height - desiredHeight) / 2

    if (xStart < 0 || yStart < 0 || desiredWidth > width || desiredHeight > height) {
        throw IllegalArgumentException("Invalid arguments for center cropping")
    }

    return Bitmap.createBitmap(this, xStart, yStart, desiredWidth, desiredHeight)
}