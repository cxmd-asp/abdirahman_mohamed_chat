package com.safarione.chat.client

sealed class Chat {
    abstract val name: String
    abstract val messages: List<Message>
}

sealed class SingleUserChat: Chat()

sealed class MultiUserChat: Chat() {
    abstract val timeCreated: Long
}
