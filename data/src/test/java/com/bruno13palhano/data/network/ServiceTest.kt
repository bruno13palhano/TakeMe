package com.bruno13palhano.data.network

import com.bruno13palhano.data.model.DriverInfo
import com.bruno13palhano.data.remote.model.request.ConfirmRideRequest
import com.bruno13palhano.data.remote.model.request.DriverRequest
import com.bruno13palhano.data.remote.model.response.ConfirmRideResponse
import com.bruno13palhano.data.remote.model.response.RideEstimateResponse
import com.bruno13palhano.data.remote.model.response.RidesResponse
import com.bruno13palhano.data.remote.service.Service
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal class ServiceTest {
    @get:Rule
    val mockWebServer = MockWebServer()

    private val moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(mockWebServer.url("/"))
            .build()
    }

    private val testApiService: Service by lazy { retrofit.create(Service::class.java) }

    @Test
    fun checkIfFindDriverPathIsCorrect() = runTest {
        val rideResponse = RideEstimateResponse(
            origin = null,
            destination = null,
            distance = null,
            duration = null,
            drivers = null,
            routeResponse = null
        )

        val rideJson = moshi.adapter(RideEstimateResponse::class.java).toJson(rideResponse)

        val request = DriverRequest(
            customerId = "123456",
            origin = "ORIGIN",
            destination = "DESTINATION"
        )

        mockWebServer.enqueue(
            response = MockResponse()
                .setBody(rideJson)
                .setResponseCode(200)

        )

        testApiService.findDriver(request = request)

        Assert.assertEquals("/ride/estimate", mockWebServer.takeRequest().path)
    }

    @Test
    fun checkIfConfirmRidePathIsCorrect() = runTest {
        val request = ConfirmRideRequest(
            customerId = "123456",
            origin = "ORIGIN",
            destination = "DESTINATION",
            distance = 10.0,
            duration = "10",
            driver = DriverInfo.empty,
            value = 10.0f
        )

        val confirmResponse = ConfirmRideResponse(success = true)
        val confirmJson = moshi.adapter(ConfirmRideResponse::class.java).toJson(confirmResponse)

        mockWebServer.enqueue(
            response = MockResponse()
                .setBody(confirmJson)
                .setResponseCode(200)
        )

        testApiService.confirmRide(request = request)

        Assert.assertEquals("/ride/confirm", mockWebServer.takeRequest().path)
    }

    @Test
    fun checkIfGetCustomerRidesPathIsCorrect() = runTest {
        val response = RidesResponse(
            customerId = "123456",
            rides = emptyList()
        )

        val responseJson = moshi.adapter(RidesResponse::class.java).toJson(response)

        mockWebServer.enqueue(
            response = MockResponse()
                .setBody(responseJson)
                .setResponseCode(200)
        )

        testApiService.getCustomerRides(customerId = "123456", driverId = 123456)

        Assert.assertEquals(
            "/ride/123456?driver_id&driver_id=123456",
            mockWebServer.takeRequest().path
        )
    }
}