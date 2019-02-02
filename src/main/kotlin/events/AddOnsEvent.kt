package events

import tornadofx.*

object AddonsRequest{
    object InitAvailableAddOnsList: FXEvent(EventBus.RunOn.BackgroundThread)

}

object AddonsEvent {
    class AddOnsList(val arguments: List<Any>) : FXEvent()
}