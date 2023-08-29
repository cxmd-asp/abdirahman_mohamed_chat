package com.safarione.chat.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.safarione.chat.R
import com.safarione.chat.ui.base.BaseActivity
import com.safarione.chat.util.Settings
import com.safarione.chat.util.emptyImmutableList
import kotlinx.collections.immutable.ImmutableList

class SettingsActivity : BaseActivity() {

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
                Content()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(finish: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.action_settings)) },
        navigationIcon = {
            IconButton(onClick = finish) {
                Icon(Icons.Filled.ArrowBack, stringResource(R.string.action_back))
            }
        }
    )
}

@Composable
private fun Content() {
    val backStack = remember { mutableStateListOf<Screen>() }

    Settings(
        path = backStack.lastOrNull()?.path ?: emptyImmutableList(),
        onScreenChange = { title, path ->
            backStack.add(Screen(title, path))
        },
        content = {
            AppearanceSection()
            SecuritySection()
            InfoSection()
        }
    )
}

private data class Screen(
    val title: String,
    val path: ImmutableList<String>
)