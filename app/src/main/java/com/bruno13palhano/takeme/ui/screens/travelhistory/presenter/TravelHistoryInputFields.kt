package com.bruno13palhano.takeme.ui.screens.travelhistory.presenter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class TravelHistoryInputFields {
    var customerId by mutableStateOf("")
        private set

    fun updateCustomerId(customerId: String) {
        this.customerId = customerId
    }

    fun isValid() = customerId.isNotBlank()
}