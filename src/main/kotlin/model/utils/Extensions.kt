package model.utils

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import model.json.JSONDumper
import model.json.JSONParser
import model.objects.GW2Argument
import model.xml.XMLDumper
import model.xml.XMLParser
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import java.io.File
import java.io.InputStream
import java.net.InetAddress
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.xml.bind.DatatypeConverter

fun Any.getResourceAsText(classpath: String) : String {
    return this.getResourceAsStream(classpath).reader().readText()
}

fun Any.getResourceAsStream(classpath: String) : InputStream {
    return this.javaClass.getResourceAsStream(classpath)
}

fun Any.getResource(classpath: String) : URL {
    return this.javaClass.getResource(classpath)
}

fun Any.className() : String {
    return this.javaClass.name
}

//********************************************************************************************************************//

fun JsonObject.build(@NotNull property: String, @Nullable value: JsonElement) : JsonObject {
    this.add(property, value)
    return this
}

fun JsonArray.append(@Nullable values: List<String>?) : JsonArray {
    values?.forEach { this.add(JsonPrimitive(it)) }
    return this
}

//********************************************************************************************************************//

inline fun <reified T > T.asJson() : String {
    return JSONDumper.asString(this)
}

inline fun <reified T > T.asXML() : String {
    return XMLDumper.asString(this)
}

inline fun <reified T > T.saveAsJson(path: String) {
    return JSONDumper.dump(this, Paths.get(path), Charset.defaultCharset())
}

inline fun <reified T > T.saveAsXML(path: String) {
    XMLDumper.dump(this, Paths.get(path), Charset.defaultCharset())
}

//********************************************************************************************************************//

fun File.rename(to: String) : Boolean {
    return this.renameTo(to.asFile())
}

//********************************************************************************************************************//

fun String.asPath() : Path {
    return Paths.get(this.replace("\\", "/"))
}

fun String.asFile() : File {
    return this.asPath().toFile()
}

fun String.isFile() : Boolean {
    return this.asFile().exists() && this.asFile().isFile
}

fun String.isDir() : Boolean {
    return this.asFile().exists() && this.asFile().isDirectory
}

fun String.replaceIf(condition: () -> Boolean) : (Regex, String) -> String {

    return if (condition()) {
        { regex , replacement ->  this.replace(regex, replacement) }
    } else {
        { _, _ -> this }
    }
}

inline fun <reified T > String.fromJson() : T {
    return JSONParser.parse(this)
}

inline fun <reified T > String.fromXML() : T {
    return XMLParser.parse(this)
}

//********************************************************************************************************************//

/**
 * Get RoundTripTime to the specified INetAddress with the given timeout
 *
 */
fun InetAddress.RTT(timeout:Int) : Long {

    val start = Date().time

    this.isReachable(timeout)

    return Date().time - start

}

//********************************************************************************************************************//

fun TextField.isValueSettable(last: GW2Argument, maxChars:Int): Boolean {
    return this.text.isNotEmpty() &&
            this.text.length < maxChars
            && last.hasValue && last.isActive
}

fun TextField.isStatusCorrect(last: GW2Argument) : Boolean {
    return !last.hasValue || last.hasValue &&
            (this.text.isEmpty() && !last.isActive ||
                    !this.text.isEmpty() && last.isActive)
}

fun ListView<Pair<String, Boolean>>.selectAndFocus(to: String, then : (String) -> Unit) : Int{
    val idx = this.items.indexOfFirst { it.first == to }
    if (idx > -1) {
        this.selectionModel.select(idx)
        this.focusModel.focus(idx)
        then(to)
    }
    return idx
}

fun ListView<Pair<String, Boolean>>.selectFocusAndScroll(to: String, then: (String) -> Unit) : Int {
    val idx = this.selectAndFocus(to, then)
    this.scrollTo(if (idx > 1) idx - 1 else idx)
    return idx
}

//********************************************************************************************************************//

fun ByteArray.toHex() : String {
    return DatatypeConverter.printHexBinary(this)
}