package com.biho.visageverify.presentation.ui

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.channels.Channel

val LocalPermissionChannel = compositionLocalOf<Channel<String>> { error("no local permission channel is provided") }
val LocalApplicationContext = compositionLocalOf<Context> { error("no local application context is provided") }