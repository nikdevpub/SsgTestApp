package com.testflight.ssgtestapp.presentation.cancel_screen

import androidx.lifecycle.SavedStateHandle
import com.testflight.ssgtestapp.BaseViewModel

class CancelViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<CancelContract.State, CancelContract.Action, CancelContract.Effect>(
    initialState = CancelContract.State(),
    savedStateHandle = savedStateHandle
) {

    override fun onAction(action: CancelContract.Action) {
        when (action) {
            is CancelContract.Action.CancelClicked -> {
                sendEffect(CancelContract.Effect.NavigateBack)
            }
        }
    }
}
