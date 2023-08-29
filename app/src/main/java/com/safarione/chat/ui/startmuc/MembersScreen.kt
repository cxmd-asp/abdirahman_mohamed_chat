package com.safarione.chat.ui.startmuc

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.safarione.chat.util.GrayCircleIcon
import com.safarione.chat.util.NavScreen
import com.safarione.chat.util.Navigator
import com.safarione.chat.util.numbers
import com.safarione.chat.util.toast

object MembersScreen: NavScreen<StartMucViewModel, Unit>() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun TopBar(viewModel: StartMucViewModel, navigator: Navigator<Unit>) {
        TopAppBar(
            title = { Text(stringResource(R.string.title_add_members)) },
            navigationIcon = {
                IconButton(onClick = { navigator.back() }) {
                    Icon(Icons.Filled.ArrowBack, stringResource(R.string.action_back))
                }
            }
        )
    }

    @Composable
    override fun Content(viewModel: StartMucViewModel, navigator: Navigator<Unit>) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(numbers - client.username) { number ->
                Number(
                    number = number,
                    selected = viewModel.members.containsKey(number),
                    onSelectedChange = { selected ->
                        if (selected)
                            viewModel.members[number] = Unit
                        else
                            viewModel.members.remove(number)
                    }
                )
            }
        }
    }

    @Composable
    private fun Number(
        number: String,
        selected: Boolean,
        onSelectedChange: (Boolean) -> Unit
    ) {
        Row(
            modifier = Modifier
                .clickable { onSelectedChange(!selected) }
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = selected, onCheckedChange = onSelectedChange)
            Spacer(modifier = Modifier.width(5.dp))
            GrayCircleIcon(icon = Icons.Filled.Person, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(number, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }

    @Composable
    override fun FloatingActionButton(viewModel: StartMucViewModel, navigator: Navigator<Unit>) {
        val context = LocalContext.current

        FloatingActionButton(onClick = {
            if (viewModel.members.isEmpty()) {
                toast(context, R.string.error_select_one_contact)
                return@FloatingActionButton
            }

            navigator.next(true)
        }) {
            Icon(Icons.Filled.ArrowForward, contentDescription = stringResource(R.string.action_next))
        }
    }
}