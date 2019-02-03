package controller

import events.OptionsEvent
import events.OptionsRequest
import model.json.dumper.GenericJsonDumper
import model.objects.Application
import model.objects.GW2Argument
import model.objects.GW2CommandLineOptions
import model.objects.GW2LocalSettings
import model.utils.Nomenclatures
import tornadofx.*
import java.nio.file.Paths

class OptionsController : Controller() {

    private var optionsLists: Map<String, GW2Argument> = mapOf()
    private lateinit var gw2: Application
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

            val gw2UserDir = gw2.configPath.value.replace('\\', '/')
            val gw2LocalSettingsPath = Paths.get("$gw2UserDir/${Nomenclatures.GW2SettingsJsonName}")

            val activeOptionsAsList = optionsLists.values.filter { it.isActive }.map {
                if (it.hasValue) "${it.name}:${it.value}" else it.name
            }.toList()

            GenericJsonDumper.dump(GW2LocalSettings(activeOptionsAsList), gw2LocalSettingsPath, Charsets.UTF_8)
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

    fun setGWApplication(gw2: Application) {
        this.gw2 = gw2
    }

}