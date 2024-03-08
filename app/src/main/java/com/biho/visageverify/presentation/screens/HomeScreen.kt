package com.biho.visageverify.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.biho.visageverify.presentation.navigation.BackOnlyTopAppBar

@Composable
fun HomeScreen(
    isRouteFirstEntry: Boolean = true,
    onRegisterClick: () -> Unit,
    onVerifyClick: () -> Unit,
    onImportClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            BackOnlyTopAppBar(isRouteFirstEntry = isRouteFirstEntry, onNavigateBack = onNavigateBack, text = "Hello")
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Column {
                Button(onClick = onRegisterClick) {
                    Text(text = "Introduce me someone")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onVerifyClick) {
                    Text(text = "Take me around")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onImportClick) {
                    Text(text = "let me see your gallery")
                }
            }
        }
    }

}