package model

import java.nio.charset.Charset
import java.nio.file.Path

open class Dumper(val mapper: Mapper) {

    /**
     * Dump the POJO representation of <T> and return its String representation
     *
     * @param obj The PJO representation of <T>
     * @return The String representation of <T>
     */
    inline fun <reified T> asString(obj: T) : String {
        return mapper.toString(obj)
    }

    /**
     * Dump the object containing the SPOJO representation of @param <T> at the specified Directory
     *
     * @param obj The POJO representation of <T>
     * @param path The Directory where the file is to be dumped
     *
     */
    inline fun <reified T> dump(obj: T, path: Path, charset: Charset){

        try {
            path.toFile().writeText(asString(obj), charset)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }
}