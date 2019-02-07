package controller

import controller.networking.HTTP
import events.AppRequest
import javafx.application.Platform
import model.objects.*
import model.utils.*
import model.utils.Nomenclatures.File
import tornadofx.*
import java.util.*
import java.util.logging.Level
import kotlin.system.exitProcess

class AppController(val parameters: Map<String, String>) : Controller() {

    private val browserController = BrowserController()
    private val argsController = ArgumentsController()
    private val valuesController = ValuesController()
    private val gfxController = GFXController()
    private val addOnsController = AddOnsController()
    private val aboutController = AboutController()

    private val gw2UserDir = SystemUtils.GW2UserDirectory()

    init {

        subscribe<AppRequest.InitViewElements> {

            val (args, addOns) = loadAppConfig()

            argsController.setAvailableItems(args)
            addOnsController.setAvailableItems(addOns)

            val gfx = loadGFXSettings()

            if(gfx.isPresent) {

                val configDir = gfx.get().application.configPath.value
                val localSettings = "$configDir/${File.GW2LocalSettingsJson}".asFile()
                val localAddOns = "$configDir/${File.GW2LocalAddonsJson}".asFile()

                argsController.setGW2Application(gfx.get().application)

                addLocals(from = localSettings, to = argsController, withDefault = GW2LocalSettings())

                addLocals(from = localAddOns, to = addOnsController, withDefault = GW2LocalAddOns())

                gfxController.setGFXSettings(gfx.get())

                valuesController.setGW2Application(gfx.get().application)
            }

        }

        subscribe<AppRequest.CloseApplication> {
            try {
                Platform.exit()

            } catch (ex: Exception) {}

            exitProcess(it.exitCode)
        }

        log.info("${this.javaClass.simpleName} READY")
    }

    private fun loadAppConfig() : Pair<GW2Arguments, GW2AddOns> {

        val json = if (
                parameters.isNotEmpty() &&
                parameters.containsKey("config-path") &&
                parameters["config-path"]!!.isFile()) {

            parameters["config-path"]!!.asFile().readText()
        } else {
            this.getResourceAsText("/${File.GW2SLConfigJson}")
        }

        val appConfig: GW2SLConfig = json.fromJson()

        val args = runAsync {
            return@runAsync get<GW2Arguments>(from = appConfig.argumentListLocation)
        }

        val addOns = runAsync {
            return@runAsync get<GW2AddOns>(from = appConfig.addOnListLocation)
        }

        return  args.get() to addOns.get()
    }

    private inline fun <reified T> get(from: String) : T {

        val then : (String) -> T = { json ->
            if (json.isEmpty()) {
                log.log(Level.WARNING, "Remote Arguments list hasn't been found. Loading internal.")
                this.getResourceAsText("/${File.GW2ArgumentsJson}").fromJson()
            } else {
                json.fromJson()
            }
        }

        return when {
            from.contains("http") -> then(HTTP.GET(from).body()?.string() ?: "")
            from.isFile() -> then(from.asFile().readText())
            else -> then("")
        }
    }

    private fun loadGFXSettings() : Optional<GW2GFXSettings> {

        val gw2GFXSettingsFile = "$gw2UserDir/${File.GW2GFXSettings64XML}".asFile()

        return if (gw2GFXSettingsFile.exists()) {

            Optional.of(gw2GFXSettingsFile.readText().fromXML())

        } else {

            fire(AppRequest.CloseApplication(-9))

            return Optional.empty()
        }
    }

    private inline fun <reified T, reified S>
            addLocals(from: java.io.File, to: ItemController<T, S>, withDefault: S) {

        to.setActiveItems(if (from.exists()) {
            from.readText().fromJson()
        } else {
            withDefault
        })
    }
}