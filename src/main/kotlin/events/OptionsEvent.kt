package events

import model.json.objects.GW2Argument
import tornadofx.*
import tornadofx.EventBus.RunOn.BackgroundThread

interface Request {
    val signature: String
}

interface Event {
    val from: Request
}

object OptionsRequest {


    class UpdateAvailableOptionsList : Request, FXEvent(BackgroundThread) {

        override val signature : String = this::class.java.name

    }

    class UpdateActiveOptionsList : Request, FXEvent(BackgroundThread) {

        override val signature : String = this::class.java.name

    }

    class UpdateOptionInfoDisplay(val id : String) : Request, FXEvent(BackgroundThread) {

        override val signature : String = this::class.java.name

    }

    class UpdateOptionStatus(val option: String, val isActive: Boolean) : Request, FXEvent(BackgroundThread) {

        override val signature: String = this::class.java.name

    }

    class UpdateOptionValue(val option: String, val text: String) : Request, FXEvent(BackgroundThread) {

        override val signature: String = this::class.java.name

    }

    class SaveOptionsSettings() : Request, FXEvent(BackgroundThread) {

        override val signature: String = this::class.java.name

    }


}

object OptionsEvent {

    data class OptionsList(
            override val from: Request,
            val options: List<GW2Argument>
    ) : FXEvent(), Event

    data class Option(
            override val from: Request,
            val option: GW2Argument
    ) : FXEvent(), Event

}
