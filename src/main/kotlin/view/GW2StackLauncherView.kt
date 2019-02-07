package view

import events.AddOnsRequest
import events.AppRequest
import events.OptionsRequest
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.web.WebView
import model.utils.Nomenclatures.Components
import model.utils.Nomenclatures.Directories
import model.utils.Nomenclatures.Files
import tornadofx.*
import view.logics.*

class GW2StackLauncherView : View("GW2 Stack Launcher") {

    override val root: BorderPane by fxml()

    val textFieldsMaxChars: Int = 30

    /* TAB */

//    val gw2slTabPane : TabPane by fxid(Components.gw2slTabPane)
//    val overviewTab : Tab by fxid(Components.overviewTab)
    val optionTab : Tab by fxid(Components.optionTab)
    val addOnsTab : Tab by fxid(Components.addOnsTab)

    /* SPLIT PANES */
    /* OPTIONS */
    val availableOptionsList : ListView<Pair<String, Boolean>>
            by fxid(Components.availableOptionsList)

    val activeOptionsList : ListView<String>
            by fxid(Components.activeOptionsList)

    val optionDescriptionArea : TextArea
            by fxid(Components.optionDescriptionArea)

    val optionPaneHeader : FlowPane
            by fxid(Components.optionPaneHeader)

//    val optionValuePane : FlowPane
//            by fxid(Components.optionValuePane)

    val optionValueField : TextField
            by fxid(Components.optionValueField)

    val optionValueChoiceBox : ChoiceBox<Pair<String, String>>
            by fxid(Components.optionValueChoiceBox)

    val optionValuesRefreshButton : Button
            by fxid(Components.optionValuesRefreshButton)

    /* ADD-ONS */

    val availableAddOnsList : ListView<Pair<String, Boolean>>
            by fxid(Components.availableAddOnsList)

    val activeAddOnsList : ListView<String>
            by fxid(Components.activeAddOnsList)

    val addOnsWebView : WebView by fxid(Components.addOnsWebView)

    /* ABOUT */
    val iconsFlowPane : FlowPane by fxid(Components.iconsFlowPane)

    /* RUN */

    val gw2slRunPane : FlowPane by fxid(Components.gw2slRunPane)

    val runGW2Button : Button
            by fxid(Components.runGW2Button)

    val gw2LocationField : TextField
            by fxid(Components.gw2LocationField)

    init {

        runGW2Button.action {
            fire(OptionsRequest.SaveOptionsSettings())
            fire(AddOnsRequest.SaveAddOnsSettings())
        }

        primaryStage.onCloseRequest = EventHandler {
            fire(AppRequest.CloseApplication(1))
        }

        val icon = this.javaClass.getResourceAsStream(Directories.icon + Files.GW2SLIcon)

        currentStage?.icons?.add(Image(icon))

        currentStage?.isResizable = false
        currentStage?.minWidth = 800.0
        currentStage?.minHeight = 600.0

        availableOptionsList.selectionModel.selectionMode = SelectionMode.SINGLE
        availableAddOnsList.selectionModel.selectionMode = SelectionMode.SINGLE
        activeOptionsList.selectionModel.selectionMode = SelectionMode.SINGLE
        activeAddOnsList.selectionModel.selectionMode = SelectionMode.SINGLE

        availableOptionsList.minWidth = 250.0
        availableAddOnsList.minWidth = 250.0
        activeOptionsList.minWidth = 250.0
        activeAddOnsList.minWidth = 250.0

        optionPaneHeader.hide()
        optionDescriptionArea.isEditable = false

//        addOnsWebView.minWidth = 700.0

        val optLogic = OptionsViewLogic(this)
        val gfxLogic = GFXViewLogic(this)
        val aboutLogic = AboutViewLogic(this)
        val addOnsViewLogic = AddOnsViewLogic(this)
        val webViewLogic = WebViewLogic(this)

    }
}