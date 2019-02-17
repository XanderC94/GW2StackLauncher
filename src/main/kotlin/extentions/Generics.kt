package extentions

import model.json.JSONDumper
import model.xml.XMLDumper
import java.nio.charset.Charset
import java.nio.file.Paths

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