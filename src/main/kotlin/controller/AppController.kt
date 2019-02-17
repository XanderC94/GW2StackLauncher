package controller

import controller.networking.HTTP
import events.AppRequest
import events.BrowserEvent
import events.BrowserRequest
import javafx.application.Platform
import model.objects.*
import model.utils.*
import model.utils.Nomenclatures.File
import tornadofx.*
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
    private val updateManager = UpdateManager()

    private val gw2UserDir = SystemUtils.GW2UserDirectory()

    enum class SourceType(val default: String) {
        Arguments(File.GW2ArgumentsJson), AddOns(File.GW2AddOnsJson)
    }

    class Source(val type: SourceType, val location: String)

    private var exitCode : Int = 0

    init {

        subscribe<AppRequest.InitViewElements> {

            val (args, addOns) = loadAppConfig()

            argsController.setAvailableItems(args)
            addOnsController.setAvailableItems(addOns)

            val gfx = loadGFXSettings()

            if(gfx != null) {

                val gw2ConfigDir = gfx.application.configPath.value
                val localSettings = "$gw2ConfigDir/${File.GW2LocalSettingsJson}".asFile()

                addLocals(from = localSettings, to = argsController, withDefault = GW2LocalSettings())

                gfxController.setGFXSettings(gfx)

                argsController.setGW2Application(gfx.application)
                valuesController.setGW2Application(gfx.application)
                updateManager.setGW2Application(gfx.application)
            }

            val localAddOns = "${SystemUtils.gw2slDir()!!}/${File.GW2LocalAddonsJson}".asFile()
            addLocals(from = localAddOns, to = addOnsController, withDefault = GW2LocalAddOns())

        }

        subscribe<AppRequest.CloseApplication> {
            exitCode = it.exitCode

            fire(BrowserRequest.CloseBrowser())
        }

        subscribe<BrowserEvent.BrowserClosed> {
            try {
                Platform.exit()
            } catch (ex: Throwable) { } finally {
                exitProcess(exitCode)
            }
        }

        log.info("${this.className()} READY")
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

    private fun loadGFXSettings() : GW2GFXSettings? {

        val gw2GFXSettingsFile = "$gw2UserDir/${File.GW2GFXSettings64XML}".asFile()

        return if (gw2GFXSettingsFile.exists()) {

            gw2GFXSettingsFile.readText().fromXML()

        } else {

            log.log(Level.SEVERE, "No GFX Settings found. Run GW2 at least once.")

            fire(AppRequest.CloseApplication(-9))

            return null
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