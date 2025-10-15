package com.testflight.ssgtestapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

@Composable
fun <Effect> SingleLaunchedEffect(
    effectFlow: Flow<Effect>,
    lifecycle: LifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    coroutineContext: CoroutineContext = Dispatchers.Main.immediate,
    onEffect: suspend CoroutineScope.(effect: Effect) -> Unit
) {
    LaunchedEffect(effectFlow) {
        effectFlow
            .flowWithLifecycle(lifecycle.lifecycle, minActiveState)
            .flowOn(coroutineContext)
            .collect {
                onEffect(it)
            }
    }
}
