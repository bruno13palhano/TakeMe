package com.bruno13palhano.takeme.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerRoute
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeRoute
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryRoute
import kotlinx.serialization.Serializable

@Composable
internal fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MainRoutes.Home
    ) {
        composable<MainRoutes.Home> {
            HomeRoute(
                navigateToDriverPicker = { navController.navigate(MainRoutes.DriverPicker) }
            )
        }

        composable<MainRoutes.DriverPicker> {
            DriverPickerRoute(
                navigateToTravelHistory = { navController.navigate(MainRoutes.TravelHistory) },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable<MainRoutes.TravelHistory> {
            TravelHistoryRoute(
                navigateToHome = {
                    navController.navigate(MainRoutes.Home) {
                        popUpTo(MainRoutes.Home) {
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

internal sealed interface MainRoutes {
    @Serializable
    data object Home : MainRoutes

    @Serializable
    data object DriverPicker : MainRoutes

    @Serializable
    data object TravelHistory : MainRoutes
}

