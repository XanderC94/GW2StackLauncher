package controller

import events.AddOnsEvent
import events.AddOnsRequest
import model.objects.GW2AddOn
import model.objects.GW2AddOns
import model.objects.GW2LocalAddOn
import model.objects.GW2LocalAddOns
import model.utils.Nomenclatures
import model.utils.SystemUtils
import model.utils.saveAsJson
import java.util.concurrent.ConcurrentHashMap


class AddOnsController : ViewController(), ItemController<GW2AddOns, GW2LocalAddOns> {

    private var availableAddOns: Map<String, GW2AddOn> = ConcurrentHashMap()
    private var activeAddOns: GW2LocalAddOns = GW2LocalAddOns()

    init {
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
                    GW2LocalAddOn(it.name, "")
                }

                val gw2LocalAddOnsPath = "${SystemUtils.gw2slDir()!!}/${Nomenclatures.File.GW2LocalAddonsJson}"

                GW2LocalAddOns(asList).saveAsJson(gw2LocalAddOnsPath)

                log.info("AddOns.local.json saved!")

                fire(AddOnsRequest.DownloadAndUpdateAddOns(availableAddOns.values.toList(), activeAddOns.addOns))

            }
        }
    }

    override fun initViewElements() {
        super.initViewElements()
//        fire(AddOnsRequest.GetAvailableAddOns())
        fire(AddOnsRequest.GetActiveAddOns())
    }

    override fun setAvailableItems(items: GW2AddOns) {

        availableAddOns = items.addOns.map { it.name to it }
                .toMap().withDefault { GW2AddOn() }

        availableAddOns = ConcurrentHashMap(availableAddOns)

        if (activeAddOns.addOns.isNotEmpty()) {
            setActiveItems(activeAddOns)
        }
    }

    override fun setActiveItems(items: GW2LocalAddOns) {
        if (availableAddOns.isNotEmpty()) {

            items.addOns.filter {
                availableAddOns.containsKey(it.name)
            }.forEach {
                availableAddOns[it.name]!!.isActive = true
            }

            initViewElements()
        }

        activeAddOns = items

    }
}