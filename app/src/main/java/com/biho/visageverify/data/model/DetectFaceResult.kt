package com.biho.visageverify.data.model

import android.graphics.Bitmap
import android.graphics.RectF

data class DetectFaceResult(
    val croppedBitmap: Bitmap,
    val boundingBox: RectF
)