package events

import model.objects.GW2AddOn
import model.objects.GW2Argument

object AddOnsRequest{
    class GetAvailableAddOns: GenericRequest()
    class GetActiveAddOns: GenericRequest()
    class UpdateAddOnStatus(val id: String, val status: Boolean): GenericRequest()
    class GetAddOn(val id: String): GenericRequest()
    class SaveAddOnsSettings : GenericRequest()
    class DownloadAndUpdateAddOns(addons: List<GW2Argument>, lastActive: List<String>) : GenericRequest()
}

object AddOnsEvent {
    class AddOnsList(
            override val from: Request,
            val addOns: List<GW2AddOn>
    ) : GenericEvent()

    class AddOn(
            override val from: Request,
            val addOn: GW2AddOn
    ) : GenericEvent()
}