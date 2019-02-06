package model

interface Mapper {

    fun <T> from(string : String, toClazz: Class<T>) : T

    fun <T> toString(obj: T) : String
}