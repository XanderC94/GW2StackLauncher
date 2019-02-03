package model

import java.nio.charset.Charset
import java.nio.file.Path

interface Dumper<T> {

    /**
     * Dump the POJO representation of <T> and return its String representation
     *
     * @param obj The PJO representation of <T>
     * @return The String representation of <T>
     */
    fun dump(obj: T) : String

    /**
     * Dump the object containing the SPOJO representation of @param <T> at the specified Path
     *
     * @param obj The POJO representation of <T>
     * @param path The Path where the file is to be dumped
     *
     */
    fun dump(obj: T, path: Path, charset: Charset)

}