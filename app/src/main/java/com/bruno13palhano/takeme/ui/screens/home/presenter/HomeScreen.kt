package com.bruno13palhano.takeme.ui.screens.home.presenter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bruno13palhano.takeme.R
import com.bruno13palhano.takeme.ui.screens.home.viewmodel.HomeViewModel
import com.bruno13palhano.takeme.ui.shared.base.clearFocusOnKeyboardDismiss
import com.bruno13palhano.takeme.ui.shared.base.clickableWithoutRipple
import com.bruno13palhano.takeme.ui.shared.base.rememberFlowWithLifecycle
import com.bruno13palhano.takeme.ui.shared.components.CircularProgress
import com.bruno13palhano.takeme.ui.shared.components.CustomTextField
import com.bruno13palhano.takeme.ui.shared.components.getInternalErrorMessages
import kotlinx.coroutines.launch

@Composable
internal fun HomeRoute(
    navigateToDriverPicker: (customerId: String, origin: String, destination: String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.container.state.collectAsStateWithLifecycle()
    val sideEffect = rememberFlowWithLifecycle(flow = viewModel.container.sideEffect)

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val invalidFieldMessage = stringResource(id = R.string.invalid_field)
    val noDriverFound = stringResource(id = R.string.no_driver_found)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val internalErrorMessages = getInternalErrorMessages()

    LaunchedEffect(sideEffect) {
        sideEffect.collect {
            when (it) {
                is HomeSideEffect.ShowResponseError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = it.message ?: "",
                            withDismissAction = true
                        )
                    }
                }

                is HomeSideEffect.ShowInternalError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = internalErrorMessages[it.internalError] ?: "",
                            withDismissAction = true
                        )
                    }
                }

                is HomeSideEffect.ShowNoDriverFound -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = noDriverFound,
                            withDismissAction = true
                        )
                    }
                }

                is HomeSideEffect.InvalidFieldError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = invalidFieldMessage,
                            withDismissAction = true
                        )
                    }
                }

                is HomeSideEffect.DismissKeyboard -> {
                    keyboardController?.hide()
                    focusManager.clearFocus(force = true)
                }

                is HomeSideEffect.NavigateToDriverPicker -> {
                    navigateToDriverPicker(it.customerId, it.origin, it.destination)
                }
            }
        }
    }

    HomeContent(
        snackbarHostState = snackbarHostState,
        state = state,
        onSendEvent = viewModel::sendEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    state: HomeState,
    onSendEvent: (event: HomeEvent) -> Unit
) {
    Scaffold(
        modifier = modifier
            .clickableWithoutRipple { onSendEvent(HomeEvent.DismissKeyboard) }
            .consumeWindowInsets(WindowInsets.safeDrawing),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            if (!state.isSearch) {
                FloatingActionButton(onClick = { onSendEvent(HomeEvent.NavigateToDriverPicker) }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(id = R.string.search_driver)
                    )
                }
            }
        }
    ) {
        if (state.isSearch) {
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
                    .verticalScroll(rememberScrollState())
            ) {
                CustomTextField(
                    modifier = Modifier
                        .clearFocusOnKeyboardDismiss()
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    value = state.homeInputFields.customerId,
                    onValueChange = state.homeInputFields::updateCustomerId,
                    label = stringResource(id = R.string.customer_id),
                    placeholder = stringResource(id = R.string.customer_id_placeholder),
                    isError = state.isFieldInvalid && state.homeInputFields.customerId.isBlank()
                )

                CustomTextField(
                    modifier = Modifier
                        .clearFocusOnKeyboardDismiss()
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    value = state.homeInputFields.origin,
                    onValueChange = state.homeInputFields::updateOrigin,
                    label = stringResource(id = R.string.origin),
                    placeholder = stringResource(id = R.string.origin_placeholder),
                    isError = state.isFieldInvalid && state.homeInputFields.origin.isBlank()
                )

                CustomTextField(
                    modifier = Modifier
                        .clearFocusOnKeyboardDismiss()
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    value = state.homeInputFields.destination,
                    onValueChange = state.homeInputFields::updateDestination,
                    label = stringResource(id = R.string.destination),
                    placeholder = stringResource(id = R.string.destination_placeholder),
                    isError = state.isFieldInvalid && state.homeInputFields.destination.isBlank()
                )
            }
        }
    }
}