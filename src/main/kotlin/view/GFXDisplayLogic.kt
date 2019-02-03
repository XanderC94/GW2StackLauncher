package view

import events.GFXEvent
import events.GFXRequest

class GFXDisplayLogic(private val view : GW2StackLauncherView) {

    init {
        with(view) {

            gw2LocationField.isEditable = false

            subscribe<GFXEvent.InstallLocation> {
                gw2LocationField.text = it.installPath.value
                gw2LocationField.isFocusTraversable = false
            }

            fire(GFXRequest.UpdateInstallLocation())

        }
    }
}