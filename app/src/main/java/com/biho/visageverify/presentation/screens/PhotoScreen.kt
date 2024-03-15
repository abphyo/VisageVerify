package com.biho.visageverify.presentation.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import com.biho.visageverify.presentation.composables.DrawFaceOverLay
import com.biho.visageverify.presentation.composables.DrawNameOverLay
import com.biho.visageverify.presentation.navigation.BackOnlyTopAppBar
import com.google.mlkit.vision.face.Face

@Composable
fun PhotoScreen(
    isRouteFirstEntry: Boolean,
    onNavigateBack: () -> Unit,
    persons: List<Pair<String?, Face>>,
    image: Bitmap?
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BackOnlyTopAppBar(
                isRouteFirstEntry = isRouteFirstEntry,
                onNavigateBack = onNavigateBack,
                text = ""
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(visible = image != null) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(model = image!!, contentDescription = null)
                    DrawFaceOverLay(
                        faces = persons.map { it.second },
                        imageWidth = image.width,
                        imageHeight = image.height
                    )
                    DrawNameOverLay(
                        pairs = persons,
                        imageWidth = image.width,
                        imageHeight = image.height
                    )
                }
            }
        }
    }
}