package model.ontologies

import model.utils.Nomenclatures

enum class SourceType(val default: String) {
    Arguments(Nomenclatures.File.GW2ArgumentsJson), AddOns(Nomenclatures.File.GW2AddOnsJson)
}