package com.safarione.chat.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlin.random.Random

@Composable
fun Settings(
    path: ImmutableList<String>,
    onScreenChange: (title: String, path: ImmutableList<String>) -> Unit,
    content: @Composable SettingsScope.() -> Unit
) {
    val nodes = remember { mutableStateListOf<Node>() }
    val scope = remember { SettingsScopeImpl(emptyPersistentList(), nodes) }

    content(scope)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Nodes(path, nodes, onScreenChange)
    }
}

@Composable
fun SettingsScope.Screen(
    title: String,
    subtitle: String? = null,
    icon: @Composable (() -> Unit)? = null,
    order: Int = 0,
    content: @Composable SettingsScope.() -> Unit
) {
    Node(title, content) { path ->
        ScreenNode(title, subtitle, icon, path, order)
    }
}

@Composable
fun SettingsScope.Category(
    name: String,
    order: Int = 0,
    content: @Composable SettingsScope.() -> Unit
) {
    Node(name, content) { path ->
        CategoryNode(name, path, order)
    }
}

@Composable
fun SettingsScope.Setting(
    title: @Composable () -> Unit,
    subtitle: @Composable (() -> Unit)? = null,
    badge: @Composable (() -> Unit)? = null,
    order: Int = 0,
    onClick: () -> Unit
) {
    Node { path ->
        SettingNode(
            title = title,
            subtitle = subtitle,
            badge = badge,
            onClick = onClick,
            path = path,
            order = order
        )
    }
}

@Composable
fun SettingsScope.Setting(
    title: String,
    subtitle: String? = null,
    badge: @Composable (() -> Unit)? = null,
    order: Int = 0,
    onClick: () -> Unit
) {
    Setting(
        title = {
            Text(title)
        },
        subtitle = condition(subtitle != null) {
            Text(subtitle!!)
        },
        badge = badge,
        order = order,
        onClick = onClick
    )
}

@Composable
fun SettingsScope.SwitchSetting(
    title: String,
    subtitle: String? = null,
    value: Boolean,
    order: Int = 0,
    onClick: (Boolean) -> Unit
) {
    Setting(
        title = title,
        subtitle = subtitle,
        badge = {
            Switch(checked = value, onCheckedChange = onClick)
        },
        order = order,
        onClick = { onClick(!value) }
    )
}

@Composable
private fun SettingsScope.Node(
    name: String? = null,
    content: @Composable (SettingsScope.() -> Unit)? = null,
    creator: (path: PersistentList<String>) -> Node
) {
    val scope = this as SettingsScopeImpl
    var node by remember { mutableStateOf(creator(scope.path)) }

    DisposableEffect(Unit) {
        scope.nodes.add(node)
        onDispose { scope.nodes.removeFirst { it === node } }
    }

    //replace the node whenever the lambda that created it changes
    var oldCreator by remember { mutableStateOf(creator) }
    LaunchedEffect(creator) {
        if (creator === oldCreator)
            return@LaunchedEffect

        val index = scope.nodes.indexOfFirst { it === node }.notValid { return@LaunchedEffect }
        val newNode = creator(scope.path)
        newNode.id = node.id
        node = newNode
        scope.nodes[index] = newNode

        oldCreator = creator
    }

    if (name != null && content != null) {
        val newScope = remember { scope.newScope(name) }
        content(newScope)
    }
}

@Composable
private fun Nodes(
    path: ImmutableList<String>,
    nodes: List<Node>,
    onScreenChange: (title: String, path: ImmutableList<String>) -> Unit
) {
    for (node in getNodes(path, nodes)) {
        key(node.id) {
            when (node) {
                is ScreenNode -> {
                    ScreenNode(
                        title = node.title,
                        subtitle = node.subtitle,
                        icon = node.icon,
                        onClick = { onScreenChange(node.title, node.path.add(node.title)) }
                    )
                }
                is CategoryNode -> {
                    CategoryNode(node.name)

                    Nodes(
                        path = node.path.add(node.name),
                        nodes = nodes,
                        onScreenChange = onScreenChange
                    )
                }
                is SettingNode -> {
                    SettingNode(
                        title = node.title,
                        subtitle = node.subtitle,
                        badge = node.badge,
                        onClick = node.onClick
                    )
                }
            }
        }
    }
}

