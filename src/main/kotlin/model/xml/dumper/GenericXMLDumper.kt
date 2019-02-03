package model.xml.dumper

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import model.Dumper
import model.utils.toPrettyPrintedString
import model.xml.XMLHeader
import java.nio.charset.Charset
import java.nio.file.Path

object GenericXMLDumper : Dumper<Any>{

    private val module = JacksonXmlModule()
    private val objMapper = XmlMapper(module)

    init {
        module.setDefaultUseWrapper(false)
        objMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    override fun dump(obj: Any, path: Path, charset: Charset) {
        try {
            path.toFile().writeText(this.dump(obj), charset)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun dump(obj: Any): String {
        val xmlStr = objMapper.writeValueAsString(obj)

        return "${XMLHeader()}\n${toPrettyPrintedString(xmlStr, 4)}"
    }


}