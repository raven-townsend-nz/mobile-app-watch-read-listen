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
fun ListenPage (
    openEditDialog: Boolean,
    setOpenEditDialog: (Boolean) -> Unit,
    openCreateDialog: Boolean,
    setOpenCreateDialog: (Boolean) -> Unit,
    selectedListenable: Listenable?,
    onSelectedListenableChanged: (Listenable) -> Unit,
    viewModel: ListenableViewModel
) {

    val context = LocalContext.current
    viewModel.onStart()
    val listenables by remember {mutableStateOf(viewModel.listenables)}

    DisposableEffect(key1 = viewModel) {
        viewModel.listenables = listenables
        onDispose { viewModel.onStop() }
    }

    Column {
        Button(
            onClick = { setOpenCreateDialog(true) },
            Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .fillMaxWidth()
        ) {
            Text(context.getString(R.string.create_listenable_title))
        }
        ListenList(listenables, onListenableClick = { listenable ->
            onSelectedListenableChanged(listenable)
            setOpenEditDialog(true)
        })
    }

    if (openEditDialog) {
        var updatedListenable by rememberSaveable(saver = stateSaver()) { mutableStateOf(Listenable(selectedListenable!!.name, selectedListenable.artist, selectedListenable.artist, selectedListenable.listened)) }
        AlertDialog(
            onDismissRequest = { setOpenEditDialog(false) },
            title = {
                Text(
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    text = selectedListenable!!.name
                )
            },
            text = {
                EditListenableContent(listenable = selectedListenable!!) {
                    updatedListenable = it
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
                                    context.getString(R.string.share_message_start) + selectedListenable!!.name + context.getString(R.string.share_message_listen) + selectedListenable.artist)
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
                            val index = listenables.indexOf(selectedListenable)
                            listenables.removeAt(index)
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
                            var index = listenables.indexOf(selectedListenable)
                            if (index == -1) {
                                for (l in listenables) {
                                    if (l.name == selectedListenable!!.name
                                        && l.artist == selectedListenable.artist
                                        && l.recommendedBy == selectedListenable.recommendedBy
                                        && l.listened == selectedListenable.listened
                                    ) {
                                        index = listenables.indexOf(l)
                                    }
                                }
                            }
                            listenables[index] = updatedListenable
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
        var newListenable = Listenable("", "", "", false)
        AlertDialog(
            onDismissRequest = { setOpenCreateDialog(false) },
            title = {
                Text(
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    text = context.getString(R.string.create_listenable_title)
                )
            },
            text = {
                CreateListenableContent(newListenable) { newListenable = it }
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
                                saveNewListenable(newListenable, listenables, context)
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

fun saveNewListenable(listenable: Listenable, listenables: MutableList<Listenable>, context: Context) {
    if (listenable.name.isNotEmpty()) {
        listenables.add(listenable)
    } else {
        val mediaPlayer = MediaPlayer.create(context, R.raw.invalid_sound)
        mediaPlayer.start()
        Toast.makeText(context, context.getString(R.string.create_listenable_needs_name), Toast.LENGTH_LONG).show()
    }
}

@Composable
fun ListenList(listenables: List<Listenable>, onListenableClick: (Listenable) -> Unit) {
    LazyColumn {
        items(listenables) { listenable ->
            Card(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true)
                ) {
                    onListenableClick(listenable)
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    var listened by remember { mutableStateOf(listenable.listened) }
                    Checkbox(
                        checked = listened,
                        onCheckedChange = { listenable.listened = it; listened = it })
                    Text(
                        modifier = Modifier,
                        style = MaterialTheme.typography.body1,
                        text = listenable.name
                    )
                }
            }
        }
    }
}

@Composable
fun EditListenableContent(listenable: Listenable, setListenable: (Listenable) -> Unit) {
    val context = LocalContext.current
    var platform by rememberSaveable(saver = stateSaver()) { mutableStateOf(TextFieldValue(listenable.artist)) }
    var recommendedBy by rememberSaveable(saver = stateSaver()) { mutableStateOf(TextFieldValue(listenable.recommendedBy)) }
    Column {
        Row(Modifier.padding(vertical = 5.dp)) {
            OutlinedTextField(
                singleLine = true,
                value = platform,
                onValueChange = {
                    platform = it
                    setListenable(Listenable(listenable.name, it.text, listenable.recommendedBy, listenable.listened))},
                label = {Text(context.getString(R.string.platform))}
            )
        }
        Row(Modifier.padding(top = 5.dp)) {
            OutlinedTextField(
                singleLine = true,
                value = recommendedBy,
                onValueChange = {
                    recommendedBy = it
                    setListenable(Listenable(listenable.name, listenable.artist, it.text, listenable.listened))},
                label = {Text(context.getString(R.string.recommended_by))}
            )
        }
    }
}

@Composable
fun CreateListenableContent(listenable: Listenable, setListenable: (Listenable) -> Unit) {
    val context = LocalContext.current
    var name by rememberSaveable(saver = stateSaver()) { mutableStateOf(TextFieldValue(listenable.name)) }
    var artist by rememberSaveable(saver = stateSaver()) { mutableStateOf(TextFieldValue(listenable.artist)) }
    var recommendedBy by rememberSaveable(saver = stateSaver()) { mutableStateOf(TextFieldValue(listenable.recommendedBy)) }
    Column {
        Row(Modifier.padding(vertical = 5.dp)) {
            OutlinedTextField(
                singleLine = true,
                value = name,
                onValueChange = {
                    name = it
                    listenable.name = it.text
                    setListenable(listenable)},
                label = {Text(context.getString(R.string.name))}
            )
        }
        Row(Modifier.padding(vertical = 5.dp)) {
            OutlinedTextField(
                singleLine = true,
                value = artist,
                onValueChange = {
                    artist = it
                    listenable.artist = it.text
                    setListenable(listenable)},
                label = {Text(context.getString(R.string.artist))}
            )
        }
        Row(Modifier.padding(top = 5.dp)) {
            OutlinedTextField(
                singleLine = true,
                value = recommendedBy,
                onValueChange = {
                    recommendedBy = it
                    listenable.recommendedBy = it.text
                    setListenable(listenable)},
                label = {Text(context.getString(R.string.recommended_by))}
            )
        }
    }
}
