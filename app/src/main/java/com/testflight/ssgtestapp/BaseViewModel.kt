package com.testflight.ssgtestapp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID

abstract class BaseViewModel<State : Any, Action : Any, Effect : Any>(
    initialState: State,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        val SAVED_STATE_KEY = "${UUID.randomUUID()}_saved_state_key"
    }

    val state: StateFlow<State> = savedStateHandle.getStateFlow(SAVED_STATE_KEY, initialState)

    private val _effect = Channel<Effect>(Channel.Factory.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    abstract fun onAction(action: Action)

    fun updateState(block: State.() -> State) {
        savedStateHandle[SAVED_STATE_KEY] = state.value.block()
    }

    fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
