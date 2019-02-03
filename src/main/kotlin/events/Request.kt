package events

import tornadofx.*

interface Request {
    val signature: String
}

abstract class GenericRequest : FXEvent(EventBus.RunOn.BackgroundThread), Request {
    override val signature: String = this::class.java.name
}