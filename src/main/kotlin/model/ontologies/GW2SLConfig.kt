package model.ontologies

import extentions.asFile
import extentions.fromJson
import extentions.getResourceAsText
import extentions.isFile

data class GW2SLConfig(
        val argumentListLocation: String,
        val addOnListLocation: String,
        val userAgent: String,
        val mainStyle: String
    ) {

    companion object {

        fun from(path: String?, isClassPath : Boolean = false) : GW2SLConfig? {
            return if (!isClassPath){
                if (path != null && path.isFile()) {
                    path.asFile().readText().fromJson<GW2SLConfig>()
                } else {
                    null
                }
            } else {
                this.getResourceAsText(path!!).fromJson()
            }
        }

    }

}