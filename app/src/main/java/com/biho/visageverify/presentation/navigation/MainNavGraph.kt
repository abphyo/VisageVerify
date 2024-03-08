package com.biho.visageverify.presentation.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.biho.visageverify.presentation.DetectViewModel
import com.biho.visageverify.presentation.composables.sharedViewModel
import com.biho.visageverify.presentation.screens.CameraScreen
import com.biho.visageverify.presentation.screens.HomeScreen
import com.biho.visageverify.presentation.ui.LocalApplicationContext
import com.biho.visageverify.presentation.ui.LocalPermissionChannel
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
        val onNavigateBackOnPermissionDenied = {
            navController.navigate(MainRoute.Home.route) {
                popUpTo(MainRoute.Home.route) {
                    inclusive = false
                    saveState = false
                }
            }
        }
        navigation(
            route = MainRoute.Detect.route,
            startDestination = DetectRoute.Register.route
        ) {
            composable(route = DetectRoute.Register.route) { entry ->
                val applicationContext = LocalApplicationContext.current
                val sharedDetectViewModel =
                    entry.sharedViewModel<DetectViewModel>(navController = navController)
                val resultBitmaps by sharedDetectViewModel.resultBitmaps.collectAsState()
                val cameraController = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                        setImageAnalysisAnalyzer(
                            ContextCompat.getMainExecutor(applicationContext),
                            sharedDetectViewModel.cameraImageAnalyzer
                        )
                    }
                }
                LaunchedEffect(key1 = LocalLifecycleOwner.current.lifecycle.currentState) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    )
                        onNavigateBackOnPermissionDenied()
                }
                LaunchedEffect(key1 = resultBitmaps) {
                    if (resultBitmaps.isNotEmpty())
                        navController.navigate(DetectRoute.Remember.route)
                }
                CameraScreen(
                    resultBitmaps = resultBitmaps,
                    isRouteFirstEntry = entry.isRouteFirstEntry(),
                    onNavigateBack = {
                        navController.navigateUp()
                    },
                    controller = cameraController
                )
            }
            composable(route = DetectRoute.Remember.route) { entry ->
                val sharedDetectViewModel = entry.sharedViewModel<DetectViewModel>(navController = navController)
                val resultBitmaps by sharedDetectViewModel.resultBitmaps.collectAsState()
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .scrollable(state = scrollState, orientation = Orientation.Vertical)
                ) {
                    resultBitmaps.forEach {
                        Image(bitmap = it.croppedBitmap.asImageBitmap(), contentDescription = null)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
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