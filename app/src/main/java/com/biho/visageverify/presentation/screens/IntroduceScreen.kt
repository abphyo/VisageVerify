package com.biho.visageverify.presentation.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.biho.visageverify.presentation.navigation.BackOnlyTopAppBar

@Composable
fun IntroduceScreen(
    isRouteFirstEntry: Boolean,
    onNavigateBack: () -> Unit,
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BackOnlyTopAppBar(
                isRouteFirstEntry = isRouteFirstEntry,
                onNavigateBack = { onNavigateBack() },
                text = title
            )
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}