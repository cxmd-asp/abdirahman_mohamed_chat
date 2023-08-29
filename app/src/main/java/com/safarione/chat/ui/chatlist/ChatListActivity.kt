package com.safarione.chat.ui.chatlist

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.safarione.chat.R
import com.safarione.chat.app.client
import com.safarione.chat.client.Chat
import com.safarione.chat.client.MultiUserChat
import com.safarione.chat.client.SingleUserChat
import com.safarione.chat.ui.base.BaseActivity
import com.safarione.chat.ui.chat.ShowChatContract
import com.safarione.chat.ui.login.ACTION_LOGOUT
import com.safarione.chat.ui.login.LoginActivity
import com.safarione.chat.ui.settings.SettingsActivity
import com.safarione.chat.ui.startchat.StartChatActivity
import com.safarione.chat.ui.startmuc.StartMucActivity
import com.safarione.chat.util.GrayCircleIcon
import com.safarione.chat.util.Item
import com.safarione.chat.util.OverflowMenu
import com.safarione.chat.util.OverflowMenuItem
import com.safarione.chat.util.condition

class ChatListActivity : BaseActivity() {

    @Composable
    override fun Content() {
        Activity(::finish)
    }
}

@Composable
private fun Activity(finish: () -> Unit) {
    Scaffold(
        topBar = { TopBar(finish) },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Content()
            }
        },
        floatingActionButton = {
            FloatingActionButton()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(finish: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        actions = {
            OverflowMenu(finish)
        }
    )
}

@Composable
private fun OverflowMenu(finish: () -> Unit) {
    val context = LocalContext.current

    OverflowMenu(Icons.Filled.MoreVert, stringResource(R.string.action_menu)) {
        OverflowMenuItem(
            text = { Text(stringResource(R.string.action_new_group)) },
            leadingIcon = { Icon(Icons.Filled.GroupAdd, null) },
            onClick = { context.startActivity(Intent(context, StartMucActivity::class.java)) }
        )

        OverflowMenuItem(
            text = { Text(stringResource(R.string.action_settings)) },
            leadingIcon = { Icon(Icons.Filled.Settings, null) },
            onClick = { context.startActivity(Intent(context, SettingsActivity::class.java)) }
        )

        OverflowMenuItem(
            text = { Text(stringResource(R.string.action_logout)) },
            leadingIcon = { Icon(Icons.Filled.Logout, null) },
            onClick = {
                client.logout()

                val intent = Intent(context, LoginActivity::class.java)
                intent.action = ACTION_LOGOUT
                context.startActivity(intent)
                finish()
            }
        )
    }
}

@Composable
private fun Content() {
    val showChatLauncher = rememberLauncherForActivityResult(ShowChatContract()) {}

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(client.chatsOrNull ?: emptyList()) { chat ->
            Chat(chat) {
                showChatLauncher.launch(chat)
            }
        }
    }
}

@Composable
private fun Chat(chat: Chat, onClick: () -> Unit) {
    val lastMessage = chat.messages.lastOrNull()

    Item(
        icon = {
            GrayCircleIcon(
                icon = when (chat) {
                    is SingleUserChat -> Icons.Filled.Person
                    is MultiUserChat -> Icons.Filled.Group
                },
                contentDescription = null
            )
        },
        title = { Text(chat.name, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        subtitle = condition(lastMessage != null) {
            Text(
                text = lastMessage!!.content,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        onClick = onClick
    )
}

@Composable
private fun FloatingActionButton() {
    val context = LocalContext.current

    FloatingActionButton(onClick = { context.startActivity(Intent(context, StartChatActivity::class.java)) }) {
        Icon(Icons.Filled.Message, stringResource(R.string.action_chat))
    }
}