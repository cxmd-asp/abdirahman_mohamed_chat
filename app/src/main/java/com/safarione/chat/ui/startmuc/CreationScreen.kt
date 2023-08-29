package com.safarione.chat.ui.startmuc

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.safarione.chat.R
import com.safarione.chat.app.client
import com.safarione.chat.client.MultiUserChat
import com.safarione.chat.ui.chat.ShowChatContract
import com.safarione.chat.util.Loading
import com.safarione.chat.util.NavScreen
import com.safarione.chat.util.Navigator
import com.safarione.chat.util.finish
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CreationScreen: NavScreen<StartMucViewModel, Unit>() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun TopBar(viewModel: StartMucViewModel, navigator: Navigator<Unit>) {
        TopAppBar(
            title = { Text(stringResource(R.string.app_name)) }
        )
    }

    @Composable
    override fun Content(viewModel: StartMucViewModel, navigator: Navigator<Unit>) {
        val showChatLauncher = rememberLauncherForActivityResult(ShowChatContract()) {}

        Loading { error ->
            try {
                withContext(Dispatchers.IO) {
                    client.createGroup(
                        name = viewModel.name,
                        members = viewModel.members.keys.toList()
                    )
                }

                var chat: MultiUserChat? = null
                for (i in 0 until 30) {
                    chat = client.chats
                        .mapNotNull { it as? MultiUserChat }
                        .filter { it.name == viewModel.name }
                        .maxByOrNull { it.timeCreated }

                    if (chat != null)
                        break
                }

                if (chat != null)
                    showChatLauncher.launch(chat)

                navigator.finish()
            }
            catch (e: Exception) {
                e.printStackTrace()
                error(R.string.error_occurred)
            }
        }
    }
}