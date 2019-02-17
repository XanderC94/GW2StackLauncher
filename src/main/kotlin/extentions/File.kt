package extentions

import java.io.File

fun File.rename(to: String) : Boolean {
    return this.renameTo(to.asFile())
}