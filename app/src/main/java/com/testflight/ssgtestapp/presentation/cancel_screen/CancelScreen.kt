package com.testflight.ssgtestapp.presentation.cancel_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skydoves.cloudy.cloudy
import com.testflight.ssgtestapp.R
import com.testflight.ssgtestapp.ui.components.SecondaryGlassButton

@Composable
fun CancelScreen(
    viewModel: CancelViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var isPressed by remember { mutableStateOf(false) }
    val backgroundPainter = painterResource(R.drawable.background)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            if (effect is CancelContract.Effect.NavigateBack) onNavigateBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val backgroundModifier = Modifier.fillMaxSize()

        Box(modifier = backgroundModifier.background(Color(0xFF4A5B7C)))

        if (isPressed) {
            Image(
                painter = backgroundPainter,
                contentDescription = null,
                modifier = backgroundModifier.cloudy(radius = 300),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = backgroundModifier.background(
                    Brush.verticalGradient(
                        colors = List(2) { Color(0xD10D1A35).copy(alpha = 0.82f) }
                    )
                )
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                SecondaryGlassButton(
                    text = stringResource(R.string.cancel),
                    onClick = { viewModel.onAction(CancelContract.Action.CancelClicked) },
                    onPressChange = { isPressed = it },
                    normalGradientAlphas = 0.9f to 0.7f,
                    pressedGradientAlphas = 0.15f to 0.10f
                )
            }
        }
    }
}
