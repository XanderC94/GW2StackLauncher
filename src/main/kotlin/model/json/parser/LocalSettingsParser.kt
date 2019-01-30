package model.json.parser

import com.google.gson.GsonBuilder
import model.json.objects.GW2LocalSettings
import java.io.File
import java.nio.file.Path

object LocalSettingsParser : JsonParser<GW2LocalSettings> {

    private val gson = GsonBuilder().create()

    override fun parse(json: String) : GW2LocalSettings {

        val type = GW2LocalSettings::class.java

        return gson.fromJson(json, type)
    }

    override fun parse(jsonFile: File) : GW2LocalSettings {
        return parse(jsonFile.readText(Charsets.UTF_8))
    }

    override fun parse(path: Path) : GW2LocalSettings {
        return parse(path.toFile())
    }
}