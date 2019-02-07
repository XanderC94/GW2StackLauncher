package view.logics

import com.teamdev.jxbrowser.chromium.Browser
import com.teamdev.jxbrowser.chromium.BrowserContext
import com.teamdev.jxbrowser.chromium.BrowserPreferences
import com.teamdev.jxbrowser.chromium.BrowserType
import com.teamdev.jxbrowser.chromium.javafx.BrowserView
import events.AddOnsEvent
import events.BrowserEvent
import events.BrowserRequest
import events.NoRequest
import model.objects.GW2AddOn
import tornadofx.*
import view.GW2StackLauncherView

class BrowserLogic (val view: GW2StackLauncherView) {

    private val browserView : BrowserView
    private val browser : Browser
    private val userAgent =
            "Mozilla/5.0 (Linux; Android 8.0.0; wv) Mobile " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Mobile " +
                    "Chrome/71.0.3578.99 Mobile " +
                    "Safari/537.36 Mobile"

    private var lastSelectedAddOn : GW2AddOn = GW2AddOn()

    init {

        BrowserPreferences.setUserAgent(userAgent)
        browser = Browser(BrowserType.LIGHTWEIGHT, BrowserContext.defaultContext())
        browserView = BrowserView(browser)

        view.webViewAnchor.add(browserView)

        browserView.anchorpaneConstraints {
            leftAnchor = 0.0
            rightAnchor = 0.0
            topAnchor = 0.0
            bottomAnchor = 0.0
        }

        handlers()

        subscriptions()

        view.fire(BrowserEvent.BrowserInstance(NoRequest(), browser))
    }

    private fun handlers() {
        with(view) {

        }
    }

    private fun subscriptions() {
        with(view) {

            subscribe<BrowserRequest.UpdateBrowser> {
                fire(BrowserEvent.BrowserInstance(NoRequest(), browser))
            }

            subscribe<AddOnsEvent.AddOn> {
                lastSelectedAddOn = it.addOn
            }
        }
    }
}