package com.bruno13palhano.takeme.ui.screens.home.presenter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class HomeInputFields {
    var customerId by mutableStateOf("CT01")
        private set

    var origin by mutableStateOf("Av. Brasil, 2033 - Jardim America, São Paulo -SP, 01431-001")
        private set

    var destination by mutableStateOf("Av. Paulista, 1538 - Bela Vista, São Paulo - SP, 01310-200")
        private set

    fun updateCustomerId(customerId: String) {
        this.customerId = customerId
    }

    fun updateOrigin(origin: String) {
        this.origin = origin
    }

    fun updateDestination(destination: String) {
        this.destination = destination
    }

    fun isValid(): Boolean {
        return customerId.isNotBlank() && origin.isNotBlank() && destination.isNotBlank()
    }
}