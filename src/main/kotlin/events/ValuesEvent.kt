package events

object ValuesRequest {
    class GetArgValues(val id:String) : GenericRequest()
}

object ValuesEvent {
    class ArgValues<T>(
            override val from: Request,
            val arg : String,
            val values: List<T>
    ) : GenericEvent()

    class ArgValue<T>(
            override val from: Request,
            val arg : String,
            val value: T
    ) : GenericEvent()
}