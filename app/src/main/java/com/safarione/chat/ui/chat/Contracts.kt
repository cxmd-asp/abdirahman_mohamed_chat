package com.safarione.chat.ui.chat

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.safarione.chat.client.Chat
import com.safarione.chat.client.MultiUserChat

class CreateNewChatContract: ActivityResultContract<String, Unit>() {

    override fun createIntent(context: Context, input: String): Intent {
        return Intent(context, ChatActivity::class.java).apply {
            putExtra(KEY_CHAT, input)
            putExtra(KEY_MULTI_USER, false)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?) {

    }
}

class ShowChatContract: ActivityResultContract<Chat, Unit>() {

    override fun createIntent(context: Context, input: Chat): Intent {
        return Intent(context, ChatActivity::class.java).apply {
            putExtra(KEY_CHAT, input.name)
            putExtra(KEY_MULTI_USER, input is MultiUserChat)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?) {

    }
}

const val KEY_CHAT = "com.safarione.chat.key.chat"
const val KEY_MULTI_USER = "com.safarione.chat.key.multi_user"
