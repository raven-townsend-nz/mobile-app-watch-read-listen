package nz.ac.canterbury.rto45.medium

import android.util.JsonWriter

class Listenable (var name: String,
                  var artist: String,
                  var recommendedBy: String,
                  var listened: Boolean) {
    override fun toString() = name

    fun write(writer: JsonWriter) {
        writer.beginObject()
        writer.name("name").value(name)
        writer.name("artist").value(artist)
        writer.name("recommendedBy").value(recommendedBy)
        writer.name("listened").value(listened)
        writer.endObject()
    }
}
