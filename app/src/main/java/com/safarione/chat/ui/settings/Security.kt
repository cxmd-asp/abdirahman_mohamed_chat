package com.safarione.chat.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.safarione.chat.R
import com.safarione.chat.util.Category
import com.safarione.chat.util.Setting
import com.safarione.chat.util.SettingsScope
import com.safarione.chat.util.SwitchSetting
import com.safarione.chat.util.showSingleChoiceDialog

@Composable
fun SettingsScope.SecuritySection() {
    Category(stringResource(R.string.pref_security)) {
        AppLock()
        LockTime()
    }
}

@Composable
private fun SettingsScope.AppLock() {
    val context = LocalContext.current

    SwitchSetting(
        title = stringResource(R.string.pref_app_lock),
        value = Settings.getAppLock(),
        onClick = { Settings.setAppLock(context, it) }
    )
}

@Composable
private fun SettingsScope.LockTime() {
    val context = LocalContext.current

    Setting(
        title = stringResource(R.string.pref_lock_time),
        subtitle = when (val lockTime = Settings.getLockTime()) {
            in 1..5 -> stringResource(R.string.label_minutes, lockTime)
            else -> stringResource(R.string.label_immediately)
        },
        onClick = {
            val lockTime = Settings.getLockTime(context)

            showSingleChoiceDialog(
                context = context,
                choices = listOf(
                    context.getString(R.string.label_immediately),
                    context.getString(R.string.label_minutes, 1),
                    context.getString(R.string.label_minutes, 5),
                    context.getString(R.string.label_minutes, 10),
                    context.getString(R.string.label_minutes, 30)
                ),
                selected = lockTime,
                onSelect = { Settings.setLockTime(context, it) }
            )
        }
    )
}