package controller

import events.AddOnsEvent
import events.AddOnsRequest
import extentions.saveAsJson
import model.ontologies.gw2.AddOn
import model.ontologies.gw2.AddOnsWrapper
import model.ontologies.gw2.LocalAddOn
import model.ontologies.gw2.LocalAddOnsWrapper
import model.utils.Nomenclatures
import model.utils.SystemUtils
import java.util.concurrent.ConcurrentHashMap


class AddOnsController : ViewController(), ItemController<AddOnsWrapper, LocalAddOnsWrapper> {

    private var availableAddOns: Map<String, AddOn> = ConcurrentHashMap()
    private var activeAddOns: LocalAddOnsWrapper = LocalAddOnsWrapper()

    init {

        subscribe<AddOnsRequest.UpdateActiveAddOns> {
            this@AddOnsController.setActiveItems(it.addOns)
        }

        subscribe<AddOnsRequest.GetAvailableAddOns> {
            fire(AddOnsEvent.AddOnsList(it, availableAddOns.values.toList()))
        }

        subscribe<AddOnsRequest.GetActiveAddOns> {
            fire(AddOnsRequest.GetAvailableAddOns())
            fire(AddOnsEvent.AddOnsList(it, availableAddOns.values.filter { it.isActive }))
        }

        subscribe<AddOnsRequest.UpdateAddOnStatus> {
            if (availableAddOns.containsKey(it.id)) {

                availableAddOns[it.id]!!.isActive = it.status

                if (availableAddOns[it.id]!!.isActive) {
                    availableAddOns[it.id]!!.bindings
                            .filter { availableAddOns.containsKey(it) }
                            .filter { !availableAddOns[it]!!.isActive }
                            .forEach { availableAddOns[it]!!.isActive = true }
                } else {
                    availableAddOns.values
                            .filter { addOn -> addOn.bindings.contains(it.id)}
                            .forEach { addOn -> addOn.isActive = false }
                }

                fire(AddOnsRequest.GetActiveAddOns())
            }
        }

        subscribe<AddOnsRequest.GetAddOn> {
            if (availableAddOns.containsKey(it.id)) {
                fire(AddOnsEvent.AddOn(it, availableAddOns[it.id]!!))
            }
        }

        subscribe<AddOnsRequest.SaveAddOnsSettings> {

            runAsync {

                val asList = availableAddOns.values.filter { it.isActive }.map {
                    LocalAddOn(it.name, "")
                }

                val gw2LocalAddOnsPath = "${SystemUtils.gw2slDir()!!}/${Nomenclatures.File.GW2LocalAddonsJson}"

                LocalAddOnsWrapper(asList).saveAsJson(gw2LocalAddOnsPath)

                log.info("AddOns.local.json saved!")

                fire(AddOnsRequest.DownloadAndUpdateAddOns(availableAddOns.values.toList(), activeAddOns.addOns))

            }
        }
    }

    override fun onReady() {
        super.onReady()
//        fire(AddOnsRequest.GetAvailableAddOns())
        fire(AddOnsRequest.GetActiveAddOns())
    }

    override fun setAvailableItems(items: AddOnsWrapper) {

        availableAddOns = items.addOns.map { it.name to it }
                .toMap().withDefault { AddOn() }

        availableAddOns = ConcurrentHashMap(availableAddOns)

        if (activeAddOns.addOns.isNotEmpty()) {
            setActiveItems(activeAddOns)
        }
    }

    override fun setActiveItems(items: LocalAddOnsWrapper) {
        if (availableAddOns.isNotEmpty()) {

            items.addOns.filter {
                availableAddOns.containsKey(it.name)
            }.forEach {
                availableAddOns[it.name]!!.isActive = true
            }

            onReady()
        }

        activeAddOns = items

    }
}