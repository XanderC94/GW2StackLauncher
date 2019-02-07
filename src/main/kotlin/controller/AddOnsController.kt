package controller

import events.AddOnsEvent
import events.AddOnsRequest
import model.objects.Application
import model.objects.GW2AddOn
import model.objects.GW2AddOns
import model.objects.GW2LocalAddOns
import java.util.concurrent.ConcurrentHashMap

class AddOnsController : ViewController(), ItemController<GW2AddOns, GW2LocalAddOns>, GW2Dipper {

    private var availableAddOns: Map<String, GW2AddOn> = ConcurrentHashMap()
    private var activeOptions: GW2LocalAddOns = GW2LocalAddOns()

    private var gw2: Application? = null

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

                fire(AddOnsRequest.GetActiveAddOns())
            }
        }

        subscribe<AddOnsRequest.GetAddOn> {
            if (availableAddOns.containsKey(it.id)) {
                fire(AddOnsEvent.AddOn(it, availableAddOns[it.id]!!))
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

        if (activeOptions.addOns.isNotEmpty()) {
            setActiveItems(activeOptions)
        }
    }

    override fun setActiveItems(items: GW2LocalAddOns) {
        if (availableAddOns.isNotEmpty()) {

            items.addOns.filter {
                availableAddOns.containsKey(it)
            }.forEach {
                availableAddOns[it]!!.isActive = true
            }

            initViewElements()
        } else {
            activeOptions = items
        }

    }

    override fun setGW2Application(gw2: Application) {
        this.gw2 = gw2
    }
}