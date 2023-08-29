package com.safarione.chat.ui.login

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.safarione.chat.R
import com.safarione.chat.app.client
import com.safarione.chat.client.LoginResult.LoginFailure
import com.safarione.chat.client.LoginResult.LoginFailure.BadCredentialsFailure
import com.safarione.chat.client.LoginResult.LoginFailure.ConnectionFailure
import com.safarione.chat.client.LoginResult.LoginSuccess
import com.safarione.chat.ui.chatlist.ChatListActivity
import com.safarione.chat.ui.login.LoginViewModel.State.LOGGED_OUT
import com.safarione.chat.ui.login.LoginViewModel.State.PREVIOUSLY_LOGGED_IN
import com.safarione.chat.util.Check
import com.safarione.chat.util.Field
import com.safarione.chat.util.check
import com.safarione.chat.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application, action: String?): ViewModel() {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(application)

    init {
        if (action == ACTION_LOGOUT) {
            preferences.edit {
                remove(KEY_USERNAME)
                remove(KEY_PASSWORD)
            }
        }
    }

    val username = Field(preferences.getString(KEY_USERNAME, "")!!)
    val password = Field(preferences.getString(KEY_PASSWORD, "")!!)
    var showPassword by mutableStateOf(false)

    var state by mutableStateOf(
        if (username.value.isNotEmpty())
            PREVIOUSLY_LOGGED_IN
        else
            LOGGED_OUT
    )

    suspend fun login(context: Context, finish: () -> Unit) {
        check(context,
            Check(username, R.string.error_field_required) { it.isNotBlank() },
            Check(password, R.string.error_field_required) { it.isNotBlank() },
            onFailed = {
                state = LOGGED_OUT
                return
            }
        )

        val result = withContext(Dispatchers.IO) {
            client.login(username.value, password.value)
        }

        when (result) {
            is LoginSuccess -> {
                preferences.edit {
                    putString(KEY_USERNAME, username.value)
                    putString(KEY_PASSWORD, password.value)
                }

                context.startActivity(Intent(context, ChatListActivity::class.java))
                finish()
            }

            is LoginFailure -> {
                state = LOGGED_OUT

                val message = when (result) {
                    is ConnectionFailure -> R.string.error_network_error
                    is BadCredentialsFailure -> R.string.error_number_or_password_wrong
                }
                toast(context, message)
            }
        }
    }

    companion object {
        private const val KEY_USERNAME = "com.safarione.chat.login.key.username"
        private const val KEY_PASSWORD = "com.safarione.chat.login.key.password"
    }

    class Factory(val application: Application, val action: String?): ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(application, action) as T
        }
    }

    enum class State {
        LOGGED_OUT, LOGGING_IN, PREVIOUSLY_LOGGED_IN
    }
}
