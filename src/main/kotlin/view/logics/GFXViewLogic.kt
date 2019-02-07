package view.logics

import events.GFXEvent
import view.GW2StackLauncherView

class GFXViewLogic(private val view : GW2StackLauncherView) {

    init {
        with(view) {

            gw2LocationField.isEditable = false
            gw2LocationField.isFocusTraversable = false

            subscribe<GFXEvent.InstallLocation> {
                gw2LocationField.text = it.installPath.value
            }
        }
    }
}