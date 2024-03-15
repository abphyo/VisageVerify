package com.biho.visageverify.presentation.composables

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner

@Composable
fun BackHandler(isEnabled: Boolean, lambda: () -> Unit) {
    val onBackPressed by rememberUpdatedState(newValue = lambda)
    val callBack = remember {
        object : OnBackPressedCallback(enabled = isEnabled) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
    }
    SideEffect {
        callBack.isEnabled = isEnabled
    }
    val dispatcher = checkNotNull(LocalOnBackPressedDispatcherOwner.current) {
        "no local onBackPressedDispatcher is provided"
    }.onBackPressedDispatcher
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner, key2 = dispatcher) {
        dispatcher.addCallback(owner = lifecycleOwner, onBackPressedCallback = callBack)
        onDispose {
            callBack.remove()
        }
    }
}