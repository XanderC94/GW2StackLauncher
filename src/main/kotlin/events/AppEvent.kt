package events

import model.ontologies.Overlay

object AppRequest {
    class RunGW2(val overlays : Map<String, Overlay>) : GenericRequest()
    class CloseApplication(val exitCode: Int) : GenericRequest()
    class InitViewElements : GenericRequest()
}

object AppEvents {

//    class ControllerReady(
//            override val from: Request = NoRequest(),
//            val name : String
//    ) : GenericEvent()
}