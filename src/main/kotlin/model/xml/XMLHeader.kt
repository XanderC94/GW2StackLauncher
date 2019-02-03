package model.xml

import java.nio.charset.Charset

data class XMLHeader(val type: String = "xml",
                     val version : Double = 1.0,
                     val encoding : Charset = Charsets.UTF_8) {
    enum class Fields : () -> String {
        Type, Version, Encoding;

        override fun invoke(): String {
            return this.name.toLowerCase()
        }
    }

    override fun toString(): String {

        return "<?" +
                    "$type " +
                    "${Fields.Version()}=\"${this.version}\" " +
                    "${Fields.Encoding()}=\"${this.encoding.name().toUpperCase()}\"" +
                "?>"
    }
}