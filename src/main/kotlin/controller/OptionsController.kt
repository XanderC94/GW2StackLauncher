package controller

import events.OptionsEvent
import events.OptionsRequest
import model.objects.Application
import model.objects.GW2Argument
import model.objects.GW2Arguments
import model.objects.GW2LocalSettings
import model.utils.Nomenclatures
import model.utils.SystemUtils
import model.utils.saveAsJson
import tornadofx.*

@Suppress("MapGetWithNotNullAssertionOperator")
class OptionsController : Controller(), ItemController<GW2Arguments, GW2LocalSettings>, GW2ApplicationController {

    private var optionsLists: Map<String, GW2Argument> = mapOf()
    private var activeOptions: GW2LocalSettings = GW2LocalSettings()
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

            val gw2LocalSettingsPath = "$gw2UserDir/${Nomenclatures.Files.GW2LocalSettingsJson}"

            val activeOptionsAsList = optionsLists.values.filter { it.isActive }.map {
                if (it.hasValue) "${it.name}:${it.value}" else it.name
            }.toList()

            GW2LocalSettings(activeOptionsAsList).saveAsJson(gw2LocalSettingsPath)
        }

        log.info("${this.javaClass.simpleName} READY")
    }

    override fun setAvailableItems(items: GW2Arguments) {

        this.optionsLists = items.arguments.map { it.name to it }
                .toMap().withDefault { GW2Argument()}

        if (activeOptions.arguments.isNotEmpty()) {
            setActiveItems(activeOptions)
        }

        fire(OptionsRequest.GetAvailableOptionsList())
    }

    override fun setActiveItems(items: GW2LocalSettings) {

        if (optionsLists.isNotEmpty()) {
            items.arguments.map{
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
            activeOptions = items
        }

        fire(OptionsRequest.GetActiveOptionsList())
    }

    override fun setGW2Application(gw2: Application) {
        this.gw2 = gw2
    }

}