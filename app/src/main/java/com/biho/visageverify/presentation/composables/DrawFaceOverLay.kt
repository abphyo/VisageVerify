package com.biho.visageverify.presentation.composables

import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeRect
import com.biho.visageverify.presentation.utils.adjustPoint
import com.biho.visageverify.presentation.utils.adjustSize
import com.google.mlkit.vision.face.Face

@Composable
fun DrawFaceOverLay(faces: List<Face>, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        faces.forEach { face ->
            val boundingBox = face.boundingBox.toComposeRect()
            val topLeft = adjustPoint(PointF(boundingBox.topLeft.x, boundingBox.topLeft.y), imageWidth, imageHeight, screenWidth, screenHeight)
            val size = adjustSize(boundingBox.size, imageWidth, imageHeight, screenWidth, screenHeight)
            drawRect(
                color = Color.Yellow,
                size = size,
                topLeft = Offset(topLeft.x, topLeft.y),
                style = Stroke(width = 5f)
            )
        }
    }
}