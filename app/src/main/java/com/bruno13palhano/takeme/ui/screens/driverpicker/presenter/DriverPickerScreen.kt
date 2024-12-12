package com.bruno13palhano.takeme.ui.screens.driverpicker.presenter

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.bruno13palhano.takeme.ui.shared.components.getInternalErrorMessages
import kotlinx.coroutines.launch

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

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val internalErrorMessages = getInternalErrorMessages()

    BackHandler { navigateBack() }

    LaunchedEffect(state.start) {
        if (state.start) {
            viewModel.onAction(
                action = DriverPickerAction.OnUpdateCustomerParams(
                    customerId = customerId,
                    origin = origin,
                    destination = destination
                )
            )
        }
    }

    LaunchedEffect(state.start) {
        if (state.start) {
            viewModel.onAction(action = DriverPickerAction.OnGetLastRideEstimate)
        }
    }

    LaunchedEffect(sideEffect) {
        sideEffect.collect {
            when (it) {
                is DriverPickerSideEffect.ShowResponseError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = it.message ?: "",
                            withDismissAction = true
                        )
                    }
                }

                is DriverPickerSideEffect.ShowInternalError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = internalErrorMessages[it.internalError] ?: "",
                            withDismissAction = true
                        )
                    }
                }

                is DriverPickerSideEffect.NavigateToTravelHistory -> navigateToTravelHistory()

                is DriverPickerSideEffect.NavigateBack -> navigateBack()
            }
        }
    }

    DriverPickerContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DriverPickerContent(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        if (state.isLoading) {
            CircularProgress(
                modifier = Modifier
                    .consumeWindowInsets(it)
                    .fillMaxSize()
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(it)
                    .consumeWindowInsets(it)
                    .fillMaxSize()
            ) {
                EstimateMap(
                    modifier = Modifier
                        .sizeIn(maxHeight = 200.dp)
                        .fillMaxWidth(),
                    route = state.rideEstimate.route,
                    originTitle = state.origin,
                    destinationTitle = state.destination
                )

                LazyColumn(contentPadding = PaddingValues(4.dp)) {
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