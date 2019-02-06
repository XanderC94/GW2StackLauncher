package view.utils

import javafx.util.StringConverter

class IPAndRTTConverter : StringConverter<Pair<String, String>>() {

    override fun fromString(string: String?) : Pair<String, String> {
        return if (string != null) {
            val tmp = string.replace("ms", "").split("@")
            tmp.first() to tmp.last()
        } else {
            "" to ""
        }

    }

    override fun toString(obj: Pair<String, String>?): String {
        return if (obj != null) {
            "${obj.first}@${obj.second}ms"
        } else {
            "0.0.0.0@0ms"
        }

    }

    companion object Checker : MappingChecker<String, Pair<String, String>> {
        val ipMatcher = Regex("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}")
        val ipWithRTTMatcher = Regex("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}@[0-9]+ms")

        override fun to(s: Pair<String, String>?): Boolean {
            return s?.first?.matches(ipMatcher) ?: false
        }

        override fun from(t: String?): Boolean {
            return t?.matches(ipWithRTTMatcher) ?: false
        }

    }

}