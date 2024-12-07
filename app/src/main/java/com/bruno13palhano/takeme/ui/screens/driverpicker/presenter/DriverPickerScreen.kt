package com.bruno13palhano.takeme.ui.screens.driverpicker.presenter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bruno13palhano.takeme.R
import com.bruno13palhano.takeme.ui.screens.driverpicker.viewmodel.DriverPickerViewModel
import com.bruno13palhano.takeme.ui.shared.base.rememberFlowWithLifecycle

@Composable
internal fun DriverPickerRoute(
    navigateToTravelHistory: () -> Unit,
    navigateBack: () -> Unit,
    viewModel: DriverPickerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sideEffect = rememberFlowWithLifecycle(flow = viewModel.sideEffect)

    LaunchedEffect(sideEffect) {
        sideEffect.collect {
            when (it) {
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

@OptIn(ExperimentalMaterial3Api::class)
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
        Column(
            modifier = Modifier
                .padding(it)
                .consumeWindowInsets(it)
                .verticalScroll(rememberScrollState())
        ) {
            Button(onClick = { onAction(DriverPickerAction.OnNavigateToTravelHistory) }) {
                Text(text = "Click")
            }
        }
    }
}