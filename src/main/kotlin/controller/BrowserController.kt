package controller

import com.teamdev.jxbrowser.chromium.Browser
import events.AddOnsEvent
import events.AppRequest
import events.BrowserEvent
import events.BrowserRequest

class BrowserController : ViewController() {

    private var browser : Browser? = null

    init {

        subscribe<BrowserEvent.BrowserInstance> {
            browser = it.browser
        }

        subscribe<AddOnsEvent.AddOn> {

            if(browser != null && browser!!.url != it.addOn.info) {
                log.info("Banane")
                browser!!.loadURL(it.addOn.info)
            }
        }

        subscribe<AppRequest.CloseApplication> {

            if (browser != null) {
                try {
                    browser!!.stop()
                    browser!!.dispose()
                } catch (ex: Exception) {}
            }

        }

        initViewElements()
    }

    override fun initViewElements() {
        super.initViewElements()
        fire(BrowserRequest.UpdateBrowser())
    }
}