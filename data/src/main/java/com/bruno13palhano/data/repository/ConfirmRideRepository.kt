package com.bruno13palhano.data.repository

import com.bruno13palhano.data.model.ConfirmRide
import com.bruno13palhano.data.model.RequestConfirmRide
import com.bruno13palhano.data.model.Resource

interface ConfirmRideRepository {
    suspend fun confirmRide(confirmRide: RequestConfirmRide): Resource<ConfirmRide>
}