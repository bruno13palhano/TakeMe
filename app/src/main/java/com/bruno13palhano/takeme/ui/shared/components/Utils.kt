package com.bruno13palhano.takeme.ui.shared.components

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal fun formatDate(date: String): String {
    return DateTimeFormatter
        .ofPattern("EEEE MMMM u HH:mm")
        .format(LocalDateTime.parse(date))
}