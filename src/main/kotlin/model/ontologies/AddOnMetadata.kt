package model.ontologies

import java.util.regex.Pattern

data class AddOnMetadata(val signature: String, val url: String) {

    companion object {

        val versionInPagePattern = Pattern.compile("Release:? ?([\\d+.?]+)")
        val versionInURLPattern = Pattern.compile("(?<=_)(?:\\w+)+")

    }

}