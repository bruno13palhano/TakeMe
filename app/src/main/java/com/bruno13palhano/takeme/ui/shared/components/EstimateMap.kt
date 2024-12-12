package com.bruno13palhano.takeme.ui.shared.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bruno13palhano.data.model.Route
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
internal fun EstimateMap(
    modifier: Modifier = Modifier,
    route: Route,
    originTitle: String,
    destinationTitle: String
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

    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(0.0, 0.0),
            0f
        )
    }

    val bounds = LatLngBounds.builder()

    val markerOrigin = rememberMarkerState()
    val markerDestination = rememberMarkerState()

    LaunchedEffect(route) {
        markerOrigin.position = LatLng(
            route.origin.latitude,
            route.origin.longitude
        )

        markerDestination.position = LatLng(
            route.destination.latitude,
            route.destination.longitude
        )
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPosition,
        uiSettings = uiSettings,
        onMapLoaded = {
            route.steps.forEach {
                bounds.include(LatLng(it.startLocation.latitude, it.startLocation.longitude))
                bounds.include(LatLng(it.endLocation.latitude, it.endLocation.longitude))
            }

            cameraPosition.move(
                update = CameraUpdateFactory.newLatLngBounds(bounds.build(), 100)
            )
        }
    ) {
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