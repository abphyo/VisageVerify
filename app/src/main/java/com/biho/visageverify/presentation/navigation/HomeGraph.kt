package com.biho.visageverify.presentation.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import coil.ImageLoader
import coil.request.ImageRequest
import com.biho.visageverify.domain.usecases.FaceDetectionUseCase
import com.biho.visageverify.presentation.CameraImageAnalyzer
import com.biho.visageverify.presentation.GalleryImageAnalyzer
import com.biho.visageverify.presentation.composables.sharedViewModel
import com.biho.visageverify.presentation.screens.HomeScreen
import com.biho.visageverify.presentation.screens.HomeViewModel
import com.biho.visageverify.presentation.screens.PhotoScreen
import com.biho.visageverify.presentation.utils.LocalApplicationContext
import com.biho.visageverify.presentation.utils.LocalPermissionChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.util.UUID

fun NavGraphBuilder.homeRoute(navController: NavHostController) {

    navigation(route = MainRoute.Home.route, startDestination = HomeRoute.Camera.route) {
        composable(route = HomeRoute.Camera.route) { entry ->

            val applicationContext = LocalApplicationContext.current
            val context = LocalContext.current
            val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope
            val permissionChannel = LocalPermissionChannel.current
            val imageLoader = koinInject<ImageLoader>()

            val onNavigateBack = {
                if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED)
                    navController.navigateUp()
            }

            val detector = koinInject<FaceDetectionUseCase>()
            val homeViewModel = entry.sharedViewModel<HomeViewModel>(navController = navController)

            val detectFacesPerFrame = detector::detectFacePerFrame

            val persons by homeViewModel.persons.collectAsState()

            val imageWidth = remember { mutableIntStateOf(0) }
            val imageHeight = remember { mutableIntStateOf(0) }

            val onPickImageUri = { uri: Uri? ->
                if (uri != null) {
                    homeViewModel.clearPersons()
                    homeViewModel.pickPhotoUri(uri = uri)
                    lifecycleScope.launch {
                        withContext(context = Dispatchers.IO) {
                            val request = ImageRequest.Builder(context = context)
                                .data(homeViewModel.pickedPhotoUri)
                                .target { drawable ->
                                    println("drawable: $drawable")
                                    val photoBitmap = (drawable as BitmapDrawable).bitmap
                                    homeViewModel.updatePickedPhoto(photo = photoBitmap)
                                    GalleryImageAnalyzer(
                                        detectFaceFromPhoto = detectFacesPerFrame
                                    ) { results, photo ->
                                        homeViewModel.validateFaces(frame = photo, faces = results)
                                    }.analyze(photo = photoBitmap)
                                }
                                .build()
                            imageLoader.execute(request)
                        }
                    }
                    navController.navigate(route = HomeRoute.Photo.route)
                } else
                    Toast.makeText(applicationContext, "file not found", Toast.LENGTH_SHORT).show()
            }

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
                cameraController = cameraController,
                onPickImageUri = onPickImageUri
            )
        }

        composable(route = HomeRoute.Photo.route) { entry ->

            val homeViewModel = entry.sharedViewModel<HomeViewModel>(navController = navController)

            val onNavigateBack = {
                navController.popBackStack()
                navController.navigate(MainRoute.Home.route)
            }

            val persons by homeViewModel.persons.collectAsState()

            LaunchedEffect(key1 = homeViewModel.pickedPhotoUri) {

            }

            PhotoScreen(
                isRouteFirstEntry = entry.isRouteFirstEntry(),
                onNavigateBack = onNavigateBack,
                persons = persons,
                image = homeViewModel.pickedPhoto
            )
        }
    }
}