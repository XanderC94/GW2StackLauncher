package model.ontologies

import model.utils.Nomenclatures

enum class SourceType(val default: String) {
    Arguments(Nomenclatures.Files.GW2ArgumentsJson), AddOns(Nomenclatures.Files.GW2AddOnsJson)
}