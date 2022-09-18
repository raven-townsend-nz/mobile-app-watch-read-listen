package nz.ac.canterbury.rto45.medium

import android.util.JsonWriter

class Readable (var name: String,
                var type: String,
                var recommendedBy: String,
                var read: Boolean) {
    override fun toString() = name

    fun write(writer: JsonWriter) {
        writer.beginObject()
        writer.name("name").value(name)
        writer.name("type").value(type)
        writer.name("recommendedBy").value(recommendedBy)
        writer.name("read").value(read)
        writer.endObject()
    }
}
