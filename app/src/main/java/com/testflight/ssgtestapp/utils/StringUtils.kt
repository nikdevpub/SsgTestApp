package com.testflight.ssgtestapp.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import java.io.Serializable

sealed class UiString : Serializable {
    data class Value(val value: String) : UiString(), Serializable
    class Resource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiString(), Serializable

    @Composable
    fun asString(): String {
        return when (this) {
            is Value -> value
            is Resource -> stringResource(resId, *args)
        }
    }

    fun asString(context: Context): String {
        return when (this@UiString) {
            is Value -> value
            is Resource -> context.getString(resId, *args)
        }
    }
}
