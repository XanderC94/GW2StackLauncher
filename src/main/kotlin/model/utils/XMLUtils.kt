package model.utils

import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.ByteArrayInputStream
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 * Thanks my god and saviour StackOverflow
 *
 */
fun toPrettyPrintedString(xml: String, indent: Int): String {
    try {
        // Turn xml string into a document
        val document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(InputSource(ByteArrayInputStream(xml.toByteArray(charset("utf-8")))))

        // Remove whitespaces outside tags
        document.normalize()
        val xPath = XPathFactory.newInstance().newXPath()
        val nodeList = xPath.evaluate("//text()[normalize-space()='']",
                document,
                XPathConstants.NODESET) as NodeList

        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i)
            node.parentNode.removeChild(node)
        }

        // Setup pretty print options
        val transformerFactory = TransformerFactory.newInstance()
        transformerFactory.setAttribute("indent-number", indent)
        val transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")

        // Return pretty print xml string
        val stringWriter = StringWriter()
        transformer.transform(DOMSource(document), StreamResult(stringWriter))
        return stringWriter.toString()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }

}