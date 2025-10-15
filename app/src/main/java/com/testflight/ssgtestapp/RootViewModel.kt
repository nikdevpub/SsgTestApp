package com.testflight.ssgtestapp

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<RootContract.State, RootContract.Action, RootContract.Effect>(
    RootContract.State,
    savedStateHandle
) {

    override fun onAction(action: RootContract.Action) {}
}


object RootContract : Serializable {
    private fun readResolve(): Any = RootContract

    @kotlinx.serialization.Serializable
    data object State : Serializable {
        private fun readResolve(): Any = State
    }

    sealed class Action

    sealed class Effect
}
