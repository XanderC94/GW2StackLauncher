package model.json.parser

import com.google.gson.GsonBuilder
import model.json.objects.GW2StackLauncherConfig
import java.io.File
import java.nio.file.Path

/**
 * Singleton encapsulating the logic to parse the program configuration
 * and create its POJO representation GW2StackLauncherConfig.
 *
 */
object ConfigParser : JsonParser<GW2StackLauncherConfig> {

    private val gson = GsonBuilder()
            .registerTypeAdapter(GW2StackLauncherConfig::class.java, GW2StackLauncherConfig)
            .create()

    override fun parse(jsonFile: File): GW2StackLauncherConfig {
        return parse(jsonFile.readText(Charsets.UTF_8))
    }

    override fun parse(path: Path): GW2StackLauncherConfig {
        return parse(path.toFile())
    }

    override fun parse(json: String): GW2StackLauncherConfig {
        val type = GW2StackLauncherConfig::class.java

        return gson.fromJson(json, type)
    }

}