package model.json.parser

import com.google.gson.GsonBuilder
import model.json.objects.GW2CommandLineOptions
import java.io.File
import java.nio.file.Path

/**
 * Singleton encapsulating the logic to parse the available command line arguments for GW2
 * and create its POJO representation GW2CommandLineOptions.
 *
 */
object CommandLineOptionsParser : JsonParser<GW2CommandLineOptions> {

    private val gson = GsonBuilder().create()

    override fun parse(json: String) : GW2CommandLineOptions {

        val type = GW2CommandLineOptions::class.java

        return gson.fromJson(json, type)
    }

    override fun parse(jsonFile: File) : GW2CommandLineOptions {
        return parse(jsonFile.readText(Charsets.UTF_8))
    }

    override fun parse(path: Path) : GW2CommandLineOptions {
        return parse(path.toFile())
    }

}