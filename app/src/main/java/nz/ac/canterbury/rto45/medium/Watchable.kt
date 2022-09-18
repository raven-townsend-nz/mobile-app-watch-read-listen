package nz.ac.canterbury.rto45.medium

  import android.util.JsonWriter

class Watchable (
    var name: String,
    var platform: String,
    var recommendedBy: String,
    var watched: Boolean) {
    override fun toString() = name

    fun write(writer: JsonWriter) {
        writer.beginObject()
        writer.name("name").value(name)
        writer.name("platform").value(platform)
        writer.name("recommendedBy").value(recommendedBy)
        writer.name("watched").value(watched)
        writer.endObject()
    }
}
