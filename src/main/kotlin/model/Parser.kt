package model

import java.io.File
import java.nio.charset.Charset
import java.nio.file.Path

interface Parser<T> {

    /**
     * Parse the String representation of <T> and return its POJO representation
     *
     * @param rep The String representation of <T>
     * @return The POJO representation of <T>
     */
    fun parse(rep: String) : T

    /**
     * Parse the file containing the String representation of <T> and return its POJO representation
     *
     * @param file The file containing the String representation of <T>
     * @return The POJO representation of <T>
     */
    fun parse(file: File, charset: Charset): T

    /**
     * Load the file containing the String representation of @param <T> at the specified Path
     * and return its POJO representation
     *
     * @param path The Path where the file is located
     * @return The POJO representation of <T>
     *
     */
    fun parse(path: Path, charset: Charset): T

}