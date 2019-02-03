package view

import controller.Logger
import events.OptionsEvent
import events.OptionsRequest
import javafx.scene.control.TextField
import model.objects.GW2Argument
import tornadofx.*

class OptionsDisplayLogic(private val view: GW2StackLauncherView) {

    private var lastClickedOption = GW2Argument.Empty()

    init {

        with(view) {

            initHandlers()
            initSubscriptions()

            optionValuePane.visibleProperty().set(false)
            optionDescriptionArea.editableProperty().set(false)

            fire(OptionsRequest.UpdateAvailableOptionsList())
            fire(OptionsRequest.UpdateActiveOptionsList())
        }
    }

    private fun initHandlers() {
        with(view) {

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

        }
    }

    private fun initSubscriptions() {
        with(view) {

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
        }
    }

    private fun isTextFieldStatusCorrect(field: TextField, last: GW2Argument) : Boolean {
        return !last.hasValue || last.hasValue && (field.text.isEmpty() && !last.isActive || !field.text.isEmpty() && last.isActive)
    }

}