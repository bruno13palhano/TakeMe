package com.bruno13palhano.takeme.ui.shared.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bruno13palhano.data.model.InternalError
import com.bruno13palhano.takeme.R

internal sealed class InternalErrorMessages(
    val code: InternalError,
    @StringRes val message: Int
) {
    data object Server: InternalErrorMessages(
        code = InternalError.SERVER_ERROR,
        message = R.string.internal_error_server
    )

    data object NotFound: InternalErrorMessages(
        code = InternalError.NO_INTERNET_CONNECTION,
        message = R.string.internal_error_no_internet
    )

    data object Unknown: InternalErrorMessages(
        code = InternalError.UNKNOWN_ERROR,
        message = R.string.internal_error_unknown
    )
}

@Composable
internal fun getInternalErrorMessages(): Map<InternalError, String> {
    return mapOf(
        InternalError.SERVER_ERROR to stringResource(id = InternalErrorMessages.Server.message),
        InternalError.NO_INTERNET_CONNECTION to stringResource(id = InternalErrorMessages.NotFound.message),
        InternalError.UNKNOWN_ERROR to stringResource(id = InternalErrorMessages.Unknown.message)
    )
}