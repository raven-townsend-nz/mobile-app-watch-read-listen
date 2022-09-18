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
fun ReadPage (
    openEditDialog: Boolean,
    setOpenEditDialog: (Boolean) -> Unit,
    openCreateDialog: Boolean,
    setOpenCreateDialog: (Boolean) -> Unit,
    selectedReadable: Readable?,
    onSelectedReadableChanged: (Readable) -> Unit,
    viewModel: ReadableViewModel
) {

    val context = LocalContext.current
    viewModel.onStart()
    val readables by remember {mutableStateOf(viewModel.readables)}

    DisposableEffect(key1 = viewModel) {
        viewModel.readables = readables
        onDispose { viewModel.onStop() }
    }

    Column {
        Button(
            onClick = { setOpenCreateDialog(true) },
            Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .fillMaxWidth()
        ) {
            Text(context.getString(R.string.create_readable_title))
        }
        ReadList(readables, onReadableClick = { readable ->
            onSelectedReadableChanged(readable)
            setOpenEditDialog(true)
        })
    }

    if (openEditDialog) {
        var updatedReadable by rememberSaveable(saver = stateSaver()) { mutableStateOf(Readable(selectedReadable!!.name, selectedReadable.type, selectedReadable.type, selectedReadable.read)) }
        AlertDialog(
            onDismissRequest = { setOpenEditDialog(false) },
            title = {
                Text(
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    text = selectedReadable!!.name
                )
            },
            text = {
                EditReadableContent(readable = selectedReadable!!) {
                    updatedReadable = it
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
                                    context.getString(R.string.share_message_start) + selectedReadable!!.name + context.getString(R.string.share_message_read) + selectedReadable.type)
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
                            val index = readables.indexOf(selectedReadable)
                            readables.removeAt(index)
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
                            var index = readables.indexOf(selectedReadable)
                            if (index == -1) {
                                for (r in readables) {
                                    if (r.name == selectedReadable!!.name
                                        && r.type == selectedReadable.type
                                        && r.recommendedBy == selectedReadable.recommendedBy
                                        && r.read == selectedReadable.read
                                    ) {
                                        index = readables.indexOf(r)
                                    }
                                }
                            }
                            readables[index] = updatedReadable
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
        var newReadable = Readable("", "", "", false)
        AlertDialog(
            onDismissRequest = { setOpenCreateDialog(false) },
            title = {
                Text(
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    text = context.getString(R.string.create_readable_title)
                )
            },
            text = {
                CreateReadableContent(newReadable) { newReadable = it }
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
                                saveNewReadable(newReadable, readables, context)
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

fun saveNewReadable(readable: Readable, readables: MutableList<Readable>, context: Context) {
    if (readable.name.isNotEmpty()) {
        readables.add(readable)
    } else {
        val mediaPlayer = MediaPlayer.create(context, R.raw.invalid_sound)
        mediaPlayer.start()
        Toast.makeText(context, context.getString(R.string.create_readable_needs_name), Toast.LENGTH_LONG).show()
    }
}

@Composable
fun ReadList(readables: List<Readable>, onReadableClick: (Readable) -> Unit) {
    LazyColumn {
        items(readables) { readable ->
            Card(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true)
                ) {
                    onReadableClick(readable)
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    var read by remember { mutableStateOf(readable.read) }
                    Checkbox(
                        checked = read,
                        onCheckedChange = { readable.read = it; read = it })
                    Text(
                        modifier = Modifier,
                        style = MaterialTheme.typography.body1,
                        text = readable.name
                    )
                }
            }
        }
    }
}

@Composable
fun EditReadableContent(readable: Readable, setReadable: (Readable) -> Unit) {
    val context = LocalContext.current
    var type by rememberSaveable(saver = stateSaver()) { mutableStateOf(TextFieldValue(readable.type)) }
    var recommendedBy by rememberSaveable(saver = stateSaver()) { mutableStateOf(TextFieldValue(readable.recommendedBy)) }
    Column {
        Row(Modifier.padding(vertical = 5.dp)) {
            OutlinedTextField(
                singleLine = true,
                value = type,
                onValueChange = {
                    type = it
                    setReadable(Readable(readable.name, it.text, readable.recommendedBy, readable.read))},
                label = {Text(context.getString(R.string.type))}
            )
        }
        Row(Modifier.padding(top = 5.dp)) {
            OutlinedTextField(
                singleLine = true,
                value = recommendedBy,
                onValueChange = {
                    recommendedBy = it
                    setReadable(Readable(readable.name, readable.type, it.text, readable.read))},
                label = {Text(context.getString(R.string.recommended_by))}
            )
        }
    }
}

@Composable
fun CreateReadableContent(readable: Readable, setReadable: (Readable) -> Unit) {
    val context = LocalContext.current
    var name by rememberSaveable(saver = stateSaver()) { mutableStateOf(TextFieldValue(readable.name)) }
    var type by rememberSaveable(saver = stateSaver()) { mutableStateOf(TextFieldValue(readable.type)) }
    var recommendedBy by rememberSaveable(saver = stateSaver()) { mutableStateOf(TextFieldValue(readable.recommendedBy)) }

    var dropDownOptions by remember { mutableStateOf(listOf<String>()) }
    var dropDownExpanded by remember { mutableStateOf(false) }

    val all = listOf("Book", "Article", "Magazine", "Comic", "Blog")

    fun onTypeChanged(value: TextFieldValue) {
        dropDownExpanded = true
        type = value
        dropDownOptions = all.filter { it.lowercase().startsWith(value.text.lowercase()) && it.lowercase() != value.text.lowercase() }.take(3)
        readable.type = value.text
        setReadable(readable)
    }

    Column {
        Row(Modifier.padding(vertical = 5.dp)) {
            OutlinedTextField(
                singleLine = true,
                value = name,
                onValueChange = {
                    name = it
                    readable.name = it.text
                    setReadable(readable)},
                label = {Text(context.getString(R.string.name))}
            )
        }
        Row(Modifier.padding(vertical = 5.dp)) {
            TextFieldWithDropdown(
                modifier = Modifier.fillMaxWidth(),
                value = type,
                setValue = ::onTypeChanged,
                onDismissRequest = {dropDownExpanded = false},
                dropDownExpanded = dropDownExpanded,
                list = dropDownOptions,
                label = context.getString(R.string.type)
            )
        }
        Row(Modifier.padding(top = 5.dp)) {
            OutlinedTextField(
                singleLine = true,
                value = recommendedBy,
                onValueChange = {
                    recommendedBy = it
                    readable.recommendedBy = it.text
                    setReadable(readable)},
                label = {Text(context.getString(R.string.recommended_by))}
            )
        }
    }
}
