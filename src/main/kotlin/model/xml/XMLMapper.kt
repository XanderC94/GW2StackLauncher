package model.xml

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import model.Mapper
import model.utils.toPrettyPrintedString

object XMLMapper : Mapper {

    private val module = JacksonXmlModule()
    private val objMapper = XmlMapper(module)

    init {
        module.setDefaultUseWrapper(false)
        objMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    override fun <T> from(string: String, toClazz: Class<T>): T {
        return objMapper.readValue(string, toClazz)
    }

    override fun <T> toString(obj: T): String {
        val xmlStr = objMapper.writeValueAsString(obj)

        return "${XMLHeader()}\n${toPrettyPrintedString(xmlStr, 4)}"
    }

}