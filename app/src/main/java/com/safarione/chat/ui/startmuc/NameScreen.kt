package com.safarione.chat.ui.startmuc

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safarione.chat.R
import com.safarione.chat.util.GrayCircleIcon
import com.safarione.chat.util.NavScreen
import com.safarione.chat.util.Navigator
import com.safarione.chat.util.finish
import com.safarione.chat.util.toast

object NameScreen: NavScreen<StartMucViewModel, Unit>() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun TopBar(viewModel: StartMucViewModel, navigator: Navigator<Unit>) {
        TopAppBar(
            title = { Text(stringResource(R.string.title_enter_name)) },
            navigationIcon = {
                IconButton(onClick = { navigator.back() }) {
                    Icon(Icons.Filled.ArrowBack, stringResource(R.string.action_back))
                }
            }
        )
    }

    @Composable
    override fun Content(viewModel: StartMucViewModel, navigator: Navigator<Unit>) {
        val members = remember {
            viewModel.members.keys.sorted()
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                modifier = Modifier.width(280.dp),
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = { Text(stringResource(R.string.label_name)) }
            )

            Spacer(modifier = Modifier.height(50.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(members) { member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GrayCircleIcon(icon = Icons.Filled.Person, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(member, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    @Composable
    override fun FloatingActionButton(viewModel: StartMucViewModel, navigator: Navigator<Unit>) {
        val context = LocalContext.current

        FloatingActionButton(onClick = {
            if (viewModel.name.isBlank()) {
                toast(context, R.string.error_enter_name)
                return@FloatingActionButton
            }

            if (viewModel.name.contains(' ')) {
                toast(context, R.string.error_space_not_allowed)
                return@FloatingActionButton
            }

            navigator.next(true)
        }) {
            Icon(Icons.Filled.Done, stringResource(R.string.action_done))
        }
    }
}