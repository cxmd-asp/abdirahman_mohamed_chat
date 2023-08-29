package com.safarione.chat.ui.chat

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.safarione.chat.R
import com.safarione.chat.app.client
import com.safarione.chat.client.Chat
import com.safarione.chat.client.Message
import com.safarione.chat.client.MultiUserChat
import com.safarione.chat.client.SingleUserChat
import com.safarione.chat.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class ChatViewModel(val chatName: String, val multiUser: Boolean): ViewModel() {

    val username: String
        get() = client.username

    private val chat: Chat?
        get() {
            return if (multiUser)
                client.chats.find { it is MultiUserChat && it.name == chatName }
            else
                client.chats.find { it is SingleUserChat && it.name == chatName }
        }

    val messages: List<Message>
        get() = chat?.messages ?: emptyList()

    var message: String by mutableStateOf("")

    private val mutex = Mutex()

    suspend fun send(context: Context) {
        mutex.withLock {
            if (message.isEmpty())
                return@withLock

            val chat = chat

            try {
                withContext(Dispatchers.IO) {
                    if (chat != null)
                        client.send(message, chat)
                    else
                        client.sendMessage(message, chatName)
                }

                message = ""
            }
            catch (e: Exception) {
                e.printStackTrace()
                toast(context, R.string.error_occurred)
            }
        }
    }

    class Factory(val chatName: String, val multiUser: Boolean): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatViewModel(chatName, multiUser) as T
        }
    }
}