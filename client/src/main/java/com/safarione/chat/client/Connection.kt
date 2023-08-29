package com.safarione.chat.client

import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Presence.Type.available
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import org.jivesoftware.smackx.muc.MultiUserChat as SmackMultiUserChat

internal sealed class Connection

internal data object Offline: Connection()

internal class Online(
    private val config: Config,
    private val username: String,
    private val connection: XMPPTCPConnection,
    private val listener: ConnectionListener,
    chats: List<Chat>
): Connection() {

    private val chatManager = ChatManager.getInstanceFor(connection)
    private val multiUserChatManager = MultiUserChatManager.getInstanceFor(connection)

    init {
        chatManager.addIncomingListener { from, message, _ ->
            listener.onMessageReceived(
                id = message.stanzaId,
                message = message.body,
                from = from.localpart.toString()
            )
        }

        chatManager.addOutgoingListener { to, messageBuilder, _ ->
            listener.onMessageSent(
                id = messageBuilder.stanzaId,
                message = messageBuilder.body,
                to = to.localpart.toString()
            )
        }

        multiUserChatManager.addInvitationListener { conn, muc, inviter, reason, password, message, invitation ->
            muc.join(Resourcepart.from(username))
            init(muc)
            listener.onGroupJoined(muc.room.localpart.toString())
        }

        for (chat in chats) {
            if (chat is MultiUserChat) {
                val muc = multiUserChatManager.getMultiUserChat(JidCreate.entityBareFrom("${chat.name}@${config.mucDomain}"))
                muc.join(Resourcepart.from(username))
                init(muc)
            }
        }
    }

    private fun init(muc: SmackMultiUserChat) {
        val name = muc.room.localpart.toString()
        muc.addMessageListener { message ->
            if (message.body.isEmpty())
                return@addMessageListener

            val from = message.from.resourceOrNull?.toString() ?: return@addMessageListener
            listener.onGroupMessageSent(message.stanzaId, message.body, from, name)
        }
    }

    fun createGroup(name: String, members: List<String>) {
        val name = name.lowercase()
        val mucJid = JidCreate.entityBareFrom("$name@${config.mucDomain}")
        val muc = multiUserChatManager.getMultiUserChat(mucJid)
        muc.create(Resourcepart.from(username)).configFormManager.submitConfigurationForm()

        for (member in members) {
            val jid = JidCreate.entityBareFrom("$member@${config.domain}")
            muc.invite(jid, "")
            muc.grantOwnership(jid)
        }

        init(muc)
        listener.onGroupJoined(name)
    }

    fun getMembers(group: String): List<String> {
        val muc = multiUserChatManager.getMultiUserChat(JidCreate.entityBareFrom("$group@${config.mucDomain}"))
        return muc.owners.mapNotNull { it.jid.localpartOrNull?.toString() }.sorted()
    }

    fun sendMessage(message: String, to: String) {
        val chat = chatManager.chatWith(JidCreate.entityBareFrom("$to@${config.domain}"))
        chat.send(message)
    }

    fun sendGroupMessage(message: String, group: String) {
        val muc = multiUserChatManager.getMultiUserChat(JidCreate.entityBareFrom("$group@${config.mucDomain}"))
        muc.sendMessage(message)
    }

    fun ping() {
        connection.sendStanza(Presence(available))
    }

    fun isAlive(): Boolean {
        return connection.isConnected && connection.isAuthenticated
    }
}

interface ConnectionListener {
    fun onMessageReceived(id: String, message: String, from: String)
    fun onMessageSent(id: String, message: String, to: String)
    fun onGroupJoined(group: String)
    fun onGroupMessageSent(id: String, message: String, from: String, group: String)
}

class OfflineException: Exception()