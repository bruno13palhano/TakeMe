package com.bruno13palhano.takeme.ui.screens.driverpicker.presenter

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bruno13palhano.takeme.R
import com.bruno13palhano.takeme.ui.screens.driverpicker.viewmodel.DriverPickerViewModel
import com.bruno13palhano.takeme.ui.shared.base.rememberFlowWithLifecycle
import com.bruno13palhano.takeme.ui.shared.components.CircularProgress
import com.bruno13palhano.takeme.ui.shared.components.DriverInfoCard
import com.bruno13palhano.takeme.ui.shared.components.EstimateMap

@Composable
internal fun DriverPickerRoute(
    customerId: String,
    origin: String,
    destination: String,
    navigateToTravelHistory: () -> Unit,
    navigateBack: () -> Unit,
    viewModel: DriverPickerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sideEffect = rememberFlowWithLifecycle(flow = viewModel.sideEffect)

    LaunchedEffect(Unit) {
        viewModel.onAction(
            action = DriverPickerAction.OnUpdateCustomerParams(
                customerId = customerId,
                origin = origin,
                destination = destination
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.onAction(action = DriverPickerAction.OnGetLastRideEstimate)
    }

    LaunchedEffect(sideEffect) {
        sideEffect.collect {
            when (it) {
                is DriverPickerSideEffect.ShowError -> {
                    println("Error: ${it.message}")
                }

                is DriverPickerSideEffect.NavigateToTravelHistory -> navigateToTravelHistory()

                is DriverPickerSideEffect.NavigateBack -> navigateBack()
            }
        }
    }

    DriverPickerContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun DriverPickerContent(
    modifier: Modifier = Modifier,
    state: DriverPickerState,
    onAction: (action: DriverPickerAction) -> Unit
) {
    Scaffold(
        modifier = modifier.consumeWindowInsets(WindowInsets.safeDrawing),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.driver_picker)) },
                navigationIcon = {
                    IconButton(onClick = { onAction(DriverPickerAction.OnNavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) {
        if (state.isLoading) {
            CircularProgress(
                modifier = Modifier
                    .consumeWindowInsets(it)
                    .fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .consumeWindowInsets(it),
                contentPadding = PaddingValues(4.dp)
            ) {
                if (state.rideEstimate.route.isNotEmpty()) {
                    stickyHeader {
                        EstimateMap(
                            route = state.rideEstimate.route,
                            originTitle = state.origin,
                            destinationTitle = state.destination,
                            onAction = onAction
                        )
                    }
                }

                if (!state.isMapLoading) {
                    items(
                        items = state.rideEstimate.drivers,
                        key = { driver -> driver.id }
                    ) { driver ->
                        DriverInfoCard(
                            modifier = Modifier.padding(4.dp),
                            id = driver.id,
                            name = driver.name.orEmpty(),
                            description = driver.description.orEmpty(),
                            vehicle = driver.vehicle.orEmpty(),
                            rating = driver.review?.rating ?: 0f,
                            value = driver.value ?: 0f,
                            onClick = {
                                onAction(
                                    DriverPickerAction.OnChooseDriver(
                                        driverId = driver.id,
                                        driverName = driver.name ?: "",
                                        value = driver.value ?: 0.0f
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}