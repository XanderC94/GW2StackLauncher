package events

import tornadofx.*

interface Event {
    val from: Request
}

abstract class GenericEvent : FXEvent(), Event