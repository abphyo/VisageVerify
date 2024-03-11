package com.biho.visageverify.presentation.utils

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.channels.Channel

val LocalPermissionChannel = compositionLocalOf<Channel<String>> { error("no local permission channel is provided") }
val LocalPermissionGrantedChannel = compositionLocalOf<Channel<String>> { error("no local permission granted channel is provided") }
val LocalApplicationContext = compositionLocalOf<Context> { error("no local application context is provided") }