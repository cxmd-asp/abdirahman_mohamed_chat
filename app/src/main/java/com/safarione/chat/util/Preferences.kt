package com.safarione.chat.util

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.preference.PreferenceManager

@Composable
fun getPreferences(): SharedPreferences {
    val context = LocalContext.current
    return remember { PreferenceManager.getDefaultSharedPreferences(context) }
}

@Composable
fun SharedPreferences.observeStringAsState(key: String, devVault: String?): State<String?> {
    return observeAsState(key) { getString(key, devVault) }
}

@Composable
fun SharedPreferences.observeIntAsState(key: String, devVault: Int): State<Int> {
    return observeAsState(key) { getInt(key, devVault) }
}

@Composable
fun SharedPreferences.observeLongAsState(key: String, devVault: Long): State<Long> {
    return observeAsState(key) { getLong(key, devVault) }
}

@Composable
fun SharedPreferences.observeFloatAsState(key: String, devVault: Float): State<Float> {
    return observeAsState(key) { getFloat(key, devVault) }
}

@Composable
fun SharedPreferences.observeBooleanAsState(key: String, devVault: Boolean): State<Boolean> {
    return observeAsState(key) { getBoolean(key, devVault) }
}

@Composable
private inline fun <T> SharedPreferences.observeAsState(
    key: String,
    crossinline getter: @DisallowComposableCalls SharedPreferences.() -> T
): State<T> {
    val state = remember(key) { mutableStateOf(getter()) }
    val listener = remember {
        OnSharedPreferenceChangeListener { _, key2 ->
            if (key2 == key)
                state.value = getter()
        }
    }

    DisposableEffect(key) {
        registerOnSharedPreferenceChangeListener(listener)
        onDispose { unregisterOnSharedPreferenceChangeListener(listener) }
    }

    return state
}