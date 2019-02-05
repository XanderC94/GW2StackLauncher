package view

import events.OptionsRequest
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import model.utils.Nomenclatures
import tornadofx.*
import view.logics.AboutDisplayLogic
import view.logics.GFXDisplayLogic
import view.logics.OptionsDisplayLogic

class GW2StackLauncherView : View("GW2 Stack Launcher") {

    override val root: BorderPane by fxml()

    val textFieldsMaxChars: Int = 30

    /* TAB */

    val gw2slTabPane : TabPane by fxid(Nomenclatures.View.gw2slTabPane)
    val overviewTab : Tab by fxid(Nomenclatures.View.overviewTab)
    val optionTab : Tab by fxid(Nomenclatures.View.optionTab)
    val addOnsTab : Tab by fxid(Nomenclatures.View.addOnsTab)

    /* OPTIONS */
    val availableOptionsList : ListView<Pair<String, Boolean>>
            by fxid(Nomenclatures.View.availableOptionsList)

    val activeOptionsList : ListView<String>
            by fxid(Nomenclatures.View.activeOptionsList)

    val optionDescriptionArea : TextArea
            by fxid(Nomenclatures.View.optionDescriptionArea)

    val optionPaneHeader : FlowPane
            by fxid(Nomenclatures.View.optionPaneHeader)

//    val optionValuePane : FlowPane
//            by fxid(Nomenclatures.View.optionValuePane)

    val optionValueField : TextField
            by fxid(Nomenclatures.View.optionValueField)

    val optionValueChoiceBox : ChoiceBox<String>
            by fxid(Nomenclatures.View.optionValueChoiceBox)

    val optionValuesRefreshButton : Button
            by fxid(Nomenclatures.View.optionValuesRefreshButton)

    /* ABOUT */
    val iconsFlowPane : FlowPane by fxid(Nomenclatures.View.iconsFlowPane)

    /* RUN */
    val runGW2Button : Button
            by fxid(Nomenclatures.View.runGW2Button)

    val gw2LocationField : TextField
            by fxid(Nomenclatures.View.gw2LocationField)

    init {

        runGW2Button.action {
            fire(OptionsRequest.SaveOptionsSettings())
//            fire(OptionsRequest.SaveAddOnsSettings())
        }

        currentStage?.isResizable = false

        val optLogic = OptionsDisplayLogic(this)
        val gfxLogic = GFXDisplayLogic(this)
        val aboutLogic = AboutDisplayLogic(this)

        val input = this.javaClass.getResourceAsStream(Nomenclatures.Path.icon + Nomenclatures.File.GW2SLIcon)

        currentStage?.icons?.add(Image(input))
    }
}