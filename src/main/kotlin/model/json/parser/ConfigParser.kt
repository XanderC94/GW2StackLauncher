package model.json.parser

import com.google.gson.GsonBuilder
import model.Parser
import model.objects.GW2StackLauncherConfig
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Path

/**
 * Singleton encapsulating the logic to parse the program configuration
 * and create its POJO representation GW2StackLauncherConfig.
 *
 */
object ConfigParser : Parser<GW2StackLauncherConfig> {

    private val gson = GsonBuilder()
            .registerTypeAdapter(GW2StackLauncherConfig::class.java, GW2StackLauncherConfig)
            .create()

    override fun parse(file: File, charset: Charset): GW2StackLauncherConfig {
        return parse(file.readText(charset))
    }

    override fun parse(path: Path, charset: Charset): GW2StackLauncherConfig {
        return parse(path.toFile(), charset)
    }

    override fun parse(rep: String): GW2StackLauncherConfig {
        val type = GW2StackLauncherConfig::class.java

        return gson.fromJson(rep, type)
    }

}