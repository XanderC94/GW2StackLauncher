package view.logics

import com.teamdev.jxbrowser.chromium.Browser
import com.teamdev.jxbrowser.chromium.BrowserContext
import com.teamdev.jxbrowser.chromium.BrowserPreferences
import com.teamdev.jxbrowser.chromium.BrowserType
import com.teamdev.jxbrowser.chromium.javafx.BrowserView
import events.AddOnsEvent
import events.AppRequest
import javafx.event.EventHandler
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import model.objects.GW2AddOn
import tornadofx.*
import view.GW2StackLauncherView

class BrowserViewLogic (val view: GW2StackLauncherView) {

    private val browserView : BrowserView
    private val browser : Browser
    private val userAgent =
            "Mozilla/5.0 (Linux; Android 8.0.0; wv) Mobile " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Mobile " +
                    "Chrome/71.0.3578.99 Mobile " +
                    "Safari/537.36 Mobile"

    private var lastSelectedAddOn : GW2AddOn = GW2AddOn()

    init {

        System.setProperty("jxbrowser.chromium.sandbox", "true")
        BrowserPreferences.setUserAgent(userAgent)
        browser = Browser(BrowserType.HEAVYWEIGHT, BrowserContext.defaultContext())
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
    }

    private fun handlers() {
        with(view) {
            browserView.onKeyPressed = EventHandler {
                BrowserAction(browser, it)
            }
        }
    }

    private fun subscriptions() {
        with(view) {

            subscribe<AddOnsEvent.AddOn> { r ->

                if(browser.url != r.addOn.info) {
                    lastSelectedAddOn = r.addOn
                    Browser.invokeAndWaitFinishLoadingMainFrame(browser) {
                        it.loadURL(r.addOn.info)
                    }

                }
            }

            subscribe<AppRequest.CloseApplication> {
                try {
                    browser.stop()
                    browser.dispose()
                } catch (ex: Exception) {}
            }
        }
    }

    enum class BrowserAction (
            private val keyCombo : KeyCodeCombination,
            private val strategy : (Browser) -> Unit) : (Browser) -> Unit {

        Reload(KeyCodeCombination(KeyCode.F5), { browser ->
            browser.reload()
        }),
        Forward(KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN), { browser ->
            if (browser.canGoForward()) {
                val index = browser.currentNavigationEntryIndex
                browser.goToIndex(index + 1)
            }
        }),
        Backward(KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN), { browser ->
            if (browser.navigationEntryCount > 2) {
                val index = browser.currentNavigationEntryIndex
                browser.goToIndex(index - 1)
            }
        });

        override fun invoke(browser: Browser) {
            this.strategy(browser)
        }

        companion object : (Browser, KeyEvent) -> Unit{
            override fun invoke(browser: Browser, combo: KeyEvent) {
                val action = get(combo)
                if (action != null) {
                    action.strategy(browser)
                }
            }

            private fun get(event: KeyEvent) : BrowserAction? {
                return BrowserAction.values().firstOrNull { it.keyCombo.match(event) }
            }
        }

        fun toMenuItem(browser: Browser) : MenuItem {

            val name = "${this.name}\t\t${this.keyCombo.displayText}"

            return object : MenuItem(name) {
                init {
                    this.onAction = EventHandler {
                        this@BrowserAction.strategy(browser)
                    }
                }
            }
        }
    }
}