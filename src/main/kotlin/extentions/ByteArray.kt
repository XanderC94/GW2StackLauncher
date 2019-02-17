package extentions

import javax.xml.bind.DatatypeConverter

fun ByteArray.toHex() : String {
    return DatatypeConverter.printHexBinary(this)
}