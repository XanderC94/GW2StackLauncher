package extentions

import model.ontologies.AddOnMetadata

fun Pair<String, String>.asMetadata() : AddOnMetadata {
    return model.ontologies.AddOnMetadata(this.first, this.second)
}