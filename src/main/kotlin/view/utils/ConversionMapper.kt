package view.utils

import javafx.util.StringConverter

abstract class ConversionMapper<T> : StringConverter<T>() {

    protected val mappings : MutableMap<String, StringConverter<T>> = mutableMapOf()
    protected val logics : MutableMap<String, MappingChecker<String, T>> = mutableMapOf()

    fun setMapping(type: String, converter: StringConverter<T>, checker: MappingChecker<String, T>) {
        mappings.putIfAbsent(type, converter)
        logics.putIfAbsent(type, checker)
    }
}

class OptionValuesConversionMapper : ConversionMapper<Pair<String, String>>() {

    override fun fromString(string: String?) : Pair<String, String> {

        val type = logics.toList().firstOrNull { it.second.from(string) }

        return if (type != null) mappings[type.first]!!.fromString(string) else "" to ""
    }

    override fun toString(obj: Pair<String, String>?): String {

        val type = logics.toList().firstOrNull { it.second.to(obj) }

        return if (type != null) mappings[type.first]!!.toString(obj) else ""
    }
}

interface MappingChecker<T, S> {

    fun from(t: T?) : Boolean

    fun to(s: S?) : Boolean

}