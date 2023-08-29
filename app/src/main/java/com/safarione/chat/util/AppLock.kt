package com.safarione.chat.util

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceManager
import com.safarione.chat.R
import com.safarione.chat.util.AppLock.State.FAILED
import com.safarione.chat.util.AppLock.State.LOCKED
import com.safarione.chat.util.AppLock.State.UNDEFINED
import com.safarione.chat.util.AppLock.State.UNLOCKED

object AppLock {

    private var state by mutableStateOf(UNDEFINED)
    private const val KEY_STOP_TIME = "com.alif.util.lock.key.stop_time"

    /**
     * Should be called whenever the app is opened.
     * @param lockEnabled whether the app has locking enabled e.g. in the settings
     * @param lockTime how many milliseconds should elapse after the user leaves the app before the app
     * is locked
     */
    fun onAppStarted(context: Context, lockEnabled: Boolean, lockTime: Long) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val stopTime = preferences.getLong(KEY_STOP_TIME, 0L)

        state = if (lockEnabled && (System.currentTimeMillis() - stopTime) > lockTime)
            LOCKED
        else
            UNLOCKED
    }

    fun onAppStopped(context: Context) {
        if (state == UNLOCKED) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            preferences.edit { putLong(KEY_STOP_TIME, System.currentTimeMillis()) }
        }

        state = UNDEFINED
    }

    fun isLocked(): Boolean {
        return state == LOCKED
    }

    fun check(activity: ComponentActivity, lockEnabled: Boolean, lockTime: Long) {
        if (state == UNDEFINED) {
            onAppStarted(activity, lockEnabled, lockTime)
        }
    }

    fun unlock() {
        state = UNLOCKED
    }

    fun fail() {
        state = FAILED
    }

    private enum class State {
        UNDEFINED, LOCKED, UNLOCKED, FAILED
    }
}

@Composable
fun AppLock(
    activity: FragmentActivity,
    content: @Composable () -> Unit
) {
    AppLock(
        activity = activity,
        locked = AppLock.isLocked(),
        onUnlocked = { AppLock.unlock() },
        content = content
    )
}

@Composable
fun AppLock(
    activity: FragmentActivity,
    locked: Boolean,
    onUnlocked: () -> Unit,
    content: @Composable () -> Unit
) {
    if (locked)
        LockScreen(activity, onUnlocked)
    else
        content()
}

@Composable
private fun LockScreen(
    activity: FragmentActivity,
    onUnlocked: () -> Unit
) {
    var attempts by remember { mutableStateOf(0) }
    val canAuthenticate = remember(attempts) { isDeviceSecure(activity) }
    var error: String? by remember(attempts) { mutableStateOf(null) }

    DisposableEffect(Unit) {
        var firstCall = true
        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                if (firstCall)
                    firstCall = false
                else
                    ++attempts
            }
        }

        activity.lifecycle.addObserver(observer)
        onDispose { activity.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Lock,
            contentDescription = null,
            modifier = Modifier
                .padding(vertical = 20.dp)
                .size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(stringResource(R.string.title_app_locked), fontSize = 28.sp, fontWeight = FontWeight.Medium)

        Spacer(Modifier.weight(3f))

        Icon(
            imageVector = if (error == null) Icons.Filled.Fingerprint else Icons.Filled.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = if (error == null) LocalContentColor.current else Color.Red
        )

        if (!canAuthenticate || error != null) {
            Text(
                text = if (!canAuthenticate) stringResource(R.string.error_set_lock_screen) else error!!,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.weight(1f))

        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

        if (!canAuthenticate && SDK_INT >= 30) {
            Button(onClick = {
                val intent = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
                intent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, DEVICE_CREDENTIAL)
                launcher.launch(intent)
            }) {
                Text(stringResource(R.string.action_set_lock_screen))
            }
        }

        Spacer(Modifier.weight(5f))
    }

    if (canAuthenticate) {
        LaunchedEffect(attempts) {
            unlock(
                activity = activity,
                title = activity.getString(R.string.title_unlock_app),
                onSuccess = onUnlocked,
                onFailure = {
                    error = it ?: activity.getString(R.string.error_authentication_failed)
                }
            )
        }
    }
}

private fun isDeviceSecure(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    return biometricManager.canAuthenticate(BIOMETRIC_WEAK or DEVICE_CREDENTIAL) == BIOMETRIC_SUCCESS
}

private fun unlock(
    activity: FragmentActivity,
    title: String,
    onSuccess: () -> Unit,
    onFailure: (String?) -> Unit
) {
    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            onFailure(errString.toString())
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            onSuccess()
        }

        override fun onAuthenticationFailed() {
            onFailure(null)
        }
    }

    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(activity, executor, callback)

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(title)
        .setAllowedAuthenticators(BIOMETRIC_WEAK or DEVICE_CREDENTIAL)
        .build()

    biometricPrompt.authenticate(promptInfo)
}