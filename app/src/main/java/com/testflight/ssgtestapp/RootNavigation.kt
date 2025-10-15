package com.testflight.ssgtestapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.testflight.ssgtestapp.presentation.cancel_screen.CancelScreen
import com.testflight.ssgtestapp.presentation.start_screen.StartScreen
import com.testflight.ssgtestapp.utils.screenFadeIn
import com.testflight.ssgtestapp.utils.screenFadeOut
import com.testflight.ssgtestapp.utils.screenSlideIn
import com.testflight.ssgtestapp.utils.screenSlideOut
import kotlinx.serialization.Serializable

sealed interface AppDestination

@Serializable
data object StartDestination : AppDestination

@Serializable
data object CancelDestination : AppDestination

/**
 * Main navigation graph for the application
 */
@Composable
fun RootNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = StartDestination,
        enterTransition = { screenSlideIn() }, // next screen
        exitTransition = { screenFadeOut() }, // current screen
        popEnterTransition = { screenFadeIn() }, // back, next screen
        popExitTransition = { screenSlideOut() },// back, current screen
    ) {
        composable<StartDestination> {
            StartScreen(
                onNavigateToCancel = {
                    navController.navigate(CancelDestination)
                }
            )
        }
        composable<CancelDestination> {
            CancelScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
