package com.safarione.chat.util

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import com.safarione.chat.R
import com.safarione.chat.client.Message
import com.safarione.chat.client.MultiUserChat
import com.safarione.chat.client.SingleUserChat
import com.safarione.chat.ui.chat.ChatViewModel
import com.safarione.chat.ui.settings.Settings

@Composable
fun <T> LiveData<T>.observeAsNonNullState(): State<T> {
    return observeAsState(value!!)
}

/**
 * Returns the composable [block] if the specified [condition] is true.
 */
fun condition(condition: Boolean, block: @Composable () -> Unit): @Composable (() -> Unit)? {
    return if (condition) block else null
}

val ColorScheme.selection: Color
    @Composable
    get() {
        val color = primary.toArgb()

        val red = android.graphics.Color.red(color)
        val green = android.graphics.Color.green(color)
        val blue = android.graphics.Color.blue(color)

        return Color(red, green, blue, 0x30)
    }

/**
 * Displays a loading screen while running the [block] and displays an error message if an error
 * occurs.
 * The block should return an error message if an error occurs or null if the loading was a success.
 */
@Composable
fun Loading(message: String? = null, block: suspend (error: (Int) -> Unit) -> Unit) {
    val context = LocalContext.current
    var error: String? by remember { mutableStateOf(null) }

    if (error == null) {
        Loading(message)

        LaunchedEffect(Unit) {
            block { errorMessage ->
                error = context.getString(errorMessage)
            }
        }
    }
    else {
        Error(error!!) {
            error = null
        }
    }
}

@Composable
fun Loading(message: String? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(Modifier.size(50.dp))

        if (message != null) {
            Spacer(Modifier.height(20.dp))
            Text(message, fontSize = 20.sp, textAlign = TextAlign.Center)
        }
    }
}

/**
 * Displays an error message and optionally a button to resolve the error.
 * @param message the error message
 * @param action the label to display on the button
 * @param onClick the action to perform when the button is clicked, if null no button is displayed
 */
@Composable
fun Error(
    message: String,
    action: String = stringResource(R.string.action_try_again),
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.width(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(message, fontSize = 23.sp, textAlign = TextAlign.Center)

            if (onClick != null) {
                Spacer(Modifier.height(20.dp))
                Button(onClick = onClick) {
                    Text(action)
                }
            }
        }
    }
}

interface OverflowMenuScope {
    fun dismiss()
}

@Composable
fun OverflowMenu(
    imageVector: ImageVector,
    contentDescription: String?,
    dropdownWidth: Dp = 200.dp,
    content: @Composable OverflowMenuScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val scope = remember {
        object : OverflowMenuScope {
            override fun dismiss() {
                expanded = false
            }
        }
    }

    Column {
        IconButton(onClick = { expanded = true }) {
            Icon(imageVector, contentDescription)
        }

        DropdownMenu(
            modifier = Modifier.width(dropdownWidth),
            expanded = expanded,
            onDismissRequest = { expanded = false },
            content = { content(scope) }
        )
    }
}

@Composable
fun OverflowMenuScope.OverflowMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true
) {
    DropdownMenuItem(
        text = text,
        onClick = {
            dismiss()
            onClick()
        },
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled
    )
}

@Composable
fun Item(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {},
    subtitle: @Composable (() -> Unit)? = null,
    badge: @Composable () -> Unit = {},
    onClick: (() -> Unit)
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Column(modifier = Modifier.weight(1f)) {
            title()
            if (subtitle != null) {
                Spacer(Modifier.height(3.dp))
                subtitle()
            }
        }
        badge()
    }
}

@Composable
fun GrayCircleIcon(icon: ImageVector, contentDescription: String?) {
    val backgroundColor: Color
    val foregroundColor: Color

    if (Settings.inDarkTheme()) {
        backgroundColor = Color(0xFF667683)
        foregroundColor = Color(0xFFD0D7DF)
    }
    else {
        backgroundColor = Color(0xFFCFD8DD)
        foregroundColor = Color.White
    }

    Box(modifier = Modifier.background(backgroundColor, CircleShape)) {
        Icon(
            modifier = Modifier
                .padding(5.dp)
                .size(36.dp),
            imageVector = icon,
            contentDescription = contentDescription,
            tint = foregroundColor
        )
    }
}

@Composable
fun Messages(
    messages: List<Message>,
    username: String,
    multiUserChat: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 5.dp),
        reverseLayout = true
    ) {
        items(messages.size) { index ->
            val reversedIndex = messages.lastIndex - index
            val message = messages[reversedIndex]

            Message(
                message = message.content,
                sender = message.from.takeUnless { it == username },
                multiUserChat = multiUserChat
            )

            if (reversedIndex != 0)
                Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@Composable
fun Message(
    message: String,
    sender: String?,
    multiUserChat: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
    ) {
        if (sender == null)
            Spacer(modifier = Modifier.weight(1f))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (sender == null)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                if (multiUserChat && sender != null) {
                    Text(
                        text = sender,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(message, fontSize = 16.sp)
            }
        }

        if (sender != null)
            Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun MessageBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = message,
            onValueChange = onMessageChange,
            label = { Text(stringResource(R.string.label_message)) },
            shape = RoundedCornerShape(40.dp)
        )

        IconButton(
            onClick = onSend,
            enabled = message.isNotEmpty()
        ) {
            Icon(Icons.Filled.Send, stringResource(R.string.action_send))
        }
    }
}