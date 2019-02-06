package view.utils

import javafx.util.StringConverter

class SystemPathConverter : StringConverter<Pair<String, String>>() {

    override fun fromString(string: String?) : Pair<String, String> {

        return if (string != null) {
            val tmp = string.replace("GB", "").split("@")
            tmp.first() to tmp.last()
        } else {
            "" to ""
        }
    }

    override fun toString(obj: Pair<String, String>?): String {
        return if (obj != null) {
            "${obj.first}@${obj.second}GB"
        } else {
            "@"
        }
    }

    companion object Checker : MappingChecker<String, Pair<String, String>> {

        private val pathMatcher = Regex("[a-zA-Z]?:?([/\\\\].*)+.*(.?.*)")

        override fun to(s: Pair<String, String>?): Boolean {
                return s?.first?.matches(pathMatcher) ?: false
        }

        override fun from(t: String?): Boolean {
            return t?.matches(pathMatcher) ?: false
        }

    }
}