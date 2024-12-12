package com.bruno13palhano.takeme.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
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
                navigateToDriverPicker = { customerId, origin, destination ->
                    navController.navigate(
                        route = MainRoutes.DriverPicker(
                            customerId = customerId,
                            origin = origin,
                            destination = destination
                        )
                    )
                }
            )
        }

        composable<MainRoutes.DriverPicker> {
            val customerId = it.toRoute<MainRoutes.DriverPicker>().customerId
            val origin = it.toRoute<MainRoutes.DriverPicker>().origin
            val destination = it.toRoute<MainRoutes.DriverPicker>().destination

            DriverPickerRoute(
                customerId = customerId,
                origin = origin,
                destination = destination,
                navigateToTravelHistory = { navController.navigate(MainRoutes.TravelHistory) },
                navigateBack = {
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
    data class DriverPicker(
        val customerId: String,
        val origin: String,
        val destination: String
    ) : MainRoutes

    @Serializable
    data object TravelHistory : MainRoutes
}

