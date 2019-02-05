package events

object AppRequest {
    class CloseApplication(val exitCode: Int) : GenericRequest()
}

object AppEvents {
    class OptionControllerReady(
            override val from: Request = NoRequest()
    ) : GenericEvent()
}