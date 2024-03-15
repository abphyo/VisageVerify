package com.biho.visageverify.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.biho.visageverify.presentation.screens.AnimatedSplashScreen
import com.biho.visageverify.presentation.utils.LocalPermissionGrantedChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay

@Composable
fun MainNavGraph(navController: NavHostController) {

    NavHost(navController = navController, startDestination = MainRoute.Splash.route) {

        composable(route = MainRoute.Splash.route) {
            val popSplashScreen = {
                navController.popBackStack()
                navController.navigate(MainRoute.Home.route)
            }
            val lifecycle = LocalLifecycleOwner.current.lifecycle

            val granted = LocalPermissionGrantedChannel.current

            LaunchedEffect(key1 = lifecycle.currentState) {
                delay(1000)
                granted.consumeEach {
                    popSplashScreen()
                }
            }
            AnimatedSplashScreen()
        }

        homeRoute(navController = navController)

        introduceRoute(navController = navController)

    }

}

@Composable
fun ObserveLifeCycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}