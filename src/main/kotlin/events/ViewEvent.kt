package events

import model.ontologies.gw2.Argument

object ViewRequest {
    class CheckArgumentTextField(val arg: Argument) : GenericRequest()
    class HideArgumentValueHeader : GenericRequest()
}

object ViewEvent {
    class TextFieldStatus(
            override val from: Request,
            val ok: Boolean
    ) : GenericEvent()
}