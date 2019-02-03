fun <T> printCollection(collection: Collection<T>) {
    for (item in collection) {
        println(item)
    }
}

fun main(args: Array<String>) {

//    val gw2UserDir = SystemUtils.GW2UserDirectory()!!.replace('\\', '/')
//    val gw2UserSettingsPath = Paths.get("$gw2UserDir/${Nomenclatures.GW2SettingsJsonName}")
//    val gw2GFXSettingsPath = Paths.get("$gw2UserDir/${Nomenclatures.GW2GFXSettings64XMLName}")
//
//    val gfx = GFXSettingsParser.parse(gw2GFXSettingsPath, Charset.defaultCharset())
//
//    val xmlStr = GenericXMLDumper.dump(gfx)
//
//    println(xmlStr)

//    val gson = GsonBuilder().setPrettyPrinting().create()

//    println(gson.toJson(gfx))

}