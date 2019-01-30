import controller.Logger
import model.json.dumper.GenericJsonDumper
import model.json.objects.GW2LocalSettings
import model.json.objects.GW2StackLauncherConfig
import model.json.parser.CommandLineOptionsParser
import model.json.parser.ConfigParser
import model.json.parser.LocalSettingsParser
import model.utils.Nomenclatures
import model.utils.SystemUtils
import java.nio.file.Paths
import kotlin.system.exitProcess

fun <T> printCollection(collection: Collection<T>) {
    for (item in collection) {
        println(item)
    }
}

fun main(args: Array<String>) {

    val workspace = System.getProperty("user.dir").replace('\\', '/')

    val logger = Logger.instance();

    logger.info(workspace)

    val configPath = Paths.get("$workspace/src/main/resources/Config.json")

    val config : GW2StackLauncherConfig

    try {
        config = ConfigParser.parse(configPath)
        println(config.toString())
    } catch (ex: Exception) {
        logger.error(ex.message)
        exitProcess(-1)
    }

    val gw2OptionList = CommandLineOptionsParser.parse(config.argumentsListPath)

    val gw2UserDir = SystemUtils.GW2UserDirectory()!!.replace('\\', '/')
    val gw2UserSettingsPath = Paths.get("$gw2UserDir/${Nomenclatures.GW2SettingsJsonName}")

    val gw2LocalSettingsFile = gw2UserSettingsPath.toFile()

    logger.info(gw2LocalSettingsFile.absolutePath)

    if (gw2LocalSettingsFile.exists()) {

        val gw2LocalSettings = LocalSettingsParser.parse(gw2LocalSettingsFile)

//        gw2OptionList.arguments.map {
//            it.copy(isActive = gw2LocalSettings.arguments.contains(it.name))
//        }.filter {
//            it.isActive
//        }.forEach {
//            println(Tuple(it.name, it.type, it.value))
//        }

    } else {

        logger.error("No GW2 Settings.json has been found!")

        val newSettings = gw2OptionList.arguments
                .filter {
                    it.isActive
                }.map {
                    when {
                        it.hasValue -> "${it.name}:${it.value}"
                        else -> it.name
                    }
                }

        GenericJsonDumper.dump(GW2LocalSettings(newSettings), gw2UserSettingsPath)

    }

}