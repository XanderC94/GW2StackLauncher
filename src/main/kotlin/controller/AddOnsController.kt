package controller

import events.AddonsEvent
import events.AddonsRequest
import model.objects.GW2AddOn
import model.objects.GW2AddOns
import model.objects.GW2LocalAddOns
import tornadofx.*

class AddOnsController : Controller() {

    private var availableAddOns: Map<String, GW2AddOn> = mapOf()

    init {
        subscribe<AddonsRequest.UpdateAvailableAddOns> {
            fire(AddonsEvent.AddOnsList(it, availableAddOns.values.toList()))
        }

        subscribe<AddonsRequest.UpdateActiveAddOns> {
            fire(AddonsEvent.AddOnsList(it, availableAddOns.values.filter { it.isActive }))
        }

    }

    fun setAvailableAddOns(addOns: GW2AddOns) {
        this.availableAddOns = addOns.addOns.map { it.name to it }.toMap()
        fire(AddonsRequest.UpdateAvailableAddOns())
    }

    fun setActiveAddOns(addOns: GW2LocalAddOns) {
//        this.availableAddOns = addOns.addOns.map { it.name to it }.toMap()
//        fire(AddonsRequest.UpdateAvailableAddOns())
    }
}