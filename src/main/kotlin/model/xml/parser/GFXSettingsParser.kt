package model.xml.parser

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import model.Parser
import model.objects.GW2GFXSettings
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Path

object GFXSettingsParser : Parser<GW2GFXSettings>{

    private val module = JacksonXmlModule()
    private val objMapper = XmlMapper(module)

    init {
        module.setDefaultUseWrapper(false)
        objMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    override fun parse(rep: String): GW2GFXSettings {
        return objMapper.readValue(rep, GW2GFXSettings::class.java)
    }

    override fun parse(file: File, charset: Charset): GW2GFXSettings {
        return parse(file.readText(charset))
    }

    override fun parse(path: Path, charset: Charset): GW2GFXSettings {
        return parse(path.toFile(), charset)
    }
}