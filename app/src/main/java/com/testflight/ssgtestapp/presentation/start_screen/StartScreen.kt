package com.testflight.ssgtestapp.presentation.start_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.testflight.ssgtestapp.R
import com.testflight.ssgtestapp.ui.components.GlassButton

@Composable
fun StartScreen(
    viewModel: StartViewModel = viewModel(),
    onNavigateToCancel: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is StartContract.Effect.NavigateToCancel -> {
                    onNavigateToCancel()
                }
            }
        }
    }

    StartScreenContent(
        isLoading = state.isLoading,
        onStartClick = { viewModel.onAction(StartContract.Action.StartClicked) }
    )
}

@Composable
private fun StartScreenContent(
    isLoading: Boolean,
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            GlassButton(
                modifier = Modifier.defaultMinSize(224.dp, 236.dp),
                text = stringResource(id = R.string.start),
                onClick = onStartClick
            )
        }
    }
}

@Preview(
    name = "Small Phone 480x854",
    showBackground = true,
    device = "spec:width=480dp,height=854dp,dpi=240"
)
@Composable
private fun StartScreenSmallPhonePreview() {
    StartScreenContent(isLoading = false, onStartClick = {})
}

@Preview(name = "Pixel 5", showBackground = true, device = Devices.PIXEL_5)
@Composable
private fun StartScreenPixel5Preview() {
    StartScreenContent(isLoading = false, onStartClick = {})
}

@Preview(name = "Tablet 10\"", showBackground = true, device = Devices.PIXEL_C)
@Composable
private fun StartScreenTablet10Preview() {
    StartScreenContent(isLoading = false, onStartClick = {})
}

// Button Only
@Preview(name = "Glass Button", showBackground = true)
@Composable
private fun GlassButtonPreview() {
    GlassButton(
        modifier = Modifier.defaultMinSize(224.dp, 236.dp),
        text = "Start",
        onClick = {}
    )
}
