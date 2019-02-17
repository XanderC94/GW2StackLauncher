package extentions

import model.json.JSONParser
import model.xml.XMLParser
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

inline fun <reified T > String.fromJson() : T {
    return JSONParser.parse(this)
}

inline fun <reified T > String.fromXML() : T {
    return XMLParser.parse(this)
}

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