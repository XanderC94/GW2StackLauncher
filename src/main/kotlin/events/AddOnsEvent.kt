package events

import model.objects.GW2AddOn

object AddOnsRequest{
    class UpdateAvailableAddOns: GenericRequest()
    class UpdateActiveAddOns: GenericRequest()
    class UpdateAddOnStatus(val id: String, val status: Boolean): GenericRequest()
    class GetAddOn(val id: String): GenericRequest()
    class ClearWebView : GenericRequest()
    class SaveAddOnsSettings : GenericRequest()
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