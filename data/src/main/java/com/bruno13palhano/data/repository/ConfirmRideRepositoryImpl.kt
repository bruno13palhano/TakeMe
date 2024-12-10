package com.bruno13palhano.data.repository

import com.bruno13palhano.data.di.TravelInfoRemoteDataSource
import com.bruno13palhano.data.model.ConfirmRide
import com.bruno13palhano.data.model.RequestConfirmRide
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.asExternalResponse
import com.bruno13palhano.data.model.asInternalRequest
import com.bruno13palhano.data.model.convert
import com.bruno13palhano.data.remote.datasource.RemoteDataSource
import javax.inject.Inject

internal class ConfirmRideRepositoryImpl @Inject constructor(
    @TravelInfoRemoteDataSource private val remote: RemoteDataSource
): ConfirmRideRepository {
    override suspend fun confirmRide(confirmRide: RequestConfirmRide): Resource<ConfirmRide> {
        val result = remote.confirmRide(
            confirmRideRequest = confirmRide.asInternalRequest()
        )

        return result.convert(data = result.data?.asExternalResponse() ?: ConfirmRide.empty)
    }
}