package view

import events.AppRequest
import events.OptionsRequest
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.web.WebView
import model.utils.Nomenclatures
import tornadofx.*
import view.logics.AboutViewLogic
import view.logics.AddOnsViewLogic
import view.logics.GFXViewLogic
import view.logics.OptionsViewLogic

class GW2StackLauncherView : View("GW2 Stack Launcher") {

    override val root: BorderPane by fxml()

    val textFieldsMaxChars: Int = 30

    /* TAB */

    val gw2slTabPane : TabPane by fxid(Nomenclatures.Components.gw2slTabPane)
    val overviewTab : Tab by fxid(Nomenclatures.Components.overviewTab)
    val optionTab : Tab by fxid(Nomenclatures.Components.optionTab)
    val addOnsTab : Tab by fxid(Nomenclatures.Components.addOnsTab)

    /* OPTIONS */
    val availableOptionsList : ListView<Pair<String, Boolean>>
            by fxid(Nomenclatures.Components.availableOptionsList)

    val activeOptionsList : ListView<String>
            by fxid(Nomenclatures.Components.activeOptionsList)

    val optionDescriptionArea : TextArea
            by fxid(Nomenclatures.Components.optionDescriptionArea)

    val optionPaneHeader : FlowPane
            by fxid(Nomenclatures.Components.optionPaneHeader)

//    val optionValuePane : FlowPane
//            by fxid(Nomenclatures.Components.optionValuePane)

    val optionValueField : TextField
            by fxid(Nomenclatures.Components.optionValueField)

    val optionValueChoiceBox : ChoiceBox<Pair<String, String>>
            by fxid(Nomenclatures.Components.optionValueChoiceBox)

    val optionValuesRefreshButton : Button
            by fxid(Nomenclatures.Components.optionValuesRefreshButton)

    /* ADD-ONS */

    val availableAddOnsList : ListView<Pair<String, Boolean>>
            by fxid(Nomenclatures.Components.availableAddOnsList)

    val activeAddOnsList : ListView<String>
            by fxid(Nomenclatures.Components.activeAddOnsList)

    val addOnsWebView : WebView by fxid(Nomenclatures.Components.addOnsWebView)

    /* ABOUT */
    val iconsFlowPane : FlowPane by fxid(Nomenclatures.Components.iconsFlowPane)

    /* RUN */
    val runGW2Button : Button
            by fxid(Nomenclatures.Components.runGW2Button)

    val gw2LocationField : TextField
            by fxid(Nomenclatures.Components.gw2LocationField)

    init {

        runGW2Button.action {
            fire(OptionsRequest.SaveOptionsSettings())
//            fire(OptionsRequest.SaveAddOnsSettings())
        }

        primaryStage.onCloseRequest = EventHandler {
            fire(AppRequest.CloseApplication(1))
        }

        currentStage?.isResizable = false
        availableOptionsList.selectionModel.selectionMode = SelectionMode.SINGLE
        optionPaneHeader.hide()
        optionDescriptionArea.isEditable = false

        val icon = this.javaClass.getResourceAsStream(Nomenclatures.Directories.icon + Nomenclatures.Files.GW2SLIcon)

        currentStage?.icons?.add(Image(icon))

        val optLogic = OptionsViewLogic(this)
        val gfxLogic = GFXViewLogic(this)
        val aboutLogic = AboutViewLogic(this)
        val addOnsViewLogic = AddOnsViewLogic(this)

    }
}