package com.safarione.chat.client

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class DatabaseTest {

    @After
    fun cleanup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase("test")
    }

    @Test
    fun testBasicFunctionality() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.databaseBuilder(context, Database::class.java, "test").build()

        val messages = listOf(
            MessageRecord(randomId(), "Hello, World!", "Qasim", "Jazim", System.nanoTime(), null),
            MessageRecord(randomId(), "I am Qasim by the way!!!", "Qasim", "Jazim", System.nanoTime(), null),
            MessageRecord(randomId(), "Goodbye, World!", "Jazim", "Qasim", System.nanoTime(), null),
            MessageRecord(randomId(), "Its been nice knowing you", "Jazim", "Qasim", System.nanoTime(), null),
            MessageRecord(randomId(), "Why did you add me to the group?", "Jazim", "Qasim", System.nanoTime(), "Random test group"),
            MessageRecord(randomId(), "I was testing the group chat feature.", "Qasim", "Jazim", System.nanoTime(), "Random test group"),
            MessageRecord(randomId(), "OK", "Jazim", "Qasim", System.nanoTime(), "Random test group"),
            MessageRecord(randomId(), "Stop adding me to random group chats", "Random", "Random 2", System.nanoTime(), "Random test group"),
            MessageRecord(randomId(), "Nothing to see here", "Random", "Random 2", System.nanoTime(), "Random test group 2"),
        )

        val groupChats = (0..100).map { GroupChatRecord("Group $it", Random.nextLong()) }

        db.messageDao().insertAll(messages.shuffled())
        db.groupChatDao().insertAll(groupChats.shuffled())

        assertEquals(
            messages,
            db.messageDao().getAll().sortedBy { it.time }
        )

        assertEquals(
            messages.subList(0, 2),
            db.messageDao().loadSingleUserChatMessages("Qasim").sortedBy { it.time }
        )

        assertEquals(
            messages.subList(2, 4),
            db.messageDao().loadSingleUserChatMessages("Jazim").sortedBy { it.time }
        )

        assertEquals(
            messages.subList(4, 8),
            db.messageDao().loadMultiUserChatMessages("Random test group").sortedBy { it.time }
        )

        assertEquals(
            messages.subList(8, 9),
            db.messageDao().loadMultiUserChatMessages("Random test group 2").sortedBy { it.time }
        )

        assertEquals(
            groupChats,
            db.groupChatDao().getAll().sortedBy { it.name.substring(6).toInt() }
        )
    }

    private fun randomId(): String {
        return Random.nextLong().toULong().toString()
    }
}