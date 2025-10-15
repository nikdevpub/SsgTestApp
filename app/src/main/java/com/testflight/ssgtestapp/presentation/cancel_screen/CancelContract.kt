package com.testflight.ssgtestapp.presentation.cancel_screen

import kotlinx.serialization.Serializable

object CancelContract {

    @Serializable
    data class State(
        val isLoading: Boolean = false
    ) : java.io.Serializable

    sealed class Action {
        object CancelClicked : Action()
    }

    sealed class Effect {
        object NavigateBack : Effect()
    }
}
