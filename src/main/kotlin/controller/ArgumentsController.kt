package controller

import events.ArgumentsEvent
import events.ArgumentsRequest
import model.objects.Application
import model.objects.GW2Argument
import model.objects.GW2Arguments
import model.objects.GW2LocalSettings
import model.utils.Nomenclatures
import model.utils.SystemUtils
import model.utils.saveAsJson
import java.util.concurrent.ConcurrentHashMap

@Suppress("MapGetWithNotNullAssertionOperator")
class ArgumentsController : ViewController(), ItemController<GW2Arguments, GW2LocalSettings>, GW2Dipper {

    private var availableArgs: Map<String, GW2Argument> = ConcurrentHashMap()
    private var activeArgs: GW2LocalSettings = GW2LocalSettings()
    private var gw2: Application? = null

    init {

        subscribe<ArgumentsRequest.GetAvailableArguments>{
            fire(ArgumentsEvent.ArgumentsList(it, availableArgs.values.toList()))
        }

        subscribe<ArgumentsRequest.GetActiveArguments>{ request ->
            fire(ArgumentsEvent.ArgumentsList(request, availableArgs.values.filter { it.isActive }.toList()))
            fire(ArgumentsRequest.GetAvailableArguments())
        }

        subscribe<ArgumentsRequest.GetArgument>{
            if (availableArgs.containsKey(it.id)) {
                fire(ArgumentsEvent.Argument(it, availableArgs[it.id]!!))
            }
        }

        subscribe<ArgumentsRequest.UpdateArgumentStatus> {
            if (availableArgs.containsKey(it.option)) {
                availableArgs[it.option]!!.isActive = it.isActive
                fire(ArgumentsRequest.GetActiveArguments())
            }
        }

        subscribe<ArgumentsRequest.UpdateArgumentValue> {
            if (availableArgs.containsKey(it.option) &&
                    availableArgs[it.option]!!.hasValue &&
                        availableArgs[it.option]!!.isActive) {

                availableArgs[it.option]!!.value = it.text

            }

            fire(ArgumentsRequest.GetActiveArguments())
        }

        subscribe<ArgumentsRequest.SaveArgumentsSettings> {

            val gw2UserDir = if (gw2 != null) {
                gw2!!.configPath.value.replace('\\', '/')
            } else {
                SystemUtils.GW2UserDirectory()
            }

            val gw2LocalSettingsPath = "$gw2UserDir/${Nomenclatures.File.GW2LocalSettingsJson}"

            val activeOptionsAsList = availableArgs.values
                    .filter { it.isActive }
                    .filter { !it.hasValue || (it.hasValue && it.value.isNotEmpty()) }
                    .map {
                        if (it.hasValue) "${it.name}:${it.value}" else it.name
                    }.toList()

            GW2LocalSettings(activeOptionsAsList).saveAsJson(gw2LocalSettingsPath)
        }
    }

    override fun initViewElements() {
        super.initViewElements()
        fire(ArgumentsRequest.GetActiveArguments())
    }

    override fun setAvailableItems(items: GW2Arguments) {

        availableArgs = items.arguments.map { it.name to it }
                .toMap().withDefault { GW2Argument()}

        availableArgs = ConcurrentHashMap(availableArgs)

        if (activeArgs.arguments.isNotEmpty()) {
            setActiveItems(activeArgs)
        }

    }

    override fun setActiveItems(items: GW2LocalSettings) {

        if (availableArgs.isNotEmpty()) {
            items.arguments.map{
                with(it.split(':')) {
                    this.first() to this.last()
                }
            }.filter {
                availableArgs.containsKey(it.first)
            }.forEach {
                availableArgs[it.first]!!.isActive = true
                if (availableArgs[it.first]!!.hasValue) {
                    availableArgs[it.first]!!.value = it.second
                }
            }

            initViewElements()

        } else {
            activeArgs = items
        }

    }

    override fun setGW2Application(gw2: Application) {
        this.gw2 = gw2
    }

}