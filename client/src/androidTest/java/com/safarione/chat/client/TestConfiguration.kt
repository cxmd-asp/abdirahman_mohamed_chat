package com.safarione.chat.client

import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry
import com.safarione.chat.client.LoginResult.LoginSuccess
import org.junit.Assert.assertEquals

val localConfiguration = TestConfiguration(
    host = "192.168.0.73",
    domain = "localhost",
    mucDomain = "muc.localhost",
    user = User(
        username = "qasim",
        password = "secret123"
    ),
    user2 = User(
        username = "jazim",
        password = "secret456"
    )
)

val safarioneConfiguration = TestConfiguration(
    host = "uatchat2.waafi.com",
    domain = "uatchat2.waafi.com",
    mucDomain = "muclight.uatchat2.waafi.com",
    user = User(
        username = "906275043772",
        password = "1Uld0qKqQV2ONBCPR7pFISAv+s9w8dBCd6CvaNE/nKrZVcHk5JOHzdRGwcjFGnGhyC1V4ckhPsYZkiEOdqD7Gg=="
    ),
    user2 = User(
        username = "906266103024",
        password = "Gf1wNqXd/wnuF0OqB+o5J1Livt7PV0mOzJvu6jvvhHQbDi+GqVEUYGaWlFleMK22D/BhbXXlpHoGigZU+3rAqA=="
    )
)

data class TestConfiguration(
    val host: String,
    val port: Int = 5222,
    val domain: String,
    val mucDomain: String,
    val user: User,
    val user2: User
) {
    fun clean() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase("database-${user.username}")
        context.deleteDatabase("database-${user2.username}")
    }
}

data class User(
    val username: String,
    val password: String
)

fun createClient(configuration: TestConfiguration, user: User): Client {
    val client = Client(
        context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application,
        config = Config(
            host = configuration.host,
            port = configuration.port,
            domain = configuration.domain,
            mucDomain = configuration.mucDomain
        )
    )

    assertEquals(LoginSuccess, client.login(user.username, user.password))
    return client
}