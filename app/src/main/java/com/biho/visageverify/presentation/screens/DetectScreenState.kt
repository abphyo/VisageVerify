package com.biho.visageverify.presentation.screens

sealed class DetectScreenState {
    data object Idle: DetectScreenState()
    data object Cropped: DetectScreenState()
    data object Success: DetectScreenState()
    class Error(message: String): DetectScreenState()
}

enum class LoadingState {
    Idle,
    Loading
}