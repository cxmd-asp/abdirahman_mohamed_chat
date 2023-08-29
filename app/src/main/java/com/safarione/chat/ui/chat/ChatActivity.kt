package com.safarione.chat.ui.chat

import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.safarione.chat.R
import com.safarione.chat.app.client
import com.safarione.chat.ui.base.BaseActivity
import com.safarione.chat.util.MessageBar
import com.safarione.chat.util.Messages
import com.safarione.chat.util.toast
import kotlinx.coroutines.launch

class ChatActivity : BaseActivity() {

    private val viewModel: ChatViewModel by viewModels {
        val number = intent.getStringExtra(KEY_CHAT) ?: throw IllegalStateException("No chat was passed")
        val multiUser = intent.getBooleanExtra(KEY_MULTI_USER, false)
        ChatViewModel.Factory(number, multiUser)
    }

    @Composable
    override fun Content() {
        Activity(viewModel, ::finish)
    }
}

@Composable
private fun Activity(viewModel: ChatViewModel, finish: () -> Unit) {
    Scaffold(
        topBar = { TopBar(viewModel, finish) },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Content(viewModel)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(viewModel: ChatViewModel, finish: () -> Unit) {
    TopAppBar(
        title = { Text(viewModel.chatName) },
        navigationIcon = {
            IconButton(onClick = finish) {
                Icon(Icons.Filled.ArrowBack, stringResource(R.string.action_back))
            }
        }
    )
}

@Composable
private fun Content(viewModel: ChatViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            Messages(
                messages = viewModel.messages,
                username = viewModel.username,
                multiUserChat = viewModel.multiUser
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (!client.isOnline) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(5.dp))
                Text(stringResource(R.string.error_not_online))
            }
        }

        MessageBar(
            message = viewModel.message,
            onMessageChange = { viewModel.message = it },
            onSend = {
                if (!client.isOnline)
                    toast(context, R.string.error_not_online)
                else
                    scope.launch { viewModel.send(context) }
            }
        )
    }
}