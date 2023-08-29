package com.safarione.chat.client

data class Config(
    val host: String,
    val port: Int = 5222,
    val domain: String,
    val mucDomain: String
)
