package controller

import events.GFXEvent
import events.GFXRequest
import model.objects.GW2GFXSettings
import tornadofx.*

class GFXController : Controller() {

    private lateinit var gfx: GW2GFXSettings

    init {
        subscribe<GFXRequest.UpdateInstallLocation> {
            fire(GFXEvent.InstallLocation(it, gfx.application.installPath))
        }
    }

    fun setGFXSettings(gfx: GW2GFXSettings) {
        this.gfx = gfx
        fire(GFXRequest.UpdateInstallLocation())
    }

}