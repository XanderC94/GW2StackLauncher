package view

import events.AddOnsRequest
import events.AppRequest
import events.ArgumentsRequest
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import model.utils.Nomenclatures.Component
import model.utils.Nomenclatures.Directory
import model.utils.Nomenclatures.File
import model.utils.getResourceAsStream
import tornadofx.*
import view.components.KeyAction
import view.logics.*

class GW2SLMainView : View("GW2 Stack Launcher") {

    override val root: BorderPane by fxml()

    val textFieldsMaxChars: Int = 30

    /* TAB */

    val argumentsTab : Tab by fxid(Component.optionTab)
    val addOnsTab : Tab by fxid(Component.addOnsTab)
    val aboutTab : Tab by fxid(Component.aboutTab)

    /* SPLIT PANES */
    /* OPTIONS */
    val availableArgsList : ListView<Pair<String, Boolean>>
            by fxid(Component.availableArgsList)

    val activeArgsList : ListView<String>
            by fxid(Component.activeArgsList)

    val argDescriptionArea : TextArea
            by fxid(Component.argDescriptionArea)

    val argPaneHeader : FlowPane
            by fxid(Component.argsPaneHeader)

    val argValueField : TextField
            by fxid(Component.argsValueField)

    val argValueChoice : ChoiceBox<Pair<String, String>>
            by fxid(Component.argsValueChoiceBox)

    val valuesRefreshButton : Button
            by fxid(Component.valuesRefreshButton)

    /* ADD-ONS */

    val availableAddOnsList : ListView<Pair<String, Boolean>>
            by fxid(Component.availableAddOnsList)

    val activeAddOnsList : ListView<String>
            by fxid(Component.activeAddOnsList)

    val webViewAnchor : AnchorPane by fxid(Component.webViewAnchor)

    /* ABOUT */
    val iconsFlowPane : FlowPane by fxid(Component.iconsFlowPane)
    val aboutAnchor : AnchorPane by fxid(Component.aboutAnchor)

    /* RUN */

    val gw2slRunPane : FlowPane by fxid(Component.gw2slRunPane)

    val runGW2Button : Button
            by fxid(Component.runGW2Button)

    val gw2LocationField : TextField
            by fxid(Component.gw2LocationField)

    init {

        runGW2Button.action {
            fire(ArgumentsRequest.SaveArgumentsSettings())
            fire(AddOnsRequest.SaveAddOnsSettings())
        }

        primaryStage.onCloseRequest = EventHandler {
            fire(AppRequest.CloseApplication(1))
        }

        root.onKeyPressed = EventHandler {
            KeyAction(this.primaryStage, it)
        }

        val icon = this.getResourceAsStream("${Directory.icon}/${File.GW2SLIcon}")

        currentStage?.icons?.add(Image(icon))

        currentStage?.isResizable = false

        availableArgsList.selectionModel.selectionMode = SelectionMode.SINGLE
        availableAddOnsList.selectionModel.selectionMode = SelectionMode.SINGLE
        activeArgsList.selectionModel.selectionMode = SelectionMode.SINGLE
        activeAddOnsList.selectionModel.selectionMode = SelectionMode.SINGLE

        val argsLogic = ArgsViewLogic(this)
        val valueLogic = ValueViewLogic(this)
        val gfxLogic = GFXViewLogic(this)
        val aboutLogic = AboutViewLogic(this)
        val addOnsViewLogic = AddOnsViewLogic(this)
        val webViewLogic = BrowserViewLogic(this)

        log.info("${this.javaClass.simpleName} READY")

        fire(AppRequest.InitViewElements())

    }
}