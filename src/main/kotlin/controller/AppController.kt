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

// --config-path="D:\Xander\Documenti\Projects\GW2StackLauncher\res\Config.json"
// https://raw.githubusercontent.com/XanderC94/GW2SLResources/master/Arguments.json
// https://raw.githubusercontent.com/XanderC94/GW2SLResources/master/AddOns.json
class AppController(val parameters: Map<String, String>) : Controller() {

    private val argsController = ArgumentsController()
    private val valuesController = ValuesController()
    private val gfxController = GFXController()
    private val addOnsController = AddOnsController()
    private val aboutController = AboutController()

    private val gw2UserDir = SystemUtils.GW2UserDirectory()

    enum class SourceType(val default: String) {
        Arguments(File.GW2ArgumentsJson), AddOns(File.GW2AddOnsJson)
    }

    class Source(val type: SourceType, val location: String)

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

        val appConfig : GW2SLConfig = if (
                parameters.isNotEmpty() &&
                parameters.containsKey("config-path") &&
                parameters["config-path"]!!.isFile()) {

            parameters["config-path"]!!.asFile().readText().fromJson()
        } else {
            this.getResourceAsText("/${File.GW2SLConfigJson}").fromJson()
        }

        val args = runAsync {
            val argsSrc = Source(SourceType.Arguments, appConfig.argumentListLocation)
            return@runAsync get<GW2Arguments>(from = argsSrc)
        }

        val addOns = runAsync {
            val addOnsSrc = Source(SourceType.AddOns, appConfig.addOnListLocation)
            return@runAsync get<GW2AddOns>(from = addOnsSrc)
        }

        return  args.get() to addOns.get()
    }

    private inline fun <reified T> get(from: Source) : T {

        log.info(from.location)

        val then : (String) -> T = { json ->
            if (json.isEmpty()) {
                log.log(Level.WARNING, "${from.location} hasn't been found. Loading internal.")
                this.getResourceAsText("/${from.type.default}").fromJson()
            } else {
                json.fromJson()
            }
        }

        return then (when {
            from.location.contains("http") -> HTTP.GET(from.location).body()?.string() ?: ""
            from.location.isFile() -> from.location.asFile().readText()
            else -> ""
        })
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