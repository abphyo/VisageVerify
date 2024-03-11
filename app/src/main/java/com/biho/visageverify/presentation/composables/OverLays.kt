package com.biho.visageverify.presentation.composables

import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import com.biho.visageverify.presentation.utils.adjustPoint
import com.biho.visageverify.presentation.utils.adjustSize
import com.google.mlkit.vision.face.Face

@Composable
fun DrawFaceOverLay(faces: List<Face>, imageWidth: Int, imageHeight: Int) {
    val context = LocalContext.current
    val screenWidth by remember { mutableIntStateOf(context.resources.displayMetrics.widthPixels) }
    val screenHeight by remember { mutableIntStateOf(context.resources.displayMetrics.heightPixels) }
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

@Composable
fun DrawNameOverLay(pairs: List<Pair<String?, Face>>, imageWidth: Int, imageHeight: Int) {
    val context = LocalContext.current
    val textMeasurer = rememberTextMeasurer()
    val screenWidth by remember { mutableIntStateOf(context.resources.displayMetrics.widthPixels) }
    val screenHeight by remember { mutableIntStateOf(context.resources.displayMetrics.heightPixels) }
    Canvas(modifier = Modifier.fillMaxSize()) {
        pairs.forEach { pair ->
            if (!pair.first.isNullOrEmpty()) {
                val boundingBox = pair.second.boundingBox.toComposeRect()
                val topLeft = adjustPoint(PointF(boundingBox.topLeft.x, boundingBox.topLeft.y), imageWidth, imageHeight, screenWidth, screenHeight)
                val size = adjustSize(boundingBox.size, imageWidth, imageHeight, screenWidth, screenHeight)

                drawText(
                    textMeasurer = textMeasurer,
                    text = pair.first!!,
                    size = size,
                    style = TextStyle(
                        color = Color.Yellow
                    ),
                    topLeft = Offset(topLeft.x, topLeft.y),
                )
            }
        }
    }
}