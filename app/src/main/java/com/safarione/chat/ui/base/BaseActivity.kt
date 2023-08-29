package com.safarione.chat.ui.base

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.safarione.chat.R
import com.safarione.chat.app.client
import com.safarione.chat.ui.login.LoginActivity
import com.safarione.chat.ui.settings.Settings
import com.safarione.chat.ui.theme.AppTheme
import com.safarione.chat.util.AppLock

abstract class BaseActivity : FragmentActivity() {

    final override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (!client.loggedIn && this !is LoginActivity) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppLock(
                        activity = this,
                        content = { Content() }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        AppLock.check(this, Settings.getAppLock(this), Settings.getLockTimeInMillis(this))
    }

    @Composable
    abstract fun Content()
}