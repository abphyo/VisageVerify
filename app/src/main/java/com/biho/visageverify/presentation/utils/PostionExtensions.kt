package com.biho.visageverify.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Rect
import android.net.Uri
import androidx.compose.ui.geometry.Size
import coil.request.ImageRequest
import com.google.mlkit.vision.face.Face
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun adjustPoint(point: PointF, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int): PointF {
    val x = point.x / imageWidth * screenWidth
    val y = point.y / imageHeight * screenHeight
    return PointF(x, y)
}

fun adjustSize(size: Size, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int): Size {
    val width = size.width / imageWidth * screenWidth
    val height = size.height / imageHeight * screenHeight
    return Size(width, height)
}

fun Bitmap.centerCrop(desiredWidth: Int, desiredHeight: Int): Bitmap {
    val xStart = (width - desiredWidth) / 2
    val yStart = (height - desiredHeight) / 2
    if (xStart < 0 || yStart < 0 || desiredWidth > width || desiredHeight > height) {
        throw IllegalArgumentException("Invalid arguments for center cropping")
    }
    return Bitmap.createBitmap(this, xStart, yStart, desiredWidth, desiredHeight)
}

fun Bitmap.cropBitmapRec(boundingBox: Rect): Bitmap {
    // Ensure the bounding box coordinates are within the bounds of the bitmap
    val left = boundingBox.left.coerceIn(0, width - 1)
    val top = boundingBox.top.coerceIn(0, height - 1)
    val right = boundingBox.right.coerceIn(0, width - 1)
    val bottom = boundingBox.bottom.coerceIn(0, height - 1)

    // Calculate the width and height of the cropped region
    val width = right - left
    val height = bottom - top

    // Create a new bitmap representing the cropped region
    return Bitmap.createBitmap(this, left, top, width, height)
}

fun List<Face>.biggestRect(): Rect {
    // Get only biggest rect from detected faces in frame
    return map {
        it.boundingBox
    }.maxBy {
        it.width() * it.height()
    }
}

fun Bitmap.rotateBitmap(rotationDegrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(rotationDegrees)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
