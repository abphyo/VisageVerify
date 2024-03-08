package com.biho.visageverify.presentation.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.biho.visageverify.presentation.CameraImageAnalyzer
import com.biho.visageverify.presentation.DetectViewModel
import com.biho.visageverify.presentation.composables.sharedViewModel
import com.biho.visageverify.presentation.screens.CameraScreen
import com.biho.visageverify.presentation.utils.LocalApplicationContext
import com.google.mlkit.vision.face.Face

fun NavGraphBuilder.detectionRoute(navController: NavHostController) {

    navigation(
        route = MainRoute.Detect.route,
        startDestination = DetectRoute.Register.route
    ) {

        composable(route = DetectRoute.Register.route) { entry ->

            val onNavigateBackOnPermissionDenied = {
                navController.popBackStack()
                navController.navigate(MainRoute.Home.route)
            }

            val applicationContext = LocalApplicationContext.current

            val sharedDetectViewModel =
                entry.sharedViewModel<DetectViewModel>(navController = navController)
            val detectFacesPerFrame = sharedDetectViewModel.detectFacesPerFrame

            val faces = remember { mutableStateListOf<Face>() }

            val imageWidth = remember { mutableIntStateOf(0) }
            val imageHeight = remember { mutableIntStateOf(0) }

            val cameraController = remember {
                LifecycleCameraController(applicationContext).apply {
                    setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                    imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                    setImageAnalysisAnalyzer(
                        ContextCompat.getMainExecutor(applicationContext),
                        CameraImageAnalyzer(
                            detectFacePerFrame = detectFacesPerFrame
                        ) { results, width, height ->
                            faces.clear()
                            faces.addAll(results)
                            imageWidth.intValue = width
                            imageHeight.intValue = height
                        }
                    )
                }
            }

            LaunchedEffect(key1 = LocalLifecycleOwner.current.lifecycle.currentState) {
                if (ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                )
                    onNavigateBackOnPermissionDenied()
            }

            CameraScreen(
                faces = faces,
                imageHeight = imageHeight.intValue,
                imageWidth = imageWidth.intValue,
                isRouteFirstEntry = entry.isRouteFirstEntry(),
                onNavigateBack = {
                    navController.navigateUp()
                },
                controller = cameraController
            )
        }

        composable(route = DetectRoute.Remember.route) { entry ->

            val sharedDetectViewModel = entry.sharedViewModel<DetectViewModel>(navController = navController)
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .scrollable(state = scrollState, orientation = Orientation.Vertical)
            ) {

            }
        }

    }
}