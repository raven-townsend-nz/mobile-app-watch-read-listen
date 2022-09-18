package nz.ac.canterbury.rto45.medium

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp


@Composable
fun WatchPage (
    openEditDialog: Boolean,
    setOpenEditDialog: (Boolean) -> Unit,
    openCreateDialog: Boolean,
    setOpenCreateDialog: (Boolean) -> Unit,
    selectedWatchable: Watchable?,
    onSelectedWatchableChanged: (Watchable) -> Unit,
    viewModel: WatchableViewModel
    ) {

    val context = LocalContext.current
    viewModel.onStart()
    val watchables by remember {mutableStateOf(viewModel.watchables)}

    DisposableEffect(key1 = viewModel) {
        viewModel.watchables = watchables
        onDispose { viewModel.onStop() }
    }

    Column {
        Button(
            onClick = { setOpenCreateDialog(true) },
            Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .fillMaxWidth()
        ) {
            Text(context.getString(R.string.create_watchable_title))
        }
        WatchList(watchables, onWatchableClick = { watchable ->
            onSelectedWatchableChanged(watchable)
            setOpenEditDialog(true)
        })
    }

    if (openEditDialog) {
        var updatedWatchable by rememberSaveable(saver = stateSaver()) { mutableStateOf(Watchable(selectedWatchable!!.name, selectedWatchable.platform, selectedWatchable.platform, selectedWatchable.watched)) }
        AlertDialog(
            onDismissRequest = { setOpenEditDialog(false) },
            title = {
                Text(
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    text = selectedWatchable!!.name
                )
            },
            text = {
                EditWatchableContent(watchable = selectedWatchable!!) {
                    updatedWatchable = it
                }
            },
            buttons = {
                Row(
                    Modifier
                        .padding(start = 25.dp, end = 25.dp, bottom = 20.dp)
                        .fillMaxWidth()) {
                    Box(Modifier.weight(1f)) {
                        Button(onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    context.getString(R.string.share_message_start) + selectedWatchable!!.name + context.getString(R.string.share_message_watch) + selectedWatchable.platform)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        },
                            Modifier
                                .padding(end = 2.dp)
                                .fillMaxWidth()) {
                            Text(context.getString(R.string.share))
                        }
                    }
                    Box(Modifier.weight(1f)) {
                        Button(onClick = {
                            val index = watchables.indexOf(selectedWatchable)
                            watchables.removeAt(index)
                            setOpenEditDialog(false)
                        },
                            Modifier
                                .padding(horizontal = 2.dp)
                                .fillMaxWidth()) {
                            Text(context.getString(R.string.delete))
                        }
                    }
                    Box(Modifier.weight(1f)) {
                        Button(onClick = {
                            var index = watchables.indexOf(selectedWatchable)
                            if (index == -1) {
                                for (w in watchables) {
                                    if (w.name == selectedWatchable!!.name
                                        && w.platform == selectedWatchable.platform
                                        && w.recommendedBy == selectedWatchable.recommendedBy
                                        && w.watched == selectedWatchable.watched
                                    ) {
                                        index = watchables.indexOf(w)
                                    }
                                }
                            }
                            watchables[index] = updatedWatchable
                            setOpenEditDialog(false)
                        },
                            Modifier
                                .padding(start = 2.dp)
                                .fillMaxWidth()) {
                            Text(context.getString(R.string.save))
                        }
                    }
                }
            }
        )
    }

    if (openCreateDialog) {
        var newWatchable = Watchable("", "", "", false)
        AlertDialog(
            onDismissRequest = { setOpenCreateDialog(false) },
            title = {
                Text(
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    text = context.getString(R.string.create_watchable_title)
                )
            },
            text = {
                CreateWatchableContent(newWatchable) { newWatchable = it }
            },
            buttons = {
                Row(
                    Modifier
                        .padding(start = 25.dp, end = 25.dp, bottom = 20.dp)
                        .fillMaxWidth()) {
                    Box(Modifier.weight(1f)) {
                        Button(
                            onClick = {setOpenCreateDialog(false)},
                            Modifier
                                .padding(horizontal = 2.dp)
                                .fillMaxWidth()) {
                            Text(context.getString(R.string.cancel))
                        }
                    }
                    Box(Modifier.weight(1f)) {
                        Button(
                            onClick = {
                                saveNewWatchable(newWatchable, watchables, context)
                                setOpenCreateDialog(false)},
                            Modifier
                                .padding(start = 2.dp)
                                .fillMaxWidth()) {
                            Text(context.getString(R.string.save))
                        }
                    }
                }
            }
        )
    }

}

