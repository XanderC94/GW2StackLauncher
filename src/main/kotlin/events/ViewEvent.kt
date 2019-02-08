package events

import model.objects.GW2Argument

object ViewRequest {
    class CheckArgumentTextField(val arg: GW2Argument) : GenericRequest()
    class HideArgumentValueHeader : GenericRequest()
}

object ViewEvent {
    class TextFieldStatus(
            override val from: Request,
            val ok: Boolean
    ) : GenericEvent()
}