package com.testflight.ssgtestapp.presentation.start_screen

import kotlinx.serialization.Serializable


object StartContract {

    @Serializable
    data class State(
        val isLoading: Boolean = false
    ) : java.io.Serializable

    sealed class Action {
        object StartClicked : Action()
    }

    sealed class Effect {
        object NavigateToCancel : Effect()
    }
}
