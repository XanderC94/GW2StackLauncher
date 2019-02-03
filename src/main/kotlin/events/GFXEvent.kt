package events

import model.objects.InstallPath

object GFXRequest {

    class UpdateInstallLocation : GenericRequest()

}

object GFXEvent {

    class InstallLocation(
            override val from: Request,
            val installPath: InstallPath
    ) : GenericEvent()

}