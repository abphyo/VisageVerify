package com.biho.visageverify.presentation.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.biho.visageverify.presentation.screens.HomeScreen
import com.biho.visageverify.presentation.utils.LocalPermissionChannel
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun MainNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val permissionChannel = LocalPermissionChannel.current
    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope

    NavHost(navController = navController, startDestination = MainRoute.Home.route) {

        val onNavigateBack = {
            if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED)
                navController.navigateUp()
        }

        composable(route = MainRoute.Home.route) { entry ->

            val onRegisterClick = {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) -> navController.navigate(MainRoute.Detect.route)

                    else -> lifecycleScope.launch {
                        permissionChannel.send(UUID.randomUUID().toString())
                    }
                }
                Unit
            }
            val onVerifyClick = {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) -> navController.navigate(MainRoute.Verify.route)

                    else -> lifecycleScope.launch {
                        permissionChannel.send(UUID.randomUUID().toString())
                    }
                }
                Unit
            }
            val onImportClick = {
                // not yet
            }
            HomeScreen(
                isRouteFirstEntry = entry.isRouteFirstEntry(),
                onRegisterClick = onRegisterClick,
                onVerifyClick = onVerifyClick,
                onImportClick = onImportClick,
                onNavigateBack = onNavigateBack
            )
        }

        detectionRoute(navController = navController)

        composable(route = MainRoute.Verify.route) {

        }

    }

}

fun NavBackStackEntry.isRouteFirstEntry(): Boolean {
    return destination.route == MainRoute.Home.route
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