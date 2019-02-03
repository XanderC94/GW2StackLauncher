package model.json.parser

import com.google.gson.GsonBuilder
import model.Parser
import model.objects.GW2LocalSettings
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Path

object LocalSettingsParser : Parser<GW2LocalSettings> {

    private val gson = GsonBuilder().create()

    override fun parse(rep: String) : GW2LocalSettings {

        val type = GW2LocalSettings::class.java

        return gson.fromJson(rep, type)
    }

    override fun parse(file: File, charset: Charset): GW2LocalSettings {
        return parse(file.readText(charset))
    }

    override fun parse(path: Path, charset: Charset): GW2LocalSettings {
        return parse(path.toFile(), charset)
    }
}