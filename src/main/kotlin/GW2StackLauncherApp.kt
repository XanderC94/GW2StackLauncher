import controller.GFXController
import controller.Logger
import controller.OptionsController
import javafx.scene.control.Alert
import model.json.parser.CommandLineOptionsParser
import model.json.parser.ConfigParser
import model.json.parser.LocalSettingsParser
import model.objects.GW2LocalSettings
import model.utils.Nomenclatures
import model.utils.SystemUtils
import model.xml.parser.GFXSettingsParser
import tornadofx.*
import view.GW2StackLauncherView
import java.nio.charset.Charset
import java.nio.file.Paths
import kotlin.system.exitProcess

class GW2StackLauncherApp: App(GW2StackLauncherView::class) {

    private val optsController: OptionsController by inject()
    private val gfxController: GFXController by inject()

    private val logger = Logger.instance()

    init {

        importStylesheet("../style/GW2StackLauncherStyle.css")

    }

    override fun init() {

        super.init()

        val appConfigPath = if (
                !parameters.named.isEmpty() &&
                parameters.named.containsKey("config-path")) {
            logger.info("Configuration loaded from ${parameters.named["config-path"]}.")
            Paths.get(parameters.named["config-path"])
        } else {
            logger.info("Configuration loaded from default location.")
            val appWorkspace = System.getProperty("user.dir").replace('\\', '/')
            Paths.get("$appWorkspace/src/main/resources/Config.json")
        }

        val appConfig = try {
            ConfigParser.parse(appConfigPath, Charsets.UTF_8)
        } catch (ex: Exception) {
            logger.error("Configuration file not found")
            exitProcess(-1)
        }

        val gw2OptionList = CommandLineOptionsParser.parse(appConfig.argumentsListPath, Charsets.UTF_8)

        optsController.setAvailableOptions(gw2OptionList)

        val gw2UserDir = SystemUtils.GW2UserDirectory()!!.replace('\\', '/')
        val gw2LocalSettingsFile = Paths.get("$gw2UserDir/${Nomenclatures.GW2SettingsJsonName}").toFile()

        if (gw2LocalSettingsFile.exists()) {

            val gw2LocalSettings = LocalSettingsParser.parse(gw2LocalSettingsFile, Charsets.UTF_8)
            optsController.setActiveOptions(gw2LocalSettings)
        } else {
            optsController.setActiveOptions(GW2LocalSettings(listOf()))
        }

        val gw2GFXSettingsFile = Paths.get("$gw2UserDir/${Nomenclatures.GW2GFXSettings64XMLName}").toFile()

        if (gw2GFXSettingsFile.exists()) {
            val gfx = GFXSettingsParser.parse(gw2GFXSettingsFile, Charset.defaultCharset())
            gfxController.setGFXSettings(gfx)
        } else {
            alert(type = Alert.AlertType.ERROR,
                    header = "Config Error",
                    content = "Start GW2 at least once before launching this application!",
                    title = "No GFXSettings found!")
            exitProcess(-1)
        }

    }

}