package com.safarione.chat.client

data class Message(
    val id: String,
    val content: String,
    val from: String,
    val time: Long
)
