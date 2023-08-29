package com.safarione.chat.client

import androidx.test.platform.app.InstrumentationRegistry
import com.safarione.chat.client.ClientTest.TestChat.TestMultiUserChat
import com.safarione.chat.client.ClientTest.TestChat.TestSingleUserChat
import org.jivesoftware.smack.android.AndroidSmackInitializer
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class ClientTest {

    init {
        System.setProperty("smack.debugEnabled", "true")
        AndroidSmackInitializer.initialize(InstrumentationRegistry.getInstrumentation().context)
    }

    @Test
    fun testSingleUserChat() {
        //localConfiguration.testSingleUserChat()
        safarioneConfiguration.testSingleUserChat()
    }

    private fun TestConfiguration.testSingleUserChat() {
        clean()

        var client = createClient(this, user)
        var client2 = createClient(this, user2)

        Thread.sleep(3000)

        client.sendMessage("Hello", user2.username)
        Thread.sleep(1000)
        client.sendMessage("ABC", user2.username)

        client.test(
            TestSingleUserChat(
                name = user2.username,
                messages = listOf(
                    "Hello" from user,
                    "ABC" from user
                )
            )
        )

        client2.test(
            TestSingleUserChat(
                name = user.username,
                messages = listOf(
                    "Hello" from user,
                    "ABC" from user
                )
            )
        )

        client2.sendMessage("Hello, World!", user.username)
        Thread.sleep(1000)
        client.sendMessage("Goodbye, World!", user2.username)

        client.test(
            TestSingleUserChat(
                name = user2.username,
                messages = listOf(
                    "Hello" from user,
                    "ABC" from user,
                    "Hello, World!" from user2,
                    "Goodbye, World!" from user
                )
            )
        )

        client2.test(
            TestSingleUserChat(
                name = user.username,
                messages = listOf(
                    "Hello" from user,
                    "ABC" from user,
                    "Hello, World!" from user2,
                    "Goodbye, World!" from user
                )
            )
        )

        client.logout()
        client2.logout()

        client = createClient(this, user)
        client2 = createClient(this, user2)

        client.test(
            TestSingleUserChat(
                name = user2.username,
                messages = listOf(
                    "Hello" from user,
                    "ABC" from user,
                    "Hello, World!" from user2,
                    "Goodbye, World!" from user
                )
            )
        )

        client2.test(
            TestSingleUserChat(
                name = user.username,
                messages = listOf(
                    "Hello" from user,
                    "ABC" from user,
                    "Hello, World!" from user2,
                    "Goodbye, World!" from user
                )
            )
        )

        client2.sendMessage("Everything seems to work", user.username)
        Thread.sleep(1000)
        client.sendMessage("Yeah I guess", user2.username)

        client.test(
            TestSingleUserChat(
                name = user2.username,
                messages = listOf(
                    "Hello" from user,
                    "ABC" from user,
                    "Hello, World!" from user2,
                    "Goodbye, World!" from user,
                    "Everything seems to work" from user2,
                    "Yeah I guess" from user
                )
            )
        )

        client2.test(
            TestSingleUserChat(
                name = user.username,
                messages = listOf(
                    "Hello" from user,
                    "ABC" from user,
                    "Hello, World!" from user2,
                    "Goodbye, World!" from user,
                    "Everything seems to work" from user2,
                    "Yeah I guess" from user
                )
            )
        )

        client.logout()
        client2.logout()
    }

    @Test
    fun testMultiUserChat() {
        //localConfiguration.testMultiUserChat()
        safarioneConfiguration.testMultiUserChat()
    }

    private fun TestConfiguration.testMultiUserChat() {
        clean()

        var client = createClient(this, user)
        var client2 = createClient(this, user2)

        Thread.sleep(3000)

        val roomName = "room[${Random.nextInt().toUInt()}]"
        client.createGroup(roomName, listOf(user2.username))

        client.test(
            TestMultiUserChat(
                name = roomName,
                members = listOf(user.username, user2.username).sorted(),
                messages = emptyList()
            )
        )

        client2.test(
            TestMultiUserChat(
                name = roomName,
                members = listOf(user.username, user2.username).sorted(),
                messages = emptyList())
        )

        client2.send("Why did you add me to the group?", client2.chats.single())
        Thread.sleep(1000)
        client.send("I was testing the group chat feature.", client.chats.single())
        Thread.sleep(1000)
        client2.send("OK", client2.chats.single())

        client.test(
            TestMultiUserChat(
                name = roomName,
                members = listOf(user.username, user2.username).sorted(),
                messages = listOf(
                    "Why did you add me to the group?" from user2,
                    "I was testing the group chat feature." from user,
                    "OK" from user2
                )
            )
        )

        client2.test(
            TestMultiUserChat(
                name = roomName,
                members = listOf(user.username, user2.username).sorted(),
                messages = listOf(
                    "Why did you add me to the group?" from user2,
                    "I was testing the group chat feature." from user,
                    "OK" from user2
                )
            )
        )

        client.sendMessage("This is a direct message", user2.username)
        Thread.sleep(1000)
        client2.sendMessage("Stop sending me dms", user.username)

        client.test(
            TestSingleUserChat(
                name = user2.username,
                messages = listOf(
                    "This is a direct message" from user,
                    "Stop sending me dms" from user2
                )
            ),
            TestMultiUserChat(
                name = roomName,
                members = listOf(user.username, user2.username).sorted(),
                messages = listOf(
                    "Why did you add me to the group?" from user2,
                    "I was testing the group chat feature." from user,
                    "OK" from user2
                )
            )
        )

        client2.test(
            TestSingleUserChat(
                name = user.username,
                messages = listOf(
                    "This is a direct message" from user,
                    "Stop sending me dms" from user2
                )
            ),
            TestMultiUserChat(
                name = roomName,
                members = listOf(user.username, user2.username).sorted(),
                messages = listOf(
                    "Why did you add me to the group?" from user2,
                    "I was testing the group chat feature." from user,
                    "OK" from user2
                )
            )
        )

        client.logout()
        client2.logout()

        client = createClient(this, user)
        client2 = createClient(this, user2)

        client.test(
            TestSingleUserChat(
                name = user2.username,
                messages = listOf(
                    "This is a direct message" from user,
                    "Stop sending me dms" from user2
                )
            ),
            TestMultiUserChat(
                name = roomName,
                members = listOf(user.username, user2.username).sorted(),
                messages = listOf(
                    "Why did you add me to the group?" from user2,
                    "I was testing the group chat feature." from user,
                    "OK" from user2
                )
            )
        )

        client2.test(
            TestSingleUserChat(
                name = user.username,
                messages = listOf(
                    "This is a direct message" from user,
                    "Stop sending me dms" from user2
                )
            ),
            TestMultiUserChat(
                name = roomName,
                members = listOf(user.username, user2.username).sorted(),
                messages = listOf(
                    "Why did you add me to the group?" from user2,
                    "I was testing the group chat feature." from user,
                    "OK" from user2
                )
            )
        )

        client2.sendMessage("Everything seems to work", user.username)
        Thread.sleep(1000)
        client.sendMessage("Yeah I guess but lets check group chats one last time", user2.username)

        client.test(
            TestSingleUserChat(
                name = user2.username,
                messages = listOf(
                    "This is a direct message" from user,
                    "Stop sending me dms" from user2,
                    "Everything seems to work" from user2,
                    "Yeah I guess but lets check group chats one last time" from user
                )
            ),
            TestMultiUserChat(
                name = roomName,
                members = listOf(user.username, user2.username).sorted(),
                messages = listOf(
                    "Why did you add me to the group?" from user2,
                    "I was testing the group chat feature." from user,
                    "OK" from user2
                )
            )
        )

        client2.test(
            TestSingleUserChat(
                name = user.username,
                messages = listOf(
                    "This is a direct message" from user,
                    "Stop sending me dms" from user2,
                    "Everything seems to work" from user2,
                    "Yeah I guess but lets check group chats one last time" from user
                )
            ),
            TestMultiUserChat(
                name = roomName,
                members = listOf(user.username, user2.username).sorted(),
                messages = listOf(
                    "Why did you add me to the group?" from user2,
                    "I was testing the group chat feature." from user,
                    "OK" from user2
                )
            )
        )

        client.send("Testing...", client.chats.first { it is MultiUserChat })
        Thread.sleep(1000)
        client2.send("Looks like the group chat still works", client2.chats.first { it is MultiUserChat })

        client.test(
            TestMultiUserChat(
                name = roomName,
                members = listOf(user.username, user2.username).sorted(),
                messages = listOf(
                    "Why did you add me to the group?" from user2,
                    "I was testing the group chat feature." from user,
                    "OK" from user2,
                    "Testing..." from user,
                    "Looks like the group chat still works" from user2
                )
            ),
            TestSingleUserChat(
                name = user2.username,
                messages = listOf(
                    "This is a direct message" from user,
                    "Stop sending me dms" from user2,
                    "Everything seems to work" from user2,
                    "Yeah I guess but lets check group chats one last time" from user
                )
            )
        )

        client2.test(
            TestMultiUserChat(
                name = roomName,
                members = listOf(user.username, user2.username).sorted(),
                messages = listOf(
                    "Why did you add me to the group?" from user2,
                    "I was testing the group chat feature." from user,
                    "OK" from user2,
                    "Testing..." from user,
                    "Looks like the group chat still works" from user2
                )
            ),
            TestSingleUserChat(
                name = user.username,
                messages = listOf(
                    "This is a direct message" from user,
                    "Stop sending me dms" from user2,
                    "Everything seems to work" from user2,
                    "Yeah I guess but lets check group chats one last time" from user
                )
            )
        )

        client.logout()
        client2.logout()
    }

    private fun Client.test(vararg chats: TestChat) {
        var attempts = 0
        val expected = chats.toList()
        while (true) {
            val actual = postAndWait {
                this.chats.map {
                    when (it) {
                        is SingleUserChat -> {
                            TestSingleUserChat(
                                name = it.name,
                                messages = it.messages.map { it.content from it.from })
                        }

                        is MultiUserChat -> {
                            TestMultiUserChat(
                                name = it.name,
                                members = getMembers(it.name),
                                messages = it.messages.map { it.content from it.from }
                            )
                        }
                    }
                }
            }

            if (expected != actual && attempts < 10) {
                Thread.sleep(1000)
                ++attempts
                continue
            }

            assertEquals(expected, actual)
            break
        }
    }

    private fun Chat.test(vararg messages: TestMessage) {
        assertEquals(messages.toList(), this.messages.map { TestMessage(it.content, it.from) })
    }

    private sealed class TestChat {
        abstract val messages: List<TestMessage>

        data class TestSingleUserChat(
            val name: String,
            override val messages: List<TestMessage>
        ): TestChat()

        data class TestMultiUserChat(
            val name: String,
            val members: List<String>,
            override val messages: List<TestMessage>
        ): TestChat()
    }

    private data class TestMessage(val content: String, val from: String) {
        constructor(content: String, user: User): this(content, user.username)
    }

    private infix fun String.from(from: String): TestMessage {
        return TestMessage(this, from)
    }

    private infix fun String.from(from: User): TestMessage {
        return TestMessage(this, from.username)
    }
}