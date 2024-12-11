package com.bruno13palhano.takeme.ui.shared.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bruno13palhano.data.model.Route
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerAction
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
internal fun EstimateMap(
    route: Route,
    originTitle: String,
    destinationTitle: String,
    onAction: (action: DriverPickerAction) -> Unit
) {
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                compassEnabled = false,
                indoorLevelPickerEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,
                rotationGesturesEnabled = false,
                scrollGesturesEnabled = false,
                scrollGesturesEnabledDuringRotateOrZoom = false,
                tiltGesturesEnabled = false,
                zoomControlsEnabled = false,
                zoomGesturesEnabled = false
            )
        )
    }

    GoogleMap(
        modifier = Modifier
            .sizeIn(maxHeight = 200.dp)
            .fillMaxWidth(),
        cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(
                    route.origin.latitude,
                    route.origin.longitude
                ),
                10f
            )
        },
        uiSettings = uiSettings,
        onMapLoaded = { onAction(DriverPickerAction.OnUpdateMapLoading(isMapLoading = false)) }
    ) {
        val markerOrigin = rememberMarkerState(
            position = LatLng(
                route.origin.latitude,
                route.origin.longitude
            )
        )

        val markerDestination = rememberMarkerState(
            position = LatLng(
                route.destination.latitude,
                route.destination.longitude
            )
        )

        Marker(state = markerOrigin, title = originTitle)

        Marker(state = markerDestination, title = destinationTitle)

        Polyline(
            color = MaterialTheme.colorScheme.error,
            points = route.steps.map {
                LatLng(
                    it.startLocation.latitude,
                    it.startLocation.longitude
                )
            }
        )
    }
}