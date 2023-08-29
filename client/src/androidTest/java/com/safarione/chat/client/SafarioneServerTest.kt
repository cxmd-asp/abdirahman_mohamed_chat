package com.safarione.chat.client

import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.junit.Assert.assertTrue
import org.junit.Test
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import java.net.Socket

class SafarioneServerTest {

    /**
     * Makes sure the server is on at all.
     * It was shutdown at one point so lets get this test out of the way first.
     */
    @Test
    fun testServerOnline() {
        Socket("uatchat2.waafi.com", 5222)
    }

    @Test
    fun testWhetherMucIsEnabled() {
        val config = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword("906275043772", "1Uld0qKqQV2ONBCPR7pFISAv+s9w8dBCd6CvaNE/nKrZVcHk5JOHzdRGwcjFGnGhyC1V4ckhPsYZkiEOdqD7Gg==")
            .setXmppDomain("uatchat2.waafi.com")
            .setHost("uatchat2.waafi.com")
            .setPort(5222)
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .build()

        val connection: AbstractXMPPConnection = XMPPTCPConnection(config)
        connection.connect()
        connection.login()

        val manager = MultiUserChatManager.getInstanceFor(connection)
        assertTrue(manager.mucServiceDomains.isNotEmpty())
        println(manager.mucServiceDomains)
    }
}