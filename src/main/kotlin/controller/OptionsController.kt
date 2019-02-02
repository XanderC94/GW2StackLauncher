package controller

import events.OptionsEvent
import events.OptionsRequest
import model.json.dumper.GenericJsonDumper
import model.json.objects.GW2Argument
import model.json.objects.GW2CommandLineOptions
import model.json.objects.GW2LocalSettings
import model.json.objects.GW2StackLauncherConfig
import model.utils.Nomenclatures
import model.utils.SystemUtils
import tornadofx.*
import java.nio.file.Paths

class OptionsController : Controller() {

    private var optionsLists: Map<String, GW2Argument> = mapOf()

    private lateinit var appConfig: GW2StackLauncherConfig

    init {

        subscribe<OptionsRequest.UpdateAvailableOptionsList>{
            fire(OptionsEvent.OptionsList(it, optionsLists.values.toList()))
        }

        subscribe<OptionsRequest.UpdateActiveOptionsList>{ request ->
            fire(OptionsEvent.OptionsList(request, optionsLists.values.filter { it.isActive }.toList()))
            fire(OptionsRequest.UpdateAvailableOptionsList())
        }

        subscribe<OptionsRequest.UpdateOptionInfoDisplay>{
            fire(OptionsEvent.Option(it, optionsLists[it.id]!!))
        }

        subscribe<OptionsRequest.UpdateOptionStatus> {
            if (optionsLists.containsKey(it.option)) {
                optionsLists[it.option]!!.isActive = it.isActive
                fire(OptionsRequest.UpdateActiveOptionsList())
            }
        }

        subscribe<OptionsRequest.UpdateOptionValue> {
            if (optionsLists.containsKey(it.option) &&
                    optionsLists[it.option]!!.hasValue &&
                        optionsLists[it.option]!!.isActive) {

                optionsLists[it.option]!!.value = it.text

            }

            fire(OptionsRequest.UpdateActiveOptionsList())
        }

        subscribe<OptionsRequest.SaveOptionsSettings> {

            val gw2UserDir = SystemUtils.GW2UserDirectory()!!.replace('\\', '/')
            val gw2LocalSettingsPath = Paths.get("$gw2UserDir/${Nomenclatures.GW2SettingsJsonName}")

            val activeOptionsAsList = optionsLists.values.filter { it.isActive }.map {
                if (it.hasValue) "${it.name}:${it.value}" else it.name
            }.toList()

            GenericJsonDumper.dump(GW2LocalSettings(activeOptionsAsList), gw2LocalSettingsPath)
        }
    }

    fun setAvailableOptions(optsList: GW2CommandLineOptions) {
        this.optionsLists = optsList.arguments.map {
            it.name to it
        }.toMap().withDefault { GW2Argument.Empty()}

    }

    fun setActiveOptions(localOptions: GW2LocalSettings) {

        localOptions.arguments.map{
            with(it.split(':')) {
                this.first() to this.last()
            }
        }.filter {
            optionsLists.containsKey(it.first)
        }.forEach {
            optionsLists[it.first]!!.isActive = true
            if (optionsLists[it.first]!!.hasValue) {
                optionsLists[it.first]!!.value = it.second
            }

        }
    }

}