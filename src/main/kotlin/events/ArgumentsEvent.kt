package events

import model.objects.GW2Argument

object ArgumentsRequest {

    class LoadAvailableArguments(val path: String) : GenericRequest()

    class LoadActiveArguments(val path: String) : GenericRequest()

    class GetAvailableArguments : GenericRequest()

    class GetActiveArguments : GenericRequest()

    class GetArgument(val id : String) : GenericRequest()

    class UpdateArgumentStatus(val id: String, val isActive: Boolean) : GenericRequest()

    class UpdateArgumentValue(val id: String, val text: String) : GenericRequest()

    class SaveArgumentsSettings : GenericRequest()
}

object ArgumentsEvent {

    data class ArgumentsList(
            override val from: Request,
            val options: List<GW2Argument>
    ) : GenericEvent()

    data class Argument(
            override val from: Request,
            val argument: GW2Argument
    ) : GenericEvent()


}
