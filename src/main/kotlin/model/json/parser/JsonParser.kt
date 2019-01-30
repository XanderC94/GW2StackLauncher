package model.json.parser

import java.io.File
import java.nio.file.Path

interface JsonParser<T> {

    /**
     * Parse the String representation of <T> and return its POJO representation
     *
     * @param json The String representation of <T>
     * @return The POJO representation of <T>
     */
    fun parse(json: String) : T

    /**
     * Parse the file containing the String representation of <T> and return its POJO representation
     *
     * @param jsonFile The file containing the String representation of <T>
     * @return The POJO representation of <T>
     */
    fun parse(jsonFile: File) : T

    /**
     * Load the file containing the String representation of @param <T> at the specified Path
     * and return its POJO representation
     *
     * @param path The Path where the file is located
     * @return The POJO representation of <T>
     *
     */
    fun parse(path: Path) : T

}