package events

object AppRequest {
    class CloseApplication(val exitCode: Int) : GenericRequest()
    class InitViewElements : GenericRequest()
}

object AppEvents {

//    class ControllerReady(
//            override val from: Request = NoRequest(),
//            val name : String
//    ) : GenericEvent()
}