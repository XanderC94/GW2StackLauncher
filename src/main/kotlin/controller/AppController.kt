package controller

import controller.http.HTTP
import events.AppRequest
import javafx.application.Platform
import model.objects.*
import model.utils.*
import model.utils.Nomenclatures.Files
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
            try {
                Platform.exit()
            } catch (ex: Exception) {}

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

        val json = if (
                parameters.isNotEmpty() &&
                parameters.containsKey("config-path") &&
                parameters["config-path"]!!.isFile()) {

            parameters["config-path"]!!.asFile().readText()
        } else {
            this.getResourceAsText("/${Files.GW2SLConfigJson}")
        }

        val appConfig: GW2SLConfig = json.fromJson()

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
            this.getResourceAsText("/${Files.GW2ArgumentsJson}")
        } else {
            body
        }
    }

    private fun loadAddOnList(addOnsListLocation : String) {
        loadFrom(addOnsListLocation) {
            val json = checkIfNotEmptyElseLoadFromResources(it)

            val gw2AddOnList : GW2AddOns = json.fromJson()

            addOnsController.setAvailableItems(gw2AddOnList)
        }
    }

    private fun loadOptionList(argListLocation: String) {

        loadFrom(argListLocation) {

            val json = checkIfNotEmptyElseLoadFromResources(it)

            val gw2OptionList : GW2Arguments = json.fromJson()

            optsController.setAvailableItems(gw2OptionList)
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

        val gw2GFXSettingsFile = "$gw2UserDir/${Files.GW2GFXSettings64XML}".asFile()

        if (gw2GFXSettingsFile.exists()) {

            val gfx : GW2GFXSettings = gw2GFXSettingsFile.readText().fromXML()

            optsController.setGW2Application(gfx.application)

            loadGW2LocalSetting(gfx.application.configPath.value)

            loadGW2LocalAddOns(gfx.application.configPath.value)

            gfxController.setGFXSettings(gfx)

            optValuesController.setGW2Application(gfx.application)

        } else {

            fire(AppRequest.CloseApplication(-9))
        }
    }

    private fun loadGW2LocalSetting(localSettingsDir: String) {

        val localSettings = "$localSettingsDir/${Files.GW2LocalSettingsJson}".asFile()

        addLocalsToController(localSettings, optsController, GW2LocalSettings(listOf()))
    }

    private fun loadGW2LocalAddOns(localAddOnsDir: String) {

        val localAddOns = "$localAddOnsDir/${Files.GW2LocalAddonsJson}".asFile()

        addLocalsToController(localAddOns, addOnsController, GW2LocalAddOns(listOf()))
    }

    private inline fun <reified T, reified S> addLocalsToController(file: File, controller: ItemController<T, S>, default: S) {

        controller.setActiveItems(if (file.exists()) {
            file.readText().fromJson()
        } else {
            default
        })
    }
}