package com.safarione.chat.ui.login

import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.safarione.chat.R
import com.safarione.chat.app.client
import com.safarione.chat.ui.base.BaseActivity
import com.safarione.chat.ui.chatlist.ChatListActivity
import com.safarione.chat.ui.login.LoginViewModel.State.LOGGED_OUT
import com.safarione.chat.ui.login.LoginViewModel.State.LOGGING_IN
import com.safarione.chat.ui.login.LoginViewModel.State.PREVIOUSLY_LOGGED_IN
import com.safarione.chat.util.CustomOutlinedTextField
import com.safarione.chat.util.Loading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginActivity : BaseActivity() {

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModel.Factory(application, intent.action)
    }

    @Composable
    override fun Content() {
        Activity(viewModel, ::finish)
    }
}

const val ACTION_LOGOUT = "com.safarione.chat.action.logout"

@Composable
private fun Activity(viewModel: LoginViewModel, finish: () -> Unit) {
    Scaffold(
        topBar = { TopBar() },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Content(viewModel, finish)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(R.string.app_name)) }
    )
}

@Composable
private fun Content(viewModel: LoginViewModel, finish: () -> Unit) {
    val context = LocalContext.current

    if (client.loggedIn) {
        LaunchedEffect(Unit) {
            context.startActivity(Intent(context, ChatListActivity::class.java))
            finish()
        }

        return
    }

    when (viewModel.state) {
        LOGGED_OUT -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                CustomOutlinedTextField(
                    modifier = Modifier.width(280.dp),
                    field = viewModel.username,
                    label = { Text(stringResource(R.string.label_number)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )

                CustomOutlinedTextField(
                    modifier = Modifier.width(280.dp),
                    field = viewModel.password,
                    label = { Text(stringResource(R.string.label_password)) },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.showPassword = !viewModel.showPassword }) {
                            if (viewModel.showPassword)
                                Icon(Icons.Filled.Visibility, stringResource(R.string.action_hide_password))
                            else
                                Icon(Icons.Filled.VisibilityOff, stringResource(R.string.action_show_password))
                        }
                    },
                    visualTransformation = if (!viewModel.showPassword) PasswordVisualTransformation() else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions { viewModel.state = LOGGING_IN },
                    singleLine = true
                )

                Button(
                    modifier = Modifier.width(200.dp),
                    onClick = { viewModel.state = LOGGING_IN }
                ) {
                    Text(stringResource(R.string.action_login))
                }

                Spacer(modifier = Modifier.weight(2f))
            }
        }
        LOGGING_IN -> {
            Loading {
                viewModel.login(context, finish)
            }
        }
        PREVIOUSLY_LOGGED_IN -> {
            Loading {
                withContext(Dispatchers.IO) {
                    client.setLogin(viewModel.username.value, viewModel.password.value)
                }
                context.startActivity(Intent(context, ChatListActivity::class.java))
                finish()
            }
        }
    }
}