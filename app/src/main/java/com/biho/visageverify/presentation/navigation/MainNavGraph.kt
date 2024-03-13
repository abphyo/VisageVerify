package com.biho.visageverify.presentation.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.biho.visageverify.domain.usecases.FaceDetectionUseCase
import com.biho.visageverify.presentation.CameraImageAnalyzer
import com.biho.visageverify.presentation.screens.AnimatedSplashScreen
import com.biho.visageverify.presentation.screens.HomeScreen
import com.biho.visageverify.presentation.screens.HomeViewModel
import com.biho.visageverify.presentation.utils.LocalApplicationContext
import com.biho.visageverify.presentation.utils.LocalPermissionChannel
import com.biho.visageverify.presentation.utils.LocalPermissionGrantedChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.util.UUID

@Composable
fun MainNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val permissionChannel = LocalPermissionChannel.current
    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope

    NavHost(navController = navController, startDestination = MainRoute.Splash.route) {

        val onNavigateBack = {
            if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED)
                navController.navigateUp()
        }

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

        composable(route = MainRoute.Home.route) { entry ->

            val applicationContext = LocalApplicationContext.current

            val detector = koinInject<FaceDetectionUseCase>()
            val homeViewModel = koinViewModel<HomeViewModel>()
            val detectFacesPerFrame = detector::detectFacePerFrame

            val persons by homeViewModel.persons.collectAsState()

            val imageWidth = remember { mutableIntStateOf(0) }
            val imageHeight = remember { mutableIntStateOf(0) }

            val onNavigateToIntroduce = {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) -> {
                        homeViewModel.clearPersons()
                        navController.navigate(MainRoute.Introduce.route) {
                            popUpTo(MainRoute.Home.route) {
                                inclusive = true
                            }
                        }
                    }

                    else -> lifecycleScope.launch {
                        permissionChannel.send(UUID.randomUUID().toString())
                    }
                }
                Unit
            }

            val onNavigateBackOnPermissionDenied = {
                navController.popBackStack()
                navController.navigate(MainRoute.Splash.route)
            }

            LaunchedEffect(key1 = LocalLifecycleOwner.current.lifecycle.currentState) {
                if (ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                )
                    onNavigateBackOnPermissionDenied()
            }

            val cameraController = remember {
                LifecycleCameraController(applicationContext).apply {
                    setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                    imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                    setImageAnalysisAnalyzer(
                        ContextCompat.getMainExecutor(applicationContext),
                        CameraImageAnalyzer(
                            detectFacePerFrame = detectFacesPerFrame
                        ) { results, frame, width, height ->
                            homeViewModel.validateFaces(frame = frame, faces = results)
                            imageWidth.intValue = width
                            imageHeight.intValue = height
                        }
                    )
                }
            }

            HomeScreen(
                isRouteFirstEntry = entry.isRouteFirstEntry(),
                onNavigateIntroduce = onNavigateToIntroduce,
                onNavigateBack = onNavigateBack,
                persons = persons,
                imageWidth = imageWidth.intValue,
                imageHeight = imageHeight.intValue,
                cameraController = cameraController
            )
        }

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