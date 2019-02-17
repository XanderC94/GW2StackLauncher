package controller

import events.ArgumentsEvent
import events.ArgumentsRequest
import extentions.saveAsJson
import model.ontologies.gw2.Application
import model.ontologies.gw2.Argument
import model.ontologies.gw2.ArgumentsWrapper
import model.ontologies.gw2.LocalArguments
import model.utils.Nomenclatures
import model.utils.SystemUtils
import java.util.concurrent.ConcurrentHashMap

@Suppress("MapGetWithNotNullAssertionOperator")
class ArgumentsController : ViewController(), ItemController<ArgumentsWrapper, LocalArguments>, GW2Dipper {

    private var availableArgs: Map<String, Argument> = ConcurrentHashMap()
    private var activeArgs: LocalArguments = LocalArguments()
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
            if (availableArgs.containsKey(it.id)) {
                availableArgs[it.id]!!.isActive = it.isActive
                fire(ArgumentsRequest.GetActiveArguments())
            }
        }

        subscribe<ArgumentsRequest.UpdateArgumentValue> {
            if (availableArgs.containsKey(it.id) &&
                    availableArgs[it.id]!!.hasValue &&
                        availableArgs[it.id]!!.isActive) {

                availableArgs[it.id]!!.value = it.text

            }

            fire(ArgumentsRequest.GetActiveArguments())
        }

        subscribe<ArgumentsRequest.SaveArgumentsSettings> {

            val gw2ConfigDir =
                    gw2?.configPath?.value?.replace('\\', '/')
                            ?: SystemUtils.GW2UserDirectory()

            val gw2LocalSettingsPath = "$gw2ConfigDir/${Nomenclatures.File.GW2LocalSettingsJson}"

            val activeOptionsAsList = availableArgs.values
                    .filter { it.isActive }
                    .filter { !it.hasValue || (it.hasValue && it.value.isNotEmpty()) }
                    .map {
                        if (it.hasValue) "${it.name}:${it.value}" else it.name
                    }.toList()

            LocalArguments(activeOptionsAsList).saveAsJson(gw2LocalSettingsPath)

            log.info("Setting.json saved!")
        }
    }

    override fun onReady() {
        super.onReady()
        fire(ArgumentsRequest.GetActiveArguments())
    }

    override fun setAvailableItems(items: ArgumentsWrapper) {

        availableArgs = items.arguments.map { it.name to it }
                .toMap().withDefault { Argument() }

        availableArgs = ConcurrentHashMap(availableArgs)

        if (activeArgs.arguments.isNotEmpty()) {
            setActiveItems(activeArgs)
        }

    }

    override fun setActiveItems(items: LocalArguments) {

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

            onReady()

        }

        activeArgs = items

    }

    override fun setGW2Application(gw2: Application) {
        this.gw2 = gw2
    }

}