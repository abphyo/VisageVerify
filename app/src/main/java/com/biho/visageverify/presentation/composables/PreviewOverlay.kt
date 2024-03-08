package com.biho.visageverify.presentation.composables

import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.biho.visageverify.data.model.DetectFaceResult
import kotlin.math.max

@Composable
fun PreviewOverlay(
    resultBitmaps: List<DetectFaceResult>,
    viewWidth: Int,
    viewHeight: Int,
    modifier: Modifier
) {

    Canvas(modifier = modifier) {

        for (face in resultBitmaps) {

            val imageWidth = face.croppedBitmap.width
            val imageHeight = face.croppedBitmap.height

            // PreviewView is in FILL_START mode. So we need to scale up the bounding box to match with
            // the size that the captured images will be displayed.
            val scaleFactor = max(viewWidth * 1f / imageWidth, viewHeight * 1f / imageHeight)
            val boundingBox = face.boundingBox

            val top = boundingBox.top * scaleFactor
            val bottom = boundingBox.bottom * scaleFactor
            val left = boundingBox.left * scaleFactor
            val right = boundingBox.right * scaleFactor

            // Draw bounding box around detected objects
            val drawableRect = RectF(left, top, right, bottom)
            val paint = Paint().apply {
                color = Color.TRANSPARENT
                style = Paint.Style.STROKE
                strokeWidth = 5f
            }
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawRect(drawableRect, paint)
            }

        }
    }
}