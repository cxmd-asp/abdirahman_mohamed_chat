package com.safarione.chat.ui.settings

import android.app.AlertDialog
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.safarione.chat.R
import com.safarione.chat.util.Category
import com.safarione.chat.util.Setting
import com.safarione.chat.util.SettingsScope

@Composable
fun SettingsScope.InfoSection() {
    Category(stringResource(R.string.pref_info)) {
        About()
        ThirdParty()
    }
}

@Composable
private fun SettingsScope.About() {
    val context = LocalContext.current

    Setting(stringResource(R.string.pref_about)) {
        AlertDialog.Builder(context)
            .setTitle(R.string.pref_about)
            .setMessage(R.string.app_desc)
            .setPositiveButton(R.string.ok, null)
            .show()
    }
}

@Composable
private fun SettingsScope.ThirdParty() {
    val context = LocalContext.current

    Setting(stringResource(R.string.pref_third_party)) {
        context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
    }
}