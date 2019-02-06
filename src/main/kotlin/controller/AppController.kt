package controller

import controller.http.HTTP
import events.AppRequest
import javafx.application.Platform
import javafx.scene.control.Alert
import model.json.JSONParser
import model.objects.*
import model.utils.Nomenclatures.Files
import model.utils.SystemUtils
import model.xml.XMLParser
import tornadofx.*
import java.io.File
import java.nio.file.Paths
import java.util.logging.Level
import kotlin.system.exitProcess

class AppController : Controller() {

    private val optsController: OptionsController by inject()
    private val optValuesController: OptionValuesController by inject()
    private val gfxController: GFXController by inject()
    private val addOnsController: AddOnsController by inject()
    private val aboutController: AboutController by inject()

    private val gw2UserDir = SystemUtils.GW2UserDirectory()

    private var parameters: Map<String, String> = mapOf()

    init {
        subscribe<AppRequest.CloseApplication> {
            Platform.exit()
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
        runAsync {
            loadAppConfig()
        }

        runAsync {
            loadGFXSettings()
        }
    }

    private fun loadAppConfig() {

        val appConfigJson = if (
                parameters.isNotEmpty() &&
                parameters.containsKey("config-path") &&
                File(parameters["config-path"]).exists()) {

            Paths.get(parameters["config-path"]).toFile().readText()
        } else {
            this.javaClass.getResourceAsStream("/${Files.GW2SLConfigJson}").reader().readText()
        }

        val appConfig: GW2SLConfig = JSONParser.parse(appConfigJson)

        runAsync {
            loadOptionList(appConfig.argumentListLocation)
        }

        runAsync {
            loadAddOnList(appConfig.addOnListLocation)
        }
    }

    private fun checkIfNotEmptyElseLoadFromResources(body: String) : String {
        return if (body.isEmpty()) {
            log.log(Level.WARNING, "Remote Arguments list hasn't been found. Loading internal.")
            this.javaClass.getResourceAsStream("/${Files.GW2ArgumentsJson}").reader().readText()
        } else {
            body
        }
    }

    private fun loadAddOnList(addOnsListLocation : String) {
        loadFrom(addOnsListLocation) {
            val json = checkIfNotEmptyElseLoadFromResources(it)

            val gw2AddOnList : GW2AddOns = JSONParser.parse(json)

            addOnsController.setAvailableAddOns(gw2AddOnList)
        }
    }

    private fun loadOptionList(argListLocation: String) {

        loadFrom(argListLocation) {

            val json = checkIfNotEmptyElseLoadFromResources(it)

            val gw2OptionList : GW2Arguments = JSONParser.parse(json)

            optsController.setAvailableOptions(gw2OptionList)
        }
    }

    private fun loadFrom(location: String, then: (String) -> Unit) {

        when {
            location.contains("http") ->
                HTTP.GET(location, { _, res ->
                    then(res.body()?.string() ?: "")
                }, { _, _ ->
                    then("")
                })
            File(location).exists() -> then(Paths.get(location).toFile().readText())
            else -> then("")
        }
    }

    private fun loadGFXSettings() {

        val gw2GFXSettingsFile = Paths.get("$gw2UserDir/${Files.GW2GFXSettings64XML}").toFile()

        if (gw2GFXSettingsFile.exists()) {

            val gfx : GW2GFXSettings = XMLParser.parse(gw2GFXSettingsFile.readText())

            optsController.setGW2Application(gfx.application)

            runAsync {
                loadGW2LocalSetting(gfx.application.configPath.value)
            }

            runAsync {
                loadGW2LocalAddOns(gfx.application.configPath.value)
            }

            runAsync {
                gfxController.setGFXSettings(gfx)
            }

            runAsync {
                optValuesController.setGW2Application(gfx.application)
            }

        } else {
            alert(type = Alert.AlertType.ERROR,
                    header = "Config Error",
                    content = "Start GW2 at least once before launching this application!",
                    title = "No GFXSettings found!")

            fire(AppRequest.CloseApplication(-9))
        }
    }

    private fun loadGW2LocalSetting(localSettingsDir: String) {
        val gw2LocalSettingsPath =
                "${localSettingsDir.replace("\\", "/")}/${Files.GW2LocalSettingsJson}"

        val gw2LocalSettingsFile = Paths.get(gw2LocalSettingsPath).toFile()

        optsController.setActiveOptions(if (gw2LocalSettingsFile.exists()) {
            JSONParser.parse(gw2LocalSettingsFile.readText())
        } else {
            GW2LocalSettings(listOf())
        })
    }

    private fun loadGW2LocalAddOns(localAddOnsDir: String) {
        val gw2LocalAddOnsPath =
                "${localAddOnsDir.replace("\\", "/")}/${Files.GW2LocalAddonsJson}"
        val gw2LocalAddOnsFile = Paths.get(gw2LocalAddOnsPath).toFile()

        addOnsController.setActiveAddOns(if (gw2LocalAddOnsFile.exists()) {
            JSONParser.parse(gw2LocalAddOnsFile.readText())
        } else {
            GW2LocalAddOns(listOf())
        })
    }
}