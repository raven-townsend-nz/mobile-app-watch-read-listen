package nz.ac.canterbury.rto45.medium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nz.ac.canterbury.rto45.medium.ui.theme.MediaListTheme


sealed class Screen(val route: String, @StringRes val resourceId: Int, val image: ImageVector) {
    object Splash : Screen("splash", R.string.watch_list, Icons.Filled.Tv)
    object Watch : Screen("watch", R.string.watch_list, Icons.Filled.Tv)
    object Read : Screen("read", R.string.read_list, Icons.Filled.AutoStories)
    object Listen : Screen("listen", R.string.listen_list, Icons.Filled.Audiotrack)
}

val gson = Gson()

//convert a data class to a map
fun <T> T.serializeToMap(): Map<String, Any> {
    return convert()
}

//convert a map to a data class
inline fun <reified T> Map<String, Any>.toDataClass(): T {
    return convert()
}

//convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {
    val json = gson.toJson(this)
    return gson.fromJson(json, object : TypeToken<O>() {}.type)
}

inline fun <reified T> stateSaver() = Saver<MutableState<T>, Map<String, Any>>(
    save = { state -> state.value.serializeToMap()},
    restore = { value: Map<String, Any> ->
        mutableStateOf(value.toDataClass())
    }
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val context = this.applicationContext
        super.onCreate(savedInstanceState)
        setContent {
            MediaListTheme {

                var selectedWatchable: Watchable? by rememberSaveable(saver = stateSaver()) {
                    mutableStateOf(
                        null
                    )
                }
                var editWatchableDialogOpen by rememberSaveable { mutableStateOf(false) }
                var createWatchableDialogOpen by rememberSaveable { mutableStateOf(false) }

                var selectedListenable: Listenable? by rememberSaveable(saver = stateSaver()) {
                    mutableStateOf(
                        null
                    )
                }
                var editListenableDialogOpen by rememberSaveable { mutableStateOf(false) }
                var createListenableDialogOpen by rememberSaveable { mutableStateOf(false) }

                var selectedReadable: Readable? by rememberSaveable(saver = stateSaver()) {
                    mutableStateOf(
                        null
                    )
                }
                var editReadableDialogOpen by rememberSaveable { mutableStateOf(false) }
                var createReadableDialogOpen by rememberSaveable { mutableStateOf(false) }

                val items = listOf(
                    Screen.Watch,
                    Screen.Read,
                    Screen.Listen,
                )

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                var showBottomBar by rememberSaveable { (mutableStateOf(false)) }

                showBottomBar = when (navBackStackEntry?.destination?.route) {
                    "splash" -> false
                    else -> true
                }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigation {
                                val currentDestination = navBackStackEntry?.destination
                                items.forEach { screen ->
                                    BottomNavigationItem(
                                        icon = { Icon(screen.image, contentDescription = null) },
                                        label = { Text(stringResource(screen.resourceId)) },
                                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController,
                        startDestination = Screen.Splash.route,
                        Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Splash.route) {
                            AnimatedSplashScreen(navController = navController)
                        }
                        composable(Screen.Watch.route) {
                            WatchPage(
                                openEditDialog = editWatchableDialogOpen,
                                setOpenEditDialog = { editWatchableDialogOpen = it },
                                openCreateDialog = createWatchableDialogOpen,
                                setOpenCreateDialog = { createWatchableDialogOpen = it },
                                selectedWatchable = selectedWatchable,
                                onSelectedWatchableChanged = { selectedWatchable = it },
                                viewModel = WatchableViewModel(context)
                            )
                        }
                        composable(Screen.Read.route) { ReadPage(
                            openEditDialog = editReadableDialogOpen,
                            setOpenEditDialog = { editReadableDialogOpen = it },
                            openCreateDialog = createReadableDialogOpen,
                            setOpenCreateDialog = { createReadableDialogOpen = it },
                            selectedReadable = selectedReadable,
                            onSelectedReadableChanged = { selectedReadable = it },
                            viewModel = ReadableViewModel(context)
                        ) }
                        composable(Screen.Listen.route) { ListenPage(
                            openEditDialog = editListenableDialogOpen,
                            setOpenEditDialog = { editListenableDialogOpen = it },
                            openCreateDialog = createListenableDialogOpen,
                            setOpenCreateDialog = { createListenableDialogOpen = it },
                            selectedListenable = selectedListenable,
                            onSelectedListenableChanged = { selectedListenable = it },
                            viewModel = ListenableViewModel(context)
                        ) }
                    }
                }

            }
        }
    }
}


