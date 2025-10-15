package com.testflight.ssgtestapp.presentation.start_screen

import androidx.lifecycle.SavedStateHandle
import com.testflight.ssgtestapp.BaseViewModel

class StartViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<StartContract.State, StartContract.Action, StartContract.Effect>(
    initialState = StartContract.State(),
    savedStateHandle = savedStateHandle
) {

    override fun onAction(action: StartContract.Action) {
        when (action) {
            is StartContract.Action.StartClicked -> {
                sendEffect(StartContract.Effect.NavigateToCancel)
            }
        }
    }
}
