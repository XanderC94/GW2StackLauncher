package events

object BrowserRequest {
    class LoadURL (val url: String) : GenericRequest()
    class CloseBrowser : GenericRequest()
    class ClearWebView : GenericRequest()
    class DisplayWebView : GenericRequest()
}

object BrowserEvent {
    class BrowserClosed(
            override val from: Request
    ) : GenericEvent()
}