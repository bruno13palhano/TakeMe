package com.bruno13palhano.data.model

import com.bruno13palhano.data.remote.model.helper.StepRemote
import kotlinx.serialization.Serializable

@Serializable
data class Step(
    val startLocation: Coordinates,
    val endLocation: Coordinates
)

internal fun StepRemote.asExternal() = Step(
    startLocation = startLocation.location,
    endLocation = endLocation.location
)