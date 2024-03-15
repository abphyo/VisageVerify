package com.biho.visageverify.presentation.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.biho.visageverify.domain.usecases.FaceDetectionUseCase
import com.biho.visageverify.presentation.CameraImageAnalyzer
import com.biho.visageverify.presentation.screens.IntroduceViewModel
import com.biho.visageverify.presentation.composables.sharedViewModel
import com.biho.visageverify.presentation.screens.DetectScreenState
import com.biho.visageverify.presentation.screens.IntroduceContent
import com.biho.visageverify.presentation.screens.IntroduceScreen
import com.biho.visageverify.presentation.utils.LocalApplicationContext
import com.google.mlkit.vision.face.Face
import org.koin.compose.koinInject

fun NavGraphBuilder.introduceRoute(navController: NavHostController) {

    navigation(
        route = MainRoute.Introduce.route,
        startDestination = IntroduceRoute.Introduce.route
    ) {

        composable(route = IntroduceRoute.Introduce.route) { entry ->
            val onNavigateBackOnPermissionDenied = {
                navController.navigate(MainRoute.Splash.route)
                navController.popBackStack()
            }

            val onNavigateBack = {
                navController.popBackStack()
                navController.navigate(MainRoute.Home.route)
            }

            val applicationContext = LocalApplicationContext.current

            val sharedIntroduceViewModel =
                entry.sharedViewModel<IntroduceViewModel>(navController = navController)

            val detector = koinInject<FaceDetectionUseCase>()
            val detectFacesPerFrame = detector::detectFacePerFrame
            val originalFrame = remember {
                mutableStateOf(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888))
            }

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
                        ) { results, frame, width, height ->
                            faces.clear()
                            faces.addAll(results)
                            originalFrame.value = frame
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

            IntroduceScreen(
                isRouteFirstEntry = entry.isRouteFirstEntry(),
                onNavigateBack = onNavigateBack,
                title = "Show me someone"
            ) {
                IntroduceContent(
                    paddingValues = it,
                    screenState = sharedIntroduceViewModel.screenState.value,
                    loadingState = sharedIntroduceViewModel.loadingState.value,
                    croppedFace = sharedIntroduceViewModel.croppedBitmap.value,
                    faces = faces,
                    imageHeight = imageHeight.intValue,
                    imageWidth = imageWidth.intValue,
                    onCropImage = { rect ->
                        sharedIntroduceViewModel.cropFaceFromFrame(originalFrame.value, rect)
                    },
                    onRememberImage = { name ->
                        sharedIntroduceViewModel.rememberFace(name = name)
                    },
                    onClearClick = sharedIntroduceViewModel::clearCropState,
                    onFinish = onNavigateBack,
                    controller = cameraController
                )
            }
        }
    }
}