private fun getNodes(path: ImmutableList<String>, nodes: List<Node>): List<Node> {
    return nodes
        .filter { it.path == path }
        .toMutableList()
        .apply {
            merge()
            sort()
        }
}

@Composable
private fun ScreenNode(
    title: String,
    subtitle: String?,
    icon: @Composable (() -> Unit)?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .heightIn(min = 50.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                icon()
            }
            Spacer(Modifier.width(5.dp))
        }

        Column {
            Text(title, fontSize = 16.sp)

            if (subtitle != null)
                Text(subtitle, fontSize = 14.sp, fontWeight = FontWeight.Light)
        }

        Spacer(Modifier.weight(1f))
        Icon(Icons.Filled.NavigateNext, null)
    }
}

@Composable
private fun CategoryNode(name: String) {
    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
        Text(
            text = name,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SettingNode(
    title: @Composable () -> Unit,
    subtitle: @Composable (() -> Unit)? = null,
    badge: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .heightIn(min = 50.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            ProvideTextStyle(TextStyle(fontSize = 16.sp)) {
                title()
            }

            if (subtitle != null) {
                Spacer(Modifier.height(3.dp))
                ProvideTextStyle(TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Light)) {
                    subtitle()
                }
            }
        }
        Spacer(Modifier.width(5.dp))
        badge?.invoke()
    }
}

sealed interface SettingsScope

private class SettingsScopeImpl(
    val path: PersistentList<String>,
    val nodes: SnapshotStateList<Node>
): SettingsScope {
    fun newScope(name: String): SettingsScopeImpl {
        return SettingsScopeImpl(
            path = path.add(name),
            nodes = nodes
        )
    }
}

private sealed class Node: Comparable<Node> {
    abstract val level: Int
    abstract val path: PersistentList<String>
    abstract val order: Int

    var id = Random.nextLong()

    override fun compareTo(other: Node): Int {
        val result = OrderComparator.compare(order, other.order)
        return if (result == 0) {
            if (this is ScreenNode && other is ScreenNode)
                String.CASE_INSENSITIVE_ORDER.compare(title, other.title)
            else
                level.compareTo(other.level)
        }
        else {
            result
        }
    }
}

private data class ScreenNode(
    val title: String,
    val subtitle: String? = null,
    val icon: @Composable (() -> Unit)? = null,
    override val path: PersistentList<String>,
    override val order: Int
): Node() {
    override val level = 1
}

private data class CategoryNode(
    val name: String,
    override val path: PersistentList<String>,
    override val order: Int
): Node() {
    override val level = 2
}

private data class SettingNode(
    val title: @Composable () -> Unit,
    val subtitle: @Composable (() -> Unit)? = null,
    val badge: @Composable (() -> Unit)? = null,
    val onClick: () -> Unit,
    override val path: PersistentList<String>,
    override val order: Int
): Node() {
    override val level = 3
}

private fun MutableList<Node>.merge() {
    //merge all the same identical nodes
    for (i in 0 until size) {
        val node = getOrNull(i) ?: break
        when (node) {
            is ScreenNode -> {
                var c = i + 1
                while (c < size) {
                    val node2 = getOrNull(c++) ?: break
                    if (node2 !is ScreenNode)
                        continue

                    if (node.title == node2.title && node.path == node2.path) {
                        this[i] = ScreenNode(
                            title = node.title,
                            subtitle = node.subtitle ?: node2.subtitle,
                            icon = node.icon ?: node2.icon,
                            path = node.path,
                            order = if (node.order != 0) node.order else node2.order
                        )

                        removeAt(--c)
                    }
                }
            }
            is CategoryNode -> {
                var c = i + 1
                while (c < size) {
                    val node2 = getOrNull(c++) ?: break
                    if (node2 !is CategoryNode)
                        continue

                    if (node.name == node2.name && node.path == node2.path) {
                        this[i] = CategoryNode(
                            name = node.name,
                            path = node.path,
                            order = if (node.order != 0) node.order else node2.order
                        )

                        removeAt(--c)
                    }
                }
            }
            is SettingNode -> {
                //setting nodes can't be merged
            }
        }
    }
}