fun saveNewWatchable(watchable: Watchable, watchables: MutableList<Watchable>, context: Context) {
    if (watchable.name.isNotEmpty()) {
        watchables.add(watchable)
    } else {
        val mediaPlayer = MediaPlayer.create(context, R.raw.invalid_sound)
        mediaPlayer.start()
        Toast.makeText(context, context.getString(R.string.create_watchable_needs_name), Toast.LENGTH_LONG).show()
    }
}

@Composable
fun WatchList(watchables: List<Watchable>, onWatchableClick: (Watchable) -> Unit) {
    val context = LocalContext.current
    LazyColumn {
        items(watchables) { watchable ->
            Card(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true)
                ) {
                    onWatchableClick(watchable)
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    var watched by remember { mutableStateOf(watchable.watched) }
                    Checkbox(
                        checked = watched,
                        onCheckedChange = { watchable.watched = it; watched = it })
                    Text(
                        modifier = Modifier,
                        style = MaterialTheme.typography.body1,
                        text = watchable.name
                    )
                }
            }
        }
    }
}

@Composable
fun EditWatchableContent(watchable: Watchable, setWatchable: (Watchable) -> Unit) {
    val context = LocalContext.current
    var platform by rememberSaveable(saver = stateSaver()) { mutableStateOf(TextFieldValue(watchable.platform)) }
    var recommendedBy by rememberSaveable(saver = stateSaver()) { mutableStateOf(TextFieldValue(watchable.recommendedBy)) }
    Column {
        Row(Modifier.padding(vertical = 5.dp)) {
            OutlinedTextField(
                singleLine = true,
                value = platform,
                onValueChange = {
                    platform = it
                    setWatchable(Watchable(watchable.name, it.text, watchable.recommendedBy, watchable.watched))},
                label = {Text(context.getString(R.string.platform))}
            )
        }
        Row(Modifier.padding(top = 5.dp)) {
            OutlinedTextField(
                singleLine = true,
                value = recommendedBy,
                onValueChange = {
                    recommendedBy = it
                    setWatchable(Watchable(watchable.name, watchable.platform, it.text, watchable.watched))},
                label = {Text(context.getString(R.string.recommended_by))}
            )
        }
    }
}



@Composable
fun CreateWatchableContent(watchable: Watchable, setWatchable: (Watchable) -> Unit) {
    val context = LocalContext.current
    var name by rememberSaveable(saver = stateSaver()) { mutableStateOf(TextFieldValue(watchable.name)) }
    var platform by rememberSaveable(saver = stateSaver()) { mutableStateOf(TextFieldValue(watchable.platform)) }
    var recommendedBy by rememberSaveable(saver = stateSaver()) { mutableStateOf(TextFieldValue(watchable.recommendedBy)) }

    val all = listOf("Netflix", "Disney Plus", "TVNZ+", "Prime Video", "Neon")

    var dropDownOptions by remember { mutableStateOf(listOf<String>()) }
    var dropDownExpanded by remember { mutableStateOf(false) }

    fun onPlatformChanged(value: TextFieldValue) {
        dropDownExpanded = true
        platform = value
        dropDownOptions = all.filter { it.lowercase().startsWith(value.text.lowercase()) && it.lowercase() != value.text.lowercase() }.take(3)
        watchable.platform = value.text
        setWatchable(watchable)
    }

    Column {
        Row(Modifier.padding(vertical = 5.dp)) {
            OutlinedTextField(
                singleLine = true,
                value = name,
                onValueChange = {
                    name = it
                    watchable.name = it.text
                    setWatchable(watchable)},
                label = {Text(context.getString(R.string.name))}
            )
        }
        Row(Modifier.padding(vertical = 5.dp)) {
            TextFieldWithDropdown(
                modifier = Modifier.fillMaxWidth(),
                value = platform,
                setValue = ::onPlatformChanged,
                onDismissRequest = {dropDownExpanded = false},
                dropDownExpanded = dropDownExpanded,
                list = dropDownOptions,
                label = context.getString(R.string.platform)
            )
        }
        Row(Modifier.padding(top = 5.dp)) {
            OutlinedTextField(
                singleLine = true,
                value = recommendedBy,
                onValueChange = {
                    recommendedBy = it
                    watchable.recommendedBy = it.text
                    setWatchable(watchable)},
                label = {Text(context.getString(R.string.recommended_by))}
            )
        }
    }
}
