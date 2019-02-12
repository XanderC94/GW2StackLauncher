package view.logics

import com.teamdev.jxbrowser.chromium.*
import com.teamdev.jxbrowser.chromium.javafx.BrowserView
import events.BrowserEvent
import events.BrowserRequest
import events.SearchRequest
import javafx.event.EventHandler
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.stage.StageStyle
import tornadofx.*
import view.GW2SLMainView
import view.GW2SLSearch
import view.components.Action


class BrowserViewLogic (val view: GW2SLMainView) {

    val addOnsBrowserView : BrowserView
    val aboutBrowserView : BrowserView
    val browser : Browser
    val userAgent =
            "Mozilla/5.0 (Linux; Android 8.0.0; wv) Mobile " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Mobile " +
                    "Chrome/71.0.3578.99 Mobile " +
                    "Safari/537.36 Mobile"

    init {

        System.setProperty("jxbrowser.chromium.sandbox", "true")
        BrowserPreferences.setUserAgent(userAgent)
        browser = Browser(BrowserType.HEAVYWEIGHT, BrowserContext.defaultContext())
        addOnsBrowserView = BrowserView(browser)
        aboutBrowserView = BrowserView(browser)

        view.webViewAnchor.add(addOnsBrowserView)
        view.aboutAnchor.add(aboutBrowserView)

        addOnsBrowserView.anchorpaneConstraints {
            leftAnchor = 0.0
            rightAnchor = 0.0
            topAnchor = 0.0
            bottomAnchor = 0.0
        }
        aboutBrowserView.anchorpaneConstraints {
            leftAnchor = 0.0
            rightAnchor = 0.0
            topAnchor = 0.0
            bottomAnchor = 0.0
        }

        handlers()

        subscriptions()
    }

    private fun handlers() {
        addOnsBrowserView.onKeyPressed = EventHandler {
            BrowserAction(this, it)
        }

        aboutBrowserView.onKeyPressed = EventHandler {
            BrowserAction(this, it)
        }
    }

    private fun subscriptions() {
        with(view) {

            subscribe<BrowserRequest.LoadURL> { r ->
                log.info(r.url)
                if(browser.url != r.url) {
                    browser.loadURL(r.url)
                }
            }

            subscribe<SearchRequest.Find> {
                browser.findText(SearchParams(it.string, it.matchCase, it.next)) {}
            }

            subscribe<BrowserRequest.CloseBrowser> {
                try {
                    browser.stop()
                    browser.dispose()
                } catch (ex: Throwable) {

                } finally {
                    fire(BrowserEvent.BrowserClosed(it))
                }
            }
        }
    }

    private enum class BrowserAction (
            override val combo : KeyCodeCombination,
            override val strategy : (Any) -> Unit) : Action<Any> {

        Reload(KeyCodeCombination(KeyCode.F5), { bv ->
            if (bv is BrowserViewLogic) {
                bv.browser.reload()
            }

        }),
        Forward(KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN), {
            if (it is BrowserViewLogic && it.browser.canGoForward()) {
                val index = it.browser.currentNavigationEntryIndex
                it.browser.goToIndex(index + 1)
            }
        }),
        Backward(KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN), {

            if (it is BrowserViewLogic && it.browser.canGoBack()) {
                val index = it.browser.currentNavigationEntryIndex
                it.browser.goToIndex(index - 1)
            }
        }),
        Find(KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN), {

            if (it is BrowserViewLogic) {
                GW2SLSearch.openWindow(StageStyle.DECORATED)
                it.view.fire(SearchRequest.Open(it.browser.selectedText))
            }
        }),
        ClearFind(KeyCodeCombination(KeyCode.ESCAPE), {
            if (it is BrowserViewLogic) {
                it.browser.stopFindingText(StopFindAction.CLEAR_SELECTION)
            }
        });

        override fun invoke(browser: Any) {
            this.strategy(browser)
        }

        companion object : (Any, KeyEvent) -> Unit {

            override fun invoke(browser: Any, combo: KeyEvent) {
                val action = get(combo)
                if (action != null) {
                    action(browser)
                }
            }

            private fun get(event: KeyEvent) : BrowserAction? {
                return BrowserAction.values().firstOrNull { it.combo.match(event) }
            }
        }

        fun toMenuItem(browser: Any) : MenuItem {

            val name = "${this.name}\t\t${this.combo.displayText}"

            return object : MenuItem(name) {
                init {
                    this.onAction = EventHandler {
                        this@BrowserAction(browser)
                    }
                }
            }
        }
    }
}