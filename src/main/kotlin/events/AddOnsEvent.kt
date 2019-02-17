package events

import model.ontologies.gw2.AddOn
import model.ontologies.gw2.LocalAddOn
import model.ontologies.gw2.LocalAddOnsWrapper

object AddOnsRequest{
    class GetAvailableAddOns: GenericRequest()
    class GetActiveAddOns: GenericRequest()
    class UpdateActiveAddOns(val addOns: LocalAddOnsWrapper): GenericRequest()
    class UpdateAddOnStatus(val id: String, val status: Boolean): GenericRequest()
    class GetAddOn(val id: String): GenericRequest()
    class SaveAddOnsSettings : GenericRequest()
    class DownloadAndUpdateAddOns(val addons: List<AddOn>, val lastActive: List<LocalAddOn>) : GenericRequest()
}

object AddOnsEvent {
    class AddOnsList(
            override val from: Request,
            val addOns: List<model.ontologies.gw2.AddOn>
    ) : GenericEvent()

    class AddOn(
            override val from: Request,
            val addOn: model.ontologies.gw2.AddOn
    ) : GenericEvent()
}