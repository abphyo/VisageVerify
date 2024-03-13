package com.biho.visageverify.presentation.screens

sealed class DetectScreenState(val message: String? = null) {
    data object Idle: DetectScreenState()
    data object Cropped: DetectScreenState()
    data object Success: DetectScreenState()

    class Error(message: String): DetectScreenState(message = message)
}

enum class LoadingState {
    Idle,
    Loading
}