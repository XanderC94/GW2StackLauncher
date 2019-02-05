package controller

import events.AppRequest
import javafx.scene.control.Alert
import model.json.parser.CommandLineOptionsParser
import model.json.parser.ConfigParser
import model.json.parser.LocalSettingsParser
import model.objects.GW2LocalSettings
import model.objects.GW2StackLauncherConfig
import model.utils.Nomenclatures
import model.utils.SystemUtils
import model.xml.parser.GFXSettingsParser
import tornadofx.*
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Paths
import kotlin.system.exitProcess

class AppController : Controller() {

    private val optsController: OptionsController by inject()
    private val optValuesController: OptionValuesController by inject()
    private val gfxController: GFXController by inject()
    private val addOnsController: AddOnsController by inject()
    private val aboutController: AboutController by inject()

    private val gw2UserDir = SystemUtils.GW2UserDirectory()!!

    private var parameters: Map<String, String> = mapOf()

    init {
        subscribe<AppRequest.CloseApplication> {
            exitProcess(it.exitCode)
        }

        log.info(optsController.toString())
        log.info(optValuesController.toString())
        log.info(gfxController.toString())
        log.info(addOnsController.toString())
        log.info(aboutController.toString())
    }

    fun addProgramParameters(params: Map<String, String>) {
        this.parameters = params

        initControllers()
    }

    private fun initControllers() {

        runAsync {

            var isCustom = false

            val appConfigJson = if (
                    parameters.isNotEmpty() &&
                    parameters.containsKey("config-path") &&
                    File(parameters["config-path"]).exists()
            ) {
                isCustom = true
                Paths.get(parameters["config-path"]).toFile().readText()
            } else {
                this.javaClass.getResourceAsStream("/${Nomenclatures.File.GW2SLConfigJson}").reader().readText()
            }

            val appConfig: GW2StackLauncherConfig = ConfigParser.parse(appConfigJson)

            val argsJson = if (isCustom) {
                appConfig.argumentsListPath.toFile().readText()
            } else {
                val settingsPathStr = appConfig.argumentsListPath.toString().replace("\\", "/")
                this.javaClass.getResourceAsStream(settingsPathStr).reader().readText()
            }

            val gw2OptionList = CommandLineOptionsParser.parse(argsJson)

            optsController.setAvailableOptions(gw2OptionList)

//            fire(OptionsRequest.LoadAvailableOptionsList(appConfig.argumentsListPath))

        }

        runAsync {
            val gw2GFXSettingsFile = Paths.get("$gw2UserDir/${Nomenclatures.File.GW2GFXSettings64XML}").toFile()

            if (gw2GFXSettingsFile.exists()) {

                val gfx = GFXSettingsParser.parse(gw2GFXSettingsFile, Charset.defaultCharset())

                optsController.setGWApplication(gfx.application)

                val gw2LocalSettingsPath = "${gfx.application.configPath.value.replace("\\", "/")}/${Nomenclatures.File.GW2SettingsJson}"
                val gw2LocalSettingsFile = Paths.get(gw2LocalSettingsPath).toFile()

                optsController.setActiveOptions(if (gw2LocalSettingsFile.exists()) {
                    LocalSettingsParser.parse(gw2LocalSettingsFile, Charsets.UTF_8)
                } else {
                    GW2LocalSettings(listOf())
                })

                gfxController.setGFXSettings(gfx)

            } else {
                alert(type = Alert.AlertType.ERROR,
                        header = "Config Error",
                        content = "Start GW2 at least once before launching this application!",
                        title = "No GFXSettings found!")
                fire(AppRequest.CloseApplication(-9))
            }
        }
    }
}