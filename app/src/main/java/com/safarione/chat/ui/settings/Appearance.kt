package com.safarione.chat.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.safarione.chat.R
import com.safarione.chat.ui.settings.DarkMode.AUTO
import com.safarione.chat.ui.settings.DarkMode.OFF
import com.safarione.chat.ui.settings.DarkMode.ON
import com.safarione.chat.util.Category
import com.safarione.chat.util.Setting
import com.safarione.chat.util.SettingsScope
import com.safarione.chat.util.showSingleChoiceDialog

@Composable
fun SettingsScope.AppearanceSection() {
    Category(stringResource(R.string.pref_appearance)) {
        DarkTheme()
    }
}

@Composable
private fun SettingsScope.DarkTheme() {
    val context = LocalContext.current

    Setting(
        title = stringResource(R.string.pref_dark_theme),
        subtitle = when (Settings.getDarkMode()) {
            AUTO -> stringResource(R.string.pref_auto)
            ON -> stringResource(R.string.pref_on)
            OFF -> stringResource(R.string.pref_off)
        },
        onClick = {
            showSingleChoiceDialog(
                context = context,
                choices = listOf(
                    context.getString(R.string.pref_auto),
                    context.getString(R.string.pref_on),
                    context.getString(R.string.pref_off)
                ),
                selected = when (Settings.getDarkMode(context)) {
                    AUTO -> 0
                    ON -> 1
                    OFF -> 2
                },
                onSelect = {
                    val value = when (it) {
                        1 -> ON
                        2 -> OFF
                        else -> AUTO
                    }
                    Settings.setDarkMode(context, value)
                }
            )
        }
    )
}