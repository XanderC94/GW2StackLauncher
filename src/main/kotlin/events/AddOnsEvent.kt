package events

import model.objects.GW2AddOn

object AddonsRequest{
    class UpdateAvailableAddOns: GenericRequest()
    class UpdateActiveAddOns: GenericRequest()

}

object AddonsEvent {
    class AddOnsList(
            override val from: Request,
            val addOns: List<GW2AddOn>
    ) : GenericEvent()
}