package controller

import events.GFXEvent
import events.GFXRequest
import model.objects.GW2GFXSettings

class GFXController : ViewController() {

    private var gfx: GW2GFXSettings? = null

    init {
        subscribe<GFXRequest.UpdateInstallLocation> {
            if (gfx != null) {
                fire(GFXEvent.InstallLocation(it, gfx!!.application.installPath))
            }
        }
    }

    override fun initViewElements() {
        super.initViewElements()
        fire(GFXRequest.UpdateInstallLocation())
    }

    fun setGFXSettings(gfx: GW2GFXSettings) {
        this.gfx = gfx

        initViewElements()
    }

}