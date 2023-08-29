package com.safarione.chat.client

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.safarione.chat.client.LoginResult.LoginSuccess
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jxmpp.jid.impl.JidCreate

internal sealed class State {
    open fun close() {}
}

internal data object LoggedOut : State()

internal class LoggedIn(
    val config: Config,
    val username: String,
    val password: String,
    smackConnection: XMPPTCPConnection?,
    val database: Database
) : State(), ConnectionListener {

    private val _chats = mutableStateListOf<Chat>()

    val chats: List<Chat>
        get() = _chats

    init {
        for (groupChat in database.groupChatDao().getAll()) {
            _chats.add(
                MutableMultiUserChat(
                    name = groupChat.name,
                    timeCreated = groupChat.timeCreated
                )
            )
        }

        for (message in database.messageDao().getAll().sortedBy { it.time }) {
            if (message.groupChat == null) {
                val chatName = if (message.from != username) message.from else message.to!!
                var chat = _chats.find { it is MutableSingleUserChat && it.name == chatName } as MutableSingleUserChat?
                if (chat == null) {
                    chat = MutableSingleUserChat(chatName)
                    _chats.add(chat)
                }

                chat.messages.add(
                    Message(
                        id = message.id,
                        content = message.content,
                        from = message.from,
                        time = message.time
                    )
                )
            }
            else {
                val chat = _chats.find { it is MutableMultiUserChat && it.name == message.groupChat } as MutableMultiUserChat? ?: continue
                chat.messages.add(
                    Message(
                        id = message.id,
                        content = message.content,
                        from = message.from,
                        time = message.time
                    )
                )
            }
        }

        sortChats()
    }

    var connection by mutableStateOf(
        if (smackConnection == null)
            Offline
        else
            Online(config, username, smackConnection, this, chats)
    )
        private set

    @Transient
    private var closed = false

    private val thread = Thread {
        fun reconnect() {
            val (result, smackConnection) = login(config, username, password)

            val newConnection = if (result is LoginSuccess)
                Online(config, username, smackConnection!!, this, chats)
            else
                Offline

            postAndWait { connection = newConnection }
        }

        while (!closed) {
            try {
                when (val connection = connection) {
                    is Offline -> {
                        reconnect()
                    }
                    is Online -> {
                        if (!connection.isAlive())
                            reconnect()
                        else
                            connection.ping()
                    }
                }

                if (closed)
                    return@Thread

                try {
                    Thread.sleep(3000)
                }
                catch (e: InterruptedException) {}
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }.apply { start() }

    fun createGroup(name: String, members: List<String>) {
        when (val connection = connection) {
            is Offline -> throw OfflineException()
            is Online -> connection.createGroup(name, members)
        }
    }

    fun getMembers(group: String): List<String> {
        return when (val connection = connection) {
            is Offline -> throw OfflineException()
            is Online -> connection.getMembers(group)
        }
    }

    fun sendMessage(message: String, to: String) {
        when (val connection = connection) {
            is Offline -> throw OfflineException()
            is Online -> connection.sendMessage(message, to)
        }
    }

    fun sendGroupMessage(message: String, group: String) {
        when (val connection = connection) {
            is Offline -> throw OfflineException()
            is Online -> connection.sendGroupMessage(message, group)
        }
    }

    override fun onMessageReceived(id: String, message: String, from: String) {
        if (database.messageDao().get(id) != null)
            return //duplicate message

        val time = System.currentTimeMillis()

        database.messageDao().insert(
            MessageRecord(
                id = id,
                content = message,
                from = from,
                to = username,
                time = time,
                groupChat = null
            )
        )

        post {
            var chat = chats.find { it is MutableSingleUserChat && it.name == from } as MutableSingleUserChat?
            if (chat == null) {
                chat = MutableSingleUserChat(from)
                _chats.add(chat)
            }

            chat.messages.add(
                Message(
                    id = id,
                    content = message,
                    from = from,
                    time = time
                )
            )

            sortChats()
        }
    }

    override fun onMessageSent(id: String, message: String, to: String) {
        val time = System.currentTimeMillis()

        database.messageDao().insert(
            MessageRecord(
                id = id,
                content = message,
                from = username,
                to = to,
                time = time,
                groupChat = null
            )
        )

        post {
            var chat = chats.find { it is MutableSingleUserChat && it.name == to } as MutableSingleUserChat?
            if (chat == null) {
                chat = MutableSingleUserChat(to)
                _chats.add(chat)
            }

            chat.messages.add(
                Message(
                    id = id,
                    content = message,
                    from = username,
                    time = time
                )
            )

            sortChats()
        }
    }

    override fun onGroupJoined(group: String) {
        val time = System.currentTimeMillis()
        database.groupChatDao().insert(GroupChatRecord(group, time))
        post {
            _chats.add(MutableMultiUserChat(group, time))
            sortChats()
        }
    }

    override fun onGroupMessageSent(id: String, message: String, from: String, group: String) {
        if (message.isEmpty())
            return

        if (database.messageDao().get(id) != null)
            return //duplicate message

        val time = System.currentTimeMillis()

        database.messageDao().insert(
            MessageRecord(
                id = id,
                content = message,
                from = from,
                to = null,
                time = time,
                groupChat = group
            )
        )

        post {
            val chat = chats.find { it is MutableMultiUserChat && it.name == group } as MutableMultiUserChat? ?: return@post

            chat.messages.add(
                Message(
                    id = id,
                    content = message,
                    from = from,
                    time = time
                )
            )

            sortChats()
        }
    }

    override fun close() {
        closed = true
        thread.interrupt()
    }

    private fun sortChats() {
        _chats.sortByDescending {
            when (it) {
                is SingleUserChat -> it.messages.lastOrNull()?.time ?: 0L
                is MultiUserChat -> it.messages.lastOrNull()?.time ?: it.timeCreated
            }
        }
    }

    private class MutableSingleUserChat(override val name: String) : SingleUserChat() {
        override val messages = mutableStateListOf<Message>()
    }

    private inner class MutableMultiUserChat(
        override val name: String,
        override val timeCreated: Long
    ) : MultiUserChat() {
        override val messages = mutableStateListOf<Message>()
    }
}