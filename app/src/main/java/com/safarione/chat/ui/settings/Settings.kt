package com.safarione.chat.ui.settings

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.safarione.chat.ui.settings.DarkMode.AUTO
import com.safarione.chat.ui.settings.DarkMode.OFF
import com.safarione.chat.ui.settings.DarkMode.ON
import com.safarione.chat.util.getPreferences
import com.safarione.chat.util.observeBooleanAsState
import com.safarione.chat.util.observeIntAsState
import com.safarione.chat.util.observeStringAsState

object Settings {

    private const val PREF_DARK_MODE = "com.safarione.chat.pref.dark_mode"

    private const val PREF_APP_LOCK = "com.safarione.chat.pref.app_lock"
    private const val PREF_APP_LOCK_DEFAULT = false

    private const val PREF_LOCK_TIME = "com.safarione.chat.pref.lock_time"
    private const val PREF_LOCK_TIME_DEFAULT = 0

    @Composable
    fun inDarkTheme(): Boolean {
        return when (getDarkMode()) {
            AUTO -> isSystemInDarkTheme()
            ON -> true
            OFF -> false
        }
    }

    @Composable
    fun getDarkMode(): DarkMode {
        val value by getPreferences().observeStringAsState(PREF_DARK_MODE, null)
        return DarkMode.from(value)
    }

    fun getDarkMode(context: Context): DarkMode {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return DarkMode.from(preferences.getString(PREF_DARK_MODE, null))
    }

    fun setDarkMode(context: Context, value: DarkMode) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit { putString(PREF_DARK_MODE, value.value) }
    }

    fun getAppLock(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(PREF_APP_LOCK, PREF_APP_LOCK_DEFAULT)
    }

    @Composable
    fun getAppLock(): Boolean {
        val value by getPreferences().observeBooleanAsState(PREF_APP_LOCK, PREF_APP_LOCK_DEFAULT)
        return value
    }

    fun setAppLock(context: Context, value: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit { putBoolean(PREF_APP_LOCK, value) }
    }

    fun getLockTimeInMillis(context: Context): Long {
        return getLockTime(context) * 60_000L
    }

    fun getLockTime(context: Context): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(PREF_LOCK_TIME, null)?.toIntOrNull() ?: PREF_LOCK_TIME_DEFAULT
    }

    @Composable
    fun getLockTime(): Int {
        val value by getPreferences().observeStringAsState(PREF_LOCK_TIME, null)
        return value?.toIntOrNull() ?: PREF_LOCK_TIME_DEFAULT
    }

    fun setLockTime(context: Context, value: Int) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit { putString(PREF_LOCK_TIME, value.toString()) }
    }
}

enum class DarkMode(val value: String) {
    AUTO("auto"),
    ON("on"),
    OFF("off");

    companion object {
        fun from(value: String?): DarkMode {
            return when (value) {
                ON.value -> ON
                OFF.value -> OFF
                else -> AUTO
            }
        }
    }
}