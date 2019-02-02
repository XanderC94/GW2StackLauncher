import controller.Logger
import controller.OptionsController
import model.json.objects.GW2LocalSettings
import model.json.objects.GW2StackLauncherConfig
import model.json.parser.CommandLineOptionsParser
import model.json.parser.ConfigParser
import model.json.parser.LocalSettingsParser
import model.utils.Nomenclatures
import model.utils.SystemUtils
import tornadofx.*
import view.GW2StackLauncherView
import java.nio.file.Paths
import kotlin.system.exitProcess

class GW2StackLauncherApp: App(GW2StackLauncherView::class) {

    private val controller: OptionsController = OptionsController()
    private val logger = Logger.instance()

    init {

        importStylesheet("../style/GW2StackLauncherStyle.css")

    }

    override fun init() {

        super.init()

        val appWorkspace = System.getProperty("user.dir").replace('\\', '/')

        val appConfig: GW2StackLauncherConfig

        val appConfigPath = if (
                !parameters.named.isEmpty() &&
                parameters.named.containsKey("config-path")) {
            logger.info("Configuration loaded from ${parameters.named["config-path"]}.")
            Paths.get(parameters.named["config-path"])
        } else {
            logger.info("Configuration loaded from default location.")
            Paths.get("$appWorkspace/src/main/resources/Config.json")
        }

        try {
            appConfig = ConfigParser.parse(appConfigPath)
        } catch (ex: Exception) {
            logger.error("Configuration file not found")
            exitProcess(-1)
        }

        val gw2OptionList = CommandLineOptionsParser.parse(appConfig.argumentsListPath)

        controller.setAvailableOptions(gw2OptionList)

        val gw2UserDir = SystemUtils.GW2UserDirectory()!!.replace('\\', '/')
        val gw2LocalSettingsFile = Paths.get("$gw2UserDir/${Nomenclatures.GW2SettingsJsonName}").toFile()

        if (gw2LocalSettingsFile.exists()) {

            val gw2LocalSettings = LocalSettingsParser.parse(gw2LocalSettingsFile)
            controller.setActiveOptions(gw2LocalSettings)
        } else {
            controller.setActiveOptions(GW2LocalSettings(listOf()))
        }
    }

}