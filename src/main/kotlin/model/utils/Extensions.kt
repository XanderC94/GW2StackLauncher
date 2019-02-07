package model.utils

import model.json.JSONDumper
import model.json.JSONParser
import model.xml.XMLDumper
import model.xml.XMLParser
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.Paths

fun Any.getResourceAsText(classpath: String) : String {
    return this.javaClass.getResourceAsStream(classpath).reader().readText()
}

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

inline fun <reified T > String.fromJson() : T {
    return JSONParser.parse(this)
}

inline fun <reified T > String.fromXML() : T {
    return XMLParser.parse(this)
}