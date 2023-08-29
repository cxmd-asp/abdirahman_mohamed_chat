package com.safarione.chat.client

import com.safarione.chat.client.LoginResult.LoginFailure.BadCredentialsFailure
import com.safarione.chat.client.LoginResult.LoginFailure.ConnectionFailure
import com.safarione.chat.client.LoginResult.LoginSuccess
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Presence.Type.available
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration

internal fun login(
    config: Config,
    username: String,
    password: String
): Pair<LoginResult, XMPPTCPConnection?> {
    val configuration = XMPPTCPConnectionConfiguration.builder()
        .setUsernameAndPassword(username, password)
        .setResource("android")
        .setXmppDomain(config.domain)
        .setHost(config.host)
        .setPort(config.port)
        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
        .build()

    val connection = XMPPTCPConnection(configuration)

    try {
        connection.connect()
    }
    catch (e: Exception) {
        e.printStackTrace()
        return ConnectionFailure to null
    }

    return try {
        connection.login()
        connection.sendStanza(Presence(available))
        LoginSuccess to connection
    }
    catch (e: Exception) {
        e.printStackTrace()
        BadCredentialsFailure to null
    }
}

sealed class LoginResult {

    data object LoginSuccess: LoginResult()

    sealed class LoginFailure: LoginResult() {
        data object ConnectionFailure: LoginFailure()
        data object BadCredentialsFailure: LoginFailure()
    }
}
