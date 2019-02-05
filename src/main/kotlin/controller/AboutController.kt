package controller

import events.AboutRequest
import tornadofx.*

class AboutController : Controller() {

    init {
        subscribe<AboutRequest.OpenLink> {
            hostServices.showDocument(it.url.toURI().toASCIIString())
        }
    }
}