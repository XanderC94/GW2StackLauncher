package view

import controller.Logger
import events.OptionsEvent
import events.OptionsRequest
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import model.json.objects.GW2Argument
import tornadofx.*

class GW2StackLauncherView : View("GW2 Stack Launcher") {

    override val root: BorderPane by fxml()

    private val availableOptionsList : ListView<Pair<String, Boolean>> by fxid("OptionsListView")
    private val activeOptionsList : ListView<String> by fxid("ActiveOptionsListOverView")

    private val optionDescriptionArea : TextArea by fxid("OptionDescriptionArea")
    private val optionValuePane : FlowPane by fxid("OptionValuePane")
    private val optionValueField : TextField by fxid("OptionValueField")

    private val textFieldsMaxChars: Int = 30

    private val runGW2Button : Button by fxid("RunGW2Button")

    init {

        var lastClickedOption : GW2Argument = GW2Argument.Empty()

        optionValuePane.visibleProperty().set(false)
        optionDescriptionArea.editableProperty().set(false)

        availableOptionsList.setOnMouseClicked {
            if (!isTextFieldStatusCorrect(optionValueField, lastClickedOption)) {
                fire(OptionsRequest.UpdateOptionStatus(lastClickedOption.name, isActive = false))
            }

            optionValueField.clear()
            fire(OptionsRequest.UpdateOptionInfoDisplay(
                    availableOptionsList.selectionModel.selectedItem.first
            ))

        }

        availableOptionsList.setCellFactory {
            ListViewItem(toggle = { id, status ->

                val itemIdx = availableOptionsList.items.indexOfFirst {
                    it.first == id
                }

                fire(OptionsRequest.UpdateOptionStatus(id, status))
                fire(OptionsRequest.UpdateOptionInfoDisplay(id))
            })
        }

        optionValueField.textProperty().onChange {
            if (optionValueField.text.isNotEmpty() &&
                    optionValueField.text.length < textFieldsMaxChars
                    && lastClickedOption.hasValue && lastClickedOption.isActive) {
                Logger.instance().info(optionValueField.text)
                fire(OptionsRequest.UpdateOptionValue(lastClickedOption.name, optionValueField.text))
            }
        }

        runGW2Button.action {
            fire(OptionsRequest.SaveOptionsSettings())
//            fire(OptionsRequest.SaveAddOnsSettings())
        }

        subscribe<OptionsEvent.OptionsList> {
            when(it.from.signature) {
                OptionsRequest.UpdateAvailableOptionsList::class.java.name -> {
                    availableOptionsList.items = it.options.map { arg -> arg.name to arg.isActive}.observable()
                }
                OptionsRequest.UpdateActiveOptionsList::class.java.name -> {
                    activeOptionsList.setItems(it.options.map {
                        arg -> if (arg.hasValue) "${arg.name}:${arg.value}" else arg.name
                    }.observable())
                }
                else -> {
                    Logger.instance().error("Unknown request signature!")
                }
            }
        }

        subscribe<OptionsEvent.Option> {
            when(it.from.signature) {
                OptionsRequest.UpdateOptionInfoDisplay::class.java.name -> {

                    if (it.option.isActive && it.option.hasValue) {
                        optionValuePane.visibleProperty().set(true)
                        optionValueField.text = it.option.value
                    } else {
                        optionValuePane.visibleProperty().set(false)
                    }

                    optionDescriptionArea.text = it.option.description

                    lastClickedOption = it.option
                }
                else -> {
                    Logger.instance().error("Unknown request signature!")
                }
            }

        }

        fire(OptionsRequest.UpdateAvailableOptionsList())
        fire(OptionsRequest.UpdateActiveOptionsList())
    }

    private fun isTextFieldStatusCorrect(field: TextField, last: GW2Argument) : Boolean {
        return !last.hasValue || last.hasValue && (field.text.isEmpty() && !last.isActive || !field.text.isEmpty() && last.isActive)
    }
}
