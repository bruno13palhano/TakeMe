package com.bruno13palhano.takeme.ui.screens.travelhistory.presenter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bruno13palhano.takeme.R
import com.bruno13palhano.takeme.ui.screens.travelhistory.viewmodel.TravelHistoryViewModel
import com.bruno13palhano.takeme.ui.shared.base.rememberFlowWithLifecycle

@Composable
internal fun TravelHistoryRoute(
    navigateToHome: () -> Unit,
    viewModel: TravelHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sideEffect = rememberFlowWithLifecycle(flow = viewModel.sideEffect)

    LaunchedEffect(sideEffect) {
        sideEffect.collect {
            when (it) {
                is TravelHistorySideEffect.NavigateToHome -> navigateToHome()
            }
        }
    }

    TravelHistoryContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TravelHistoryContent(
    modifier: Modifier = Modifier,
    state: TravelHistoryState,
    onAction: (action: TravelHistoryAction) -> Unit
) {
    Scaffold(
        modifier = modifier.consumeWindowInsets(WindowInsets.safeDrawing),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.travel_history)) },
                navigationIcon = {
                    IconButton(onClick = { onAction(TravelHistoryAction.OnNavigateToHome) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_to_home)
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

        }
    }
}