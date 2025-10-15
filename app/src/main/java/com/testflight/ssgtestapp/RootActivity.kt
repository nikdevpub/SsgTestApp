package com.testflight.ssgtestapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.testflight.ssgtestapp.ui.theme.SsgTestAppTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterial3Api
@AndroidEntryPoint
class RootActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            SsgTestAppTheme {
                RootNavigation()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RootActivityPreview() {
    RootNavigation()
}
