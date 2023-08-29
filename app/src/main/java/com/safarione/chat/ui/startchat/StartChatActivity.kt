package com.safarione.chat.ui.startchat

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safarione.chat.R
import com.safarione.chat.app.client
import com.safarione.chat.ui.base.BaseActivity
import com.safarione.chat.ui.chat.CreateNewChatContract
import com.safarione.chat.util.GrayCircleIcon
import com.safarione.chat.util.numbers

class StartChatActivity : BaseActivity() {

    @Composable
    override fun Content() {
        Activity(::finish)
    }
}

@Composable
private fun Activity(finish: () -> Unit) {
    Scaffold(
        topBar = { TopBar(finish) },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Content(finish)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(finish: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.title_select_contact)) },
        navigationIcon = {
            IconButton(onClick = finish) {
                Icon(Icons.Filled.ArrowBack, stringResource(R.string.action_back))
            }
        }
    )
}

@Composable
private fun Content(finish: () -> Unit) {
    val createNewChatLauncher = rememberLauncherForActivityResult(CreateNewChatContract()) {}

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(numbers - client.username) { number ->
            Row(
                modifier = Modifier
                    .clickable {
                        createNewChatLauncher.launch(number)
                        finish()
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GrayCircleIcon(icon = Icons.Filled.Person, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(number, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}