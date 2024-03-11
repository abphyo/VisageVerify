package com.biho.visageverify.presentation.navigation

import androidx.navigation.NavBackStackEntry

enum class MainRoute(val route: String) {
    Splash("splash"),
    Home("home"),
    Introduce("introduce_main"),
    Verify("verify"),
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
        append({placeholder})
    }
}

fun MainRoute.withArg(arg: String): String {
    return buildString {
        append(route)
        append("/")
        append(arg)
    }
}