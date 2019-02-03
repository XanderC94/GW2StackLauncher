package model.json.parser

import com.google.gson.GsonBuilder
import model.Parser
import model.objects.GW2CommandLineOptions
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Path

/**
 * Singleton encapsulating the logic to parse the available command line arguments for GW2
 * and create its POJO representation GW2CommandLineOptions.
 *
 */
object CommandLineOptionsParser : Parser<GW2CommandLineOptions> {

    private val gson = GsonBuilder().create()

    override fun parse(rep: String) : GW2CommandLineOptions {

        val type = GW2CommandLineOptions::class.java

        return gson.fromJson(rep, type)
    }

    override fun parse(file: File, charset: Charset): GW2CommandLineOptions {
        return parse(file.readText(charset))
    }

    override fun parse(path: Path, charset: Charset): GW2CommandLineOptions {
        return parse(path.toFile(), charset)
    }

}