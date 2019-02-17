package controller

import events.GFXEvent
import events.GFXRequest
import model.ontologies.gw2.GW2GFXSettings

class GFXController : ViewController() {

    private var gfx: GW2GFXSettings? = null

    init {
        subscribe<GFXRequest.UpdateInstallLocation> {
            if (gfx != null) {
                fire(GFXEvent.InstallLocation(it, gfx!!.application.installPath))
            }
        }
    }

    override fun onReady() {
        super.onReady()
        fire(GFXRequest.UpdateInstallLocation())
    }

    fun setGFXSettings(gfx: GW2GFXSettings) {
        this.gfx = gfx
        onReady()
    }

}