package com.biho.visageverify.presentation.navigation

import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

enum class MainRoute(val route: String) {
    Splash("splash"),
    Home("home"),
    Introduce("introduce_main"),
    Verify("verify"),
}

enum class HomeRoute(val route: String) {
    Camera("camera"),
    Photo("photo")
}

enum class IntroduceRoute(val route: String) {
    Introduce("introduce")
}

fun NavBackStackEntry.isRouteFirstEntry(): Boolean {
    return destination.route == MainRoute.Home.route
}

fun MainRoute.withArgReceiver(placeholder: String): String {
    return buildString {
        append(route)
        append("/")
        append("{$placeholder}")
    }
}

@Serializable
data class UriNavArg(val uri: String)

fun MainRoute.withUriArg(uri: Uri): String {
    return buildString {
        append(route)
        append("/")
        append(
            Json.encodeToString(
                serializer = UriNavArg.serializer(),
                value = UriNavArg(uri.toString())
            )
        )
    }
}
