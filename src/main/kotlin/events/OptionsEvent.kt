package events

import model.objects.GW2Argument

object OptionsRequest {


    class UpdateAvailableOptionsList : GenericRequest()

    class UpdateActiveOptionsList : GenericRequest()

    class UpdateOptionInfoDisplay(val id : String) : GenericRequest()

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
