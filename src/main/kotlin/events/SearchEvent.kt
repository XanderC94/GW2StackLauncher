package events

object SearchRequest {

    class Find(val string: String, val matchCase: Boolean, val next: Boolean) : GenericRequest()
    class Next : GenericRequest()
    class Open(val string: String) : GenericRequest()
    class ClearSearch : GenericRequest()
}

object SearchEvent {
    class Found(val n:Int)
}