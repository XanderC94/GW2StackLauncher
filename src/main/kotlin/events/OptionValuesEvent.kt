package events

object OptionValuesRequest {
    class GetOptionValues(val id:String) : GenericRequest()
}

object OptionValuesEvent {
    class OptionValues<T>(
            override val from: Request,
            val values: List<T>
    ) : GenericEvent()

    class OptionValue<T>(
            override val from: Request,
            val value: T
    ) : GenericEvent()
}