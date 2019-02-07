package model

import java.io.File
import java.nio.charset.Charset
import java.nio.file.Path

open class Parser(val mapper : Mapper) {

    /**
     * Parse the String representation of <T> and return its POJO representation
     *
     * @param rep The String representation of <T>
     * @return The POJO representation of <T>
     */
    inline fun <reified T> parse(rep: String): T {
        return mapper.from(rep, T::class.java)
    }

    /**
     * Parse the file containing the String representation of <T> and return its POJO representation
     *
     * @param file The file containing the String representation of <T>
     * @return The POJO representation of <T>
     */
    inline fun <reified T> parse(file: File, charset: Charset): T {
        return parse(file.readText(charset))
    }

    /**
     * Load the file containing the String representation of <T> at the specified Directory
     * and return its POJO representation
     *
     * @param path The Directory where the file is located
     * @return The POJO representation of <T>
     *
     */
    inline fun <reified T> parse(path: Path, charset: Charset): T {
        return parse(path.toFile(), charset)
    }
}