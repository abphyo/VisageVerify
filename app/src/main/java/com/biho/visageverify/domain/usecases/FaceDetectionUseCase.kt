package com.biho.visageverify.domain.usecases

import android.graphics.Bitmap
import androidx.datastore.core.DataStore
import com.biho.visageverify.data.TfLiteInterpreter
import com.biho.visageverify.data.model.TfLitePreferences
import com.biho.visageverify.data.utils.bitmapToNV21ByteArray
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

class FaceDetectionUseCase(
    private val faceDetector: FaceDetector
) {

    fun detectFacePerFrame(bitmap: Bitmap, rotationDegrees: Int): Task<MutableList<Face>> {
        // Pretreatment before processing with google ml kit
        val image = InputImage.fromByteArray(
            bitmap.bitmapToNV21ByteArray(),
            bitmap.width,
            bitmap.height,
            rotationDegrees,
            InputImage.IMAGE_FORMAT_NV21
        )
        return faceDetector.process(image)
    }

}