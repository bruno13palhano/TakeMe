package com.bruno13palhano.takeme.ui.shared.base

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.bruno13palhano.takeme.ui.screens.di.PresenterEntryPoint
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerPresenter
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomePresenter
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryPresenter
import dagger.hilt.EntryPoints
import kotlinx.coroutines.flow.Flow

private fun View.isKeyboardOpen(): Boolean {
    val rect = Rect()
    getWindowVisibleDisplayFrame(rect)
    val screenHeight = rootView.height
    val keypadHeight = screenHeight - rect.bottom
    return keypadHeight > screenHeight * 0.15
}

@Composable
private fun rememberIsKeyboardOpen(): State<Boolean> {
    val view = LocalView.current

    return produceState(initialValue = view.isKeyboardOpen()) {
        val viewTreeObserver = view.viewTreeObserver
        val listener = ViewTreeObserver.OnGlobalLayoutListener { value = view.isKeyboardOpen() }
        viewTreeObserver.addOnGlobalLayoutListener(listener)

        awaitDispose { viewTreeObserver.removeOnGlobalLayoutListener(listener) }
    }
}

fun Modifier.clearFocusOnKeyboardDismiss(): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    var keyboardAppearedSinceLastFocused by remember { mutableStateOf(false) }

    if (isFocused) {
        val isKeyboardOpen by rememberIsKeyboardOpen()

        val focusManager = LocalFocusManager.current
        LaunchedEffect(key1 = isKeyboardOpen) {
            if (isKeyboardOpen) {
                keyboardAppearedSinceLastFocused = true
            } else if (keyboardAppearedSinceLastFocused) {
                focusManager.clearFocus()
            }
        }
    }

    onFocusEvent {
        if (isFocused != it.isFocused) {
            isFocused = it.isFocused
            if (isFocused) {
                keyboardAppearedSinceLastFocused = false
            }
        }
    }
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}

@Composable
fun <T> rememberFlowWithLifecycle(
    flow: Flow<T>,
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): Flow<T> = remember(flow, lifecycle) {
    flow.flowWithLifecycle(
        lifecycle = lifecycle,
        minActiveState = minActiveState
    )
}

@Composable
internal fun <T : BasePresenter<*, *, *, *>> rememberPresenter(presenter: Class<T>): T {
    val applicationContext = LocalContext.current.applicationContext

    if (presenter.isAssignableFrom(HomePresenter::class.java)) {
        return remember(applicationContext) {
            EntryPoints.get(
                applicationContext,
                PresenterEntryPoint::class.java
            ).homePresenter
        } as T
    } else if(presenter.isAssignableFrom(DriverPickerPresenter::class.java)) {
        return remember(applicationContext) {
            EntryPoints.get(
                applicationContext,
                PresenterEntryPoint::class.java
            ).driverPickerPresenter
        } as T
    } else if(presenter.isAssignableFrom(TravelHistoryPresenter::class.java)) {
        return remember(applicationContext) {
            EntryPoints.get(
                applicationContext,
                PresenterEntryPoint::class.java
            ).travelHistoryPresenter
        } as T
    } else {
        throw IllegalArgumentException("Invalid presenter class")
    }
}