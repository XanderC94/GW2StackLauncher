package events

import model.objects.GW2Argument
import java.nio.file.Path

object ArgumentsRequest {

    class LoadAvailableArguments(val path: Path) : GenericRequest()

    class LoadActiveArguments(val path: Path) : GenericRequest()

    class GetAvailableArguments : GenericRequest()

    class GetActiveArguments : GenericRequest()

    class GetArgument(val id : String) : GenericRequest()

    class UpdateArgumentStatus(val option: String, val isActive: Boolean) : GenericRequest()

    class UpdateArgumentValue(val option: String, val text: String) : GenericRequest()

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
