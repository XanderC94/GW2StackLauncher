package view.logics

import events.AddonsEvent
import events.AddonsRequest
import tornadofx.*
import view.GW2StackLauncherView

class AddOnsViewLogic(view: GW2StackLauncherView) {

    init {

        with(view) {
            subscribe<AddonsEvent.AddOnsList> {
                when(it.from) {
                    is AddonsRequest.UpdateAvailableAddOns -> {
                        availableAddOnsList.items = it.addOns.map { it.name to it.isActive }.observable()
                    }
                    is AddonsRequest.UpdateActiveAddOns -> {
                        activeAddOnsList.items = it.addOns.filter { it.isActive }.map { it.name }.observable()
                    }
                }
            }
        }

    }
}