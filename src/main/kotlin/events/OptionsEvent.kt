package events

import model.objects.GW2Argument
import java.nio.file.Path

object OptionsRequest {

    class LoadAvailableOptionsList(val path: Path) : GenericRequest()

    class LoadActiveOptionsList(val path: Path) : GenericRequest()

    class GetAvailableOptionsList : GenericRequest()

    class GetActiveOptionsList : GenericRequest()

    class GetOption(val id : String) : GenericRequest()

    class UpdateOptionStatus(val option: String, val isActive: Boolean) : GenericRequest()

    class UpdateOptionValue(val option: String, val text: String) : GenericRequest()

    class SaveOptionsSettings : GenericRequest()
}

object OptionsEvent {

    data class OptionsList(
            override val from: Request,
            val options: List<GW2Argument>
    ) : GenericEvent()

    data class Option(
            override val from: Request,
            val option: GW2Argument
    ) : GenericEvent()


}
