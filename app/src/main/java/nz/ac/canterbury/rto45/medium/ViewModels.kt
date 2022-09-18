package nz.ac.canterbury.rto45.medium

import android.annotation.SuppressLint
import android.content.Context
import android.util.JsonReader
import android.util.JsonWriter
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import javax.inject.Inject

var initialWatchables = arrayListOf<Watchable>(
    Watchable(
        "The Good Place",
        "Netflix",
        "Johnny",
        true
    ),
    Watchable(
        "Apollo 13",
        "Neon",
        "Olivia",
        false
    ),
    Watchable(
        "House of Dragons",
        "Neon",
        "Taylor",
        false
    ),
    Watchable(
        "Love Island",
        "Neon",
        "Taylor",
        false
    ),
    Watchable(
        "The Handmaid's Tale",
        "Neon",
        "Taylor",
        false
    ),
    Watchable(
        "Star Wars: The Clone Wars",
        "Disney Plus",
        "Geoff",
        false
    ),
    Watchable(
        "American Dad",
        "Disney Plus",
        "Ben",
        false
    ),
    Watchable(
        "Glee",
        "Disney Plus",
        "Taylor",
        false
    ),
    Watchable(
        "The Bachelorette",
        "TVNZ+",
        "Bob",
        false
    ),
    Watchable(
        "The Davinci Code",
        "TVNZ+",
        "Bob",
        false
    ),
    Watchable(
        "Country Calendar",
        "TVNZ+",
        "Bob",
        false
    ),
    Watchable(
        "Players",
        "TVNZ+",
        "Bob",
        false
    ),
    Watchable(
        "Temptation Island",
        "TVNZ+",
        "Andrew",
        false
    ),
    Watchable(
        "Barons",
        "TVNZ+",
        "Bob",
        false
    ),
    Watchable(
        "Temptation Island",
        "TVNZ+",
        "Bob",
        false
    ),
    Watchable(
        "The Boys",
        "Prime Video",
        "Daniel",
        false
    ),
    Watchable(
        "The Rings of Power",
        "Prime Video",
        "Daniel",
        false
    ),
    Watchable(
        "Top Gear",
        "Prime Video",
        "Daniel",
        false
    ),
    Watchable(
        "Venom",
        "Prime Video",
        "Daniel",
        false
    )
)

var initialListenables = arrayListOf<Listenable>(
    Listenable(
        "Thunderstruck",
        "AC/DC",
        "TJ",
        false
    ),
    Listenable(
        "Shape of You",
        "Ed Sheeran",
        "Bruce",
        false
    ),
    Listenable(
        "Blinding Lights",
        "The Weekend",
        "Bruce",
        false
    ),
    Listenable(
        "Dance Monkey",
        "Tones And I",
        "Bruce",
        false
    ),
    Listenable(
        "rockstar",
        "Post Malone, 21 Savage",
        "Bruce",
        false
    )
)

var initialReadables = arrayListOf<Readable>(
    Readable(
        "Harry Potter",
        "Book",
        "Beth",
        true
    ),
    Readable(
        "To Kill a Mockingbird",
        "Book",
        "Taylor",
        false
    ),
    Readable(
        "Lifehack.org",
        "Blog",
        "Taylor",
        false
    ),
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class WatchableViewModel @Inject constructor(private val context: Context) : ViewModel() {
    var watchables: MutableList<Watchable> = emptyList<Watchable>().toMutableList()

    private val filename = "watchables.json"

    fun onStart() {
        try {
            val file = context.openFileInput(filename)
            val reader = JsonReader(InputStreamReader(file))
            reader.beginArray()
            while(reader.hasNext()) {
                val watchable = readWatchable(reader)
                watchables.add(watchable)
            }
            reader.endArray()
            reader.close()
        } catch (e: FileNotFoundException) {
            watchables = initialWatchables
        }
    }

    fun onStop() {
        try {
            val file = context.openFileOutput(filename, Context.MODE_PRIVATE)
            val writer = JsonWriter(OutputStreamWriter(file))

            writer.beginArray()
            for (watchable in watchables) {
                watchable.write(writer)
            }
            writer.endArray()

            writer.close()
        } catch (e: Exception) {
            Log.d("myerror", e.toString())
        }

    }

    private fun readWatchable(reader: JsonReader) : Watchable {
        val watchable = Watchable("", "", "", false)

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "name" -> watchable.name = reader.nextString()
                "platform" -> watchable.platform = reader.nextString()
                "recommendedBy" -> watchable.recommendedBy = reader.nextString()
                "watched" -> watchable.watched = reader.nextBoolean()
            }
        }
        reader.endObject()

        return watchable
    }
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ListenableViewModel @Inject constructor(private val context: Context) : ViewModel() {
    var listenables: MutableList<Listenable> = emptyList<Listenable>().toMutableList()

    private val filename = "listenables.json"

    fun onStart() {
        try {
            val file = context.openFileInput(filename)
            val reader = JsonReader(InputStreamReader(file))
            reader.beginArray()
            while(reader.hasNext()) {
                val listenable = readListenable(reader)
                listenables.add(listenable)
            }
            reader.endArray()
            reader.close()
        } catch (e: FileNotFoundException) {
            listenables = initialListenables
        }
    }

    fun onStop() {
        try {
            val file = context.openFileOutput(filename, Context.MODE_PRIVATE)
            val writer = JsonWriter(OutputStreamWriter(file))

            writer.beginArray()
            for (listenable in listenables) {
                listenable.write(writer)
            }
            writer.endArray()

            writer.close()
        } catch (e: Exception) {
            Log.d("myerror", e.toString())
        }

    }

    private fun readListenable(reader: JsonReader) : Listenable {
        val listenable = Listenable("", "", "", false)

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "name" -> listenable.name = reader.nextString()
                "artist" -> listenable.artist = reader.nextString()
                "recommendedBy" -> listenable.recommendedBy = reader.nextString()
                "listened" -> listenable.listened = reader.nextBoolean()
            }
        }
        reader.endObject()

        return listenable
    }
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ReadableViewModel @Inject constructor(private val context: Context) : ViewModel() {
    var readables: MutableList<Readable> = emptyList<Readable>().toMutableList()

    private val filename = "readables.json"

    fun onStart() {
        try {
            val file = context.openFileInput(filename)
            val reader = JsonReader(InputStreamReader(file))
            reader.beginArray()
            while(reader.hasNext()) {
                val readable = readReadable(reader)
                readables.add(readable)
            }
            reader.endArray()
            reader.close()
        } catch (e: FileNotFoundException) {
            readables = initialReadables
        }
    }

    fun onStop() {
        try {
            val file = context.openFileOutput(filename, Context.MODE_PRIVATE)
            val writer = JsonWriter(OutputStreamWriter(file))

            writer.beginArray()
            for (readable in readables) {
                readable.write(writer)
            }
            writer.endArray()

            writer.close()
        } catch (e: Exception) {
            Log.d("myerror", e.toString())
        }

    }

    private fun readReadable(reader: JsonReader) : Readable {
        val readable = Readable("", "", "", false)

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "name" -> readable.name = reader.nextString()
                "type" -> readable.type = reader.nextString()
                "recommendedBy" -> readable.recommendedBy = reader.nextString()
                "read" -> readable.read = reader.nextBoolean()
            }
        }
        reader.endObject()

        return readable
    }
}