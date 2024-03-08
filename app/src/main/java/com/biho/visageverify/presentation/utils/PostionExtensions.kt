package com.biho.visageverify.presentation.utils

import android.graphics.Bitmap
import android.graphics.PointF
import androidx.compose.ui.geometry.Size

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