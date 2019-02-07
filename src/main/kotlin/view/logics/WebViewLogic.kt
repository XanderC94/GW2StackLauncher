package view.logics

import events.AddOnsEvent
import events.AppRequest
import view.GW2StackLauncherView

class WebViewLogic (val view: GW2StackLauncherView) {

    private val browser = view.addOnsWebView.engine
    private val userAgent =
            "Mozilla/5.0 (Linux; Android 8.0.0; wv) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/71.0.3578.99 Mobile " +
            "Safari/537.36"

    init {

        browser.userAgent = userAgent

        with(view) {

            subscribe<AddOnsEvent.AddOn> {

                if(browser.location != it.addOn.info) {
                    log.info(it.addOn.info)
                    browser.load(it.addOn.info)
                }

            }

            subscribe<AppRequest.CloseApplication> {
                try {
                    browser.loadWorker.cancel()
                } catch (ex: Exception) {}

            }
        }
    }
}