package controller

import events.OptionsEvent
import events.OptionsRequest
import model.json.JSONDumper
import model.objects.Application
import model.objects.GW2Argument
import model.objects.GW2Arguments
import model.objects.GW2LocalSettings
import model.utils.Nomenclatures
import model.utils.SystemUtils
import tornadofx.*
import java.nio.file.Paths

@Suppress("MapGetWithNotNullAssertionOperator")
class OptionsController : Controller() {

    private var optionsLists: Map<String, GW2Argument> = mapOf()
    private var activeOptions: MutableSet<String> = mutableSetOf()
    private var gw2: Application? = null

    init {

        subscribe<OptionsRequest.GetAvailableOptionsList>{
            fire(OptionsEvent.OptionsList(it, optionsLists.values.toList()))
        }

        subscribe<OptionsRequest.GetActiveOptionsList>{ request ->
            fire(OptionsEvent.OptionsList(request, optionsLists.values.filter { it.isActive }.toList()))
            fire(OptionsRequest.GetAvailableOptionsList())
        }

        subscribe<OptionsRequest.GetOption>{
            if (optionsLists.containsKey(it.id)) {
                fire(OptionsEvent.Option(it, optionsLists[it.id]!!))
            }
        }

        subscribe<OptionsRequest.UpdateOptionStatus> {
            if (optionsLists.containsKey(it.option)) {
                optionsLists[it.option]!!.isActive = it.isActive
                fire(OptionsRequest.GetActiveOptionsList())
            }
        }

        subscribe<OptionsRequest.UpdateOptionValue> {
            if (optionsLists.containsKey(it.option) &&
                    optionsLists[it.option]!!.hasValue &&
                        optionsLists[it.option]!!.isActive) {

                optionsLists[it.option]!!.value = it.text

            }

            fire(OptionsRequest.GetActiveOptionsList())
        }

        subscribe<OptionsRequest.SaveOptionsSettings> {

            val gw2UserDir = if (gw2 != null) {
                gw2!!.configPath.value.replace('\\', '/')
            } else {
                SystemUtils.GW2UserDirectory()
            }

            val gw2LocalSettingsPath = Paths.get("$gw2UserDir/${Nomenclatures.Files.GW2LocalSettingsJson}")

            val activeOptionsAsList = optionsLists.values.filter { it.isActive }.map {
                if (it.hasValue) "${it.name}:${it.value}" else it.name
            }.toList()

            JSONDumper.dump(GW2LocalSettings(activeOptionsAsList), gw2LocalSettingsPath, Charsets.UTF_8)
        }

        log.info("${this.javaClass.simpleName} READY")
    }

    fun setAvailableOptions(optsList: GW2Arguments) {

        this.optionsLists = optsList.arguments.map {
            it.name to it
        }.toMap().withDefault { GW2Argument()}

        if (activeOptions.isNotEmpty()) {
            setActiveOptions(GW2LocalSettings(activeOptions.toList()))
        } else {
            fire(OptionsRequest.GetAvailableOptionsList())
        }
    }

    fun setActiveOptions(localOptions: GW2LocalSettings) {

        if (optionsLists.isNotEmpty()) {
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
        } else {
            activeOptions = localOptions.arguments.toHashSet()
        }

        fire(OptionsRequest.GetActiveOptionsList())
    }

    fun setGW2Application(gw2: Application) {
        this.gw2 = gw2
    }

}