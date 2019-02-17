package extentions

import java.io.InputStream
import java.net.URL

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