package com.biho.visageverify.presentation.navigation

enum class MainRoute(val route: String) {
    Home("home"),
    Detect("detect"),
    Verify("verify"),
}

enum class DetectRoute(val route: String) {
    Register("register"),
    Remember("remember"),
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