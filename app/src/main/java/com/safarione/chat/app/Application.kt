package com.safarione.chat.app

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.safarione.chat.client.Client
import com.safarione.chat.client.Config
import com.safarione.chat.ui.settings.Settings
import com.safarione.chat.util.AppLock
import org.jivesoftware.smack.android.AndroidSmackInitializer

class Application: android.app.Application() {

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get()
            .lifecycle
            .addObserver(AppLifecycleObserver(this))

        AndroidSmackInitializer.initialize(this)

        /*val localConfig = Config(
            host = "192.168.0.73",
            domain = "localhost",
            mucDomain = "muc.localhost"
        )*/

        val safarioneConfig = Config(
            host = "uatchat2.waafi.com",
            domain = "uatchat2.waafi.com",
            mucDomain = "muclight.uatchat2.waafi.com"
        )

        _client = Client(this, safarioneConfig)
    }
}

private class AppLifecycleObserver(private val application: Application): DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        AppLock.onAppStarted(application, Settings.getAppLock(application), Settings.getLockTimeInMillis(application))
    }

    override fun onStop(owner: LifecycleOwner) {
        AppLock.onAppStopped(application)
    }
}

private var _client: Client? = null

val client: Client
    get() = _client ?: throw AssertionError("The client hasn't been initialized")
