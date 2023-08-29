package com.safarione.chat.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

/*
 * Created on 27 January 2022.
 */

abstract class NavScreen<I, O>() {

    open val start: Boolean = false

    internal val name = this::class.java.simpleName

    @Composable
    open fun Icon() {}

    @Composable
    open fun Label() {}

    @Composable
    abstract fun TopBar(viewModel: I, navigator: Navigator<O>)

    @Composable
    abstract fun Content(viewModel: I, navigator: Navigator<O>)

    @Composable
    open fun FloatingActionButton(viewModel: I, navigator: Navigator<O>) {}
}

interface Navigator<O> {
    fun showDrawer()
    fun navigate(screen: NavScreen<*, O>, addCurrentScreenToBackStack: Boolean)
    fun back()
    fun next(addCurrentScreenToBackStack: Boolean)
    fun finish(result: O)
    fun cancel()
}

fun Navigator<Unit>.finish() {
    finish(Unit)
}

@Composable
fun <I, O> NavigationActivity(
    viewModel: I,
    drawer: @Composable (ColumnScope.(close: () -> Unit) -> Unit)? = null,
    screens: List<NavScreen<I, O>>,
    startScreen: NavScreen<I, O> = screens.find { it.start } ?: screens.first(),
    hasBottomNavigation: Boolean,
    onScreenChanged: (newScreen: NavScreen<I, O>) -> Unit = {},
    onFinish: (O) -> Unit,
    onCancel: () -> Unit
) {
    require(screens.isNotEmpty()) { "the screens can't be empty" }

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            val screen = screens.first { it.name == destination.route }
            onScreenChanged(screen)
        }
    }

    val navigator = remember {
        object : Navigator<O> {
            override fun showDrawer() {
                scope.launch {
                    drawerState.open()
                }
            }

            override fun navigate(screen: NavScreen<*, O>, addCurrentScreenToBackStack: Boolean) {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                navController.navigate(screen.name) {
                    if (!addCurrentScreenToBackStack && currentRoute != null)
                        popUpTo(currentRoute) { inclusive = true }
                }
            }

            override fun back() {
                if (!navController.popBackStack())
                    onCancel()
            }

            override fun next(addCurrentScreenToBackStack: Boolean) {
                val currentRoute = navController.currentBackStackEntry?.destination?.route

                val currentScreen = currentRoute?.let {
                    val index = screens.indexOfFirst { it.name == currentRoute }
                    screens[index + 1]
                } ?: screens.first()

                navigate(currentScreen, addCurrentScreenToBackStack)
            }

            override fun finish(result: O) {
                onFinish(result)
            }

            override fun cancel() {
                onCancel()
            }
        }
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val currentScreen = currentRoute?.let { route -> screens.first { it.name == route } }

    if (drawer == null) {
        NavigationScaffold(viewModel, navigator, navController, screens, startScreen, currentScreen, hasBottomNavigation)
    }
    else {
        ModalNavigationDrawer(
            modifier = Modifier.fillMaxSize(),
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    drawer {
                        scope.launch { drawerState.close() }
                    }
                }
            },
            content = {
                NavigationScaffold(viewModel, navigator, navController, screens, startScreen, currentScreen, hasBottomNavigation)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <I, O> NavigationScaffold(
    viewModel: I,
    navigator: Navigator<O>,
    navController: NavHostController,
    screens: List<NavScreen<I, O>>,
    startScreen: NavScreen<I, O>,
    currentScreen: NavScreen<I, O>?,
    hasBottomNavigation: Boolean
) {
    Scaffold(
        topBar = {
            currentScreen?.TopBar(viewModel, navigator)
        },
        bottomBar = {
            if (hasBottomNavigation)
                BottomBar(screens, currentScreen, navController)
        },
        content = { padding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {
                NavHost(navController, startScreen.name) {
                    screens.forEach { screen ->
                        composable(screen.name) { screen.Content(viewModel, navigator) }
                    }
                }
            }
        },
        floatingActionButton = {
            currentScreen?.FloatingActionButton(viewModel, navigator)
        }
    )
}

@Composable
private fun <I, O> BottomBar(
    screens: List<NavScreen<I, O>>,
    currentScreen: NavScreen<I, O>?,
    navController: NavHostController
) {
    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = { screen.Icon() },
                label = { screen.Label() },
                selected = screen === currentScreen,
                onClick = {
                    navController.navigate(screen.name) {
                        if (currentScreen != null) {
                            //remove the current screen from the back stack
                            popUpTo(currentScreen.name) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}