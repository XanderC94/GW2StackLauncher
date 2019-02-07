package events

import com.teamdev.jxbrowser.chromium.Browser

object BrowserRequest {
    class UpdateBrowser : GenericRequest()
    class LoadURL (val url: String) : GenericRequest()
    class CloseBrowser : GenericRequest()
}

object BrowserEvent {

    class BrowserInstance(
            override val from: Request,
            val browser: Browser
    ) : GenericEvent()
}