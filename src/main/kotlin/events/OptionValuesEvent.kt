package events

object OptionValuesRequest {
    class GetOptionValues(val id:String) : GenericRequest()
}

object OptionValuesEvent {
    class OptionValues(
            override val from: Request,
            val values: List<Pair<String, Long>>
    ) : GenericEvent()
}