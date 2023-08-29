package com.safarione.chat.client

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.Room
import com.safarione.chat.client.LoginResult.LoginSuccess
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart

class Client(
    val context: Application,
    val config: Config
) {

    private var state: State by mutableStateOf(LoggedOut)

    val loggedIn: Boolean
        get() = state is LoggedIn

    private val data: LoggedIn
        get() = state as? LoggedIn ?: throw IllegalStateException("The user isn't logged in")

    /**
     * The username of the logged in user.
     */
    val username: String
        get() = data.username

    val chats: List<Chat>
        get() = data.chats

    val chatsOrNull: List<Chat>?
        get() = (state as? LoggedIn)?.chats

    val isOnline: Boolean
        get() = data.connection is Online

    fun login(username: String, password: String): LoginResult {
        check(state == LoggedOut) { "The client isn't logged out" }

        val (result, connection) = login(config, username, password)
        if (result is LoginSuccess) {
            init(username, password, connection!!)
        }

        return result
    }

    fun setLogin(username: String, password: String) {
        init(username, password, null)
    }

    fun logout() {
        state.close()
        state = LoggedOut
    }

    private fun init(username: String, password: String, connection: XMPPTCPConnection?) {
        val state = LoggedIn(
            config = config,
            username = username,
            password = password,
            smackConnection = connection,
            database = Room.databaseBuilder(context, Database::class.java, "database-$username").build()
        )
        post { this.state = state }
    }

    fun createGroup(name: String, members: List<String>) {
        data.createGroup(name, members)
    }

    fun getMembers(group: String): List<String> {
        return data.getMembers(group)
    }

    fun sendMessage(message: String, to: String) {
        data.sendMessage(message, to)
    }

    fun sendGroupMessage(message: String, group: String) {
        data.sendGroupMessage(message, group)
    }

    fun send(message: String, chat: Chat) {
        when (chat) {
            is SingleUserChat -> sendMessage(message, chat.name)
            is MultiUserChat -> sendGroupMessage(message, chat.name)
        }
    }
}