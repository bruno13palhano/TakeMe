package com.bruno13palhano.takeme.ui.screens.travelhistory.presenter

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bruno13palhano.data.model.DriverInfo
import com.bruno13palhano.takeme.R
import com.bruno13palhano.takeme.ui.screens.travelhistory.viewmodel.TravelHistoryViewModel
import com.bruno13palhano.takeme.ui.shared.base.clearFocusOnKeyboardDismiss
import com.bruno13palhano.takeme.ui.shared.base.clickableWithoutRipple
import com.bruno13palhano.takeme.ui.shared.base.rememberFlowWithLifecycle
import com.bruno13palhano.takeme.ui.shared.components.CircularProgress
import com.bruno13palhano.takeme.ui.shared.components.CustomTextField
import com.bruno13palhano.takeme.ui.shared.components.formatDate
import com.bruno13palhano.takeme.ui.shared.components.getInternalErrorMessages
import com.bruno13palhano.takeme.ui.theme.TakeMeTheme
import kotlinx.coroutines.launch

@Composable
internal fun TravelHistoryRoute(
    navigateToHome: () -> Unit,
    viewModel: TravelHistoryViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.handleEvents() }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val sideEffect = rememberFlowWithLifecycle(flow = viewModel.sideEffect)

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val customerIdError = stringResource(id = R.string.customer_id_required)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val internalErrorMessages = getInternalErrorMessages()

    BackHandler { navigateToHome() }

    LaunchedEffect(state.start) {
        if (state.start) {
            viewModel.sendEvent(event = TravelHistoryEvent.GetDrivers)
        }
    }

    LaunchedEffect(sideEffect) {
        sideEffect.collect {
            when (it) {
                is TravelHistorySideEffect.ShowResponseError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = it.message ?: "",
                            withDismissAction = true
                        )
                    }
                }

                is TravelHistorySideEffect.ShowInternalError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = internalErrorMessages[it.internalError] ?: "",
                            withDismissAction = true
                        )
                    }
                }

                is TravelHistorySideEffect.InvalidFieldError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = customerIdError,
                            withDismissAction = true
                        )
                    }
                }

                is TravelHistorySideEffect.DismissKeyboard -> {
                    keyboardController?.hide()
                    focusManager.clearFocus(force = true)
                }

                is TravelHistorySideEffect.NavigateToHome -> navigateToHome()
            }
        }
    }

    TravelHistoryContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::sendEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun TravelHistoryContent(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    state: TravelHistoryState,
    onEvent: (event: TravelHistoryEvent) -> Unit
) {
    Scaffold(
        modifier = modifier
            .clickableWithoutRipple { onEvent(TravelHistoryEvent.DismissKeyboard) }
            .consumeWindowInsets(WindowInsets.safeDrawing),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.travel_history)) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(TravelHistoryEvent.NavigateToHome) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_to_home)
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
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .consumeWindowInsets(it),
                contentPadding = PaddingValues(4.dp)
            ) {
                stickyHeader {
                    DriverSelector(
                        expanded = state.expandSelector,
                        customerId = state.travelHistoryInputFields.customerId,
                        drivers = state.drivers,
                        selectedDriver = state.currentDriver.name,
                        isFieldInvalid = state.isFieldInvalid,
                        onCustomerIdChange = state.travelHistoryInputFields::updateCustomerId,
                        onDriverSelected = { driver ->
                            onEvent(TravelHistoryEvent.UpdateCurrentDriver(driver = driver))
                        },
                        onClick = { expanded ->
                            onEvent(TravelHistoryEvent.ExpandSelector(expandSelector = expanded))
                        },
                        onDismiss = { expanded ->
                            onEvent(TravelHistoryEvent.ExpandSelector(expandSelector = expanded))
                        },
                        onSearchClick = {
                            onEvent(
                                TravelHistoryEvent.GetCustomerRides(
                                    customerId = state.travelHistoryInputFields.customerId,
                                    driverId = state.currentDriver.id
                                )
                            )
                        }
                    )
                }

                items(items = state.rides) { ride ->
                    RideCard(
                        modifier = Modifier.padding(4.dp),
                        origin = ride.origin,
                        destination = ride.destination,
                        date = formatDate(date = ride.date),
                        driverName = ride.driver.name,
                        distance = ride.distance,
                        duration = ride.duration,
                        value = ride.value
                    )
                }
            }
        }
    }
}

@Composable
private fun RideCard(
    modifier: Modifier = Modifier,
    origin: String,
    destination: String,
    date: String,
    driverName: String,
    distance: Double,
    duration: String,
    value: Float
) {
    ElevatedCard(modifier = modifier) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.ride_title_tag, origin, destination),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            text = stringResource(
                id = R.string.ride_description_tag,
                duration,
                distance,
                value,
                driverName
            ),
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.ride_foot_tag, date),
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
private fun DriverSelector(
    expanded: Boolean,
    customerId: String,
    drivers: List<DriverInfo>,
    selectedDriver: String,
    isFieldInvalid: Boolean,
    onCustomerIdChange: (customerId: String) -> Unit,
    onDriverSelected: (driver: DriverInfo) -> Unit,
    onClick: (expanded: Boolean) -> Unit,
    onDismiss: (expanded: Boolean) -> Unit,
    onSearchClick: () -> Unit
) {
    Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
        CustomTextField(
            modifier = Modifier
                .clearFocusOnKeyboardDismiss()
                .padding(start = 4.dp, bottom = 4.dp, end = 4.dp)
                .fillMaxWidth(),
            value = customerId,
            onValueChange = onCustomerIdChange,
            label = stringResource(id = R.string.customer_id),
            placeholder = stringResource(id = R.string.customer_id_placeholder),
            isError = isFieldInvalid && customerId.isBlank()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .weight(1f)
            ) {
                Card(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    shape = RectangleShape,
                    onClick = { onClick(!expanded) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f),
                            text = selectedDriver
                        )

                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = ""
                        )
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onDismiss(false) }
                ) {
                    drivers.forEachIndexed { index, driver ->
                        DropdownMenuItem(
                            text = { Text(text = driver.name) },
                            onClick = {
                                onDriverSelected(drivers[index])
                                onDismiss(false)
                            }
                        )
                    }
                }
            }

            Button(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = onSearchClick
            ) {
                Text(
                    text = stringResource(id = R.string.search)
                )
            }
        }
    }
}

@Preview
@Composable
private fun TravelHistoryContentPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        TakeMeTheme {
            TravelHistoryContent(
                state = TravelHistoryState.initialState,
                snackbarHostState = SnackbarHostState(),
                onEvent = {}
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RideCardPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        TakeMeTheme {
            RideCard(
                origin = "Some random origin",
                destination = "Some random destination",
                date = "23/05/1998",
                driverName = "Some random driver",
                distance = 234.56,
                duration = "1223",
                value = 1234.67f
            )
        }
    }
}