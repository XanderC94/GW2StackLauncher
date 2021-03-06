package controller

import events.AboutRequest

class AboutController : ViewController() {

    init {
        subscribe<AboutRequest.OpenLink> {
            hostServices.showDocument(it.url.toURI().toASCIIString())
        }

        onReady()
    }
}