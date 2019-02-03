package view

import events.OptionsRequest
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import tornadofx.*

class GW2StackLauncherView : View("GW2 Stack Launcher") {

    override val root: BorderPane by fxml()

    /* OPTIONS */
    val availableOptionsList : ListView<Pair<String, Boolean>> by fxid("OptionsListView")
    val activeOptionsList : ListView<String> by fxid("ActiveOptionsListOverView")

    val optionDescriptionArea : TextArea by fxid("OptionDescriptionArea")
    val optionValuePane : FlowPane by fxid("OptionValuePane")
    val optionValueField : TextField by fxid("OptionValueField")

    val textFieldsMaxChars: Int = 30

    /* RUN */
    val runGW2Button : Button by fxid("RunGW2Button")
    val gw2LocationField : TextField by fxid("GW2LocationField")

    init {

        runGW2Button.action {
            fire(OptionsRequest.SaveOptionsSettings())
//            fire(OptionsRequest.SaveAddOnsSettings())
        }

        val optLogic = OptionsDisplayLogic(this)
        val gfxLogic = GFXDisplayLogic(this)
    }
}