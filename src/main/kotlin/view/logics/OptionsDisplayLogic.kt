package view.logics

import controller.Logger
import events.*
import javafx.event.EventHandler
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import javafx.scene.control.TextField
import model.objects.GW2Argument
import tornadofx.*
import view.GW2StackLauncherView
import view.components.ListViewItem

class OptionsDisplayLogic(private val view: GW2StackLauncherView) {

    private var lastClickedOption = GW2Argument.Empty()

    class SetOptionValue : GenericRequest()
    class HideOptionValuePane : GenericRequest()

    init {

        with(view) {

            initHandlers()
            initSubscriptions()

            availableOptionsList.selectionModel.selectionMode = SelectionMode.SINGLE
            optionPaneHeader.hide()
            optionDescriptionArea.isEditable = false
        }
    }

    private fun initHandlers() {
        with(view) {

            availableOptionsList.onMouseClicked = EventHandler {
                val item = availableOptionsList.selectedItem
                if (item!= null) {
                    checkFieldStatusAndMaybeFire(optionValueField, lastClickedOption)
                    optionValueField.clear()
                    optionValueChoiceBox.items = listOf<String>().observable()
                    fire(OptionsRequest.GetOption(item.first))
                }
            }

            availableOptionsList.setCellFactory {
                ListViewItem(toggle = { id, status ->

                    checkFieldStatusAndMaybeFire(optionValueField, lastClickedOption)
                    selectAndFocus(availableOptionsList, id)
                    fire(OptionsRequest.UpdateOptionStatus(id, status))
                    fire(OptionsRequest.GetOption(id))
                })
            }

            optionValueField.textProperty().onChange {
                if (isValueFieldUpdateable(optionValueField)) {
//                    Logger.instance().info(optionValueField.text)
                    fire(OptionsRequest.UpdateOptionValue(lastClickedOption.name, optionValueField.text))
                }
            }

            activeOptionsList.onMouseClicked = EventHandler {
                val item = activeOptionsList.selectedItem
                if (item != null) {
                    val option = item.split(":").first()
                    gw2slTabPane.selectionModel.select(optionTab)
                    selectFocusAndScroll(availableOptionsList, option)
                    fire(OptionsRequest.GetOption(option))
                }
            }

            optionTab.setOnSelectionChanged {

                if (!optionTab.isSelected && checkFieldStatusAndMaybeFire(optionValueField, lastClickedOption)) {
                    fire(HideOptionValuePane())
                } else if (optionTab.isSelected) {
                    selectFocusAndScroll(availableOptionsList, lastClickedOption.name)
                }
            }

            optionValueChoiceBox.selectionModel.selectedItemProperty().onChange{
                if (it != null) {
                    optionValueField.text = it.split("@").first()
                }
            }

            optionValuesRefreshButton.onMouseClicked = EventHandler {
                log.info("FIRED")
                fire(OptionValuesRequest.GetOptionValues(lastClickedOption.name))
            }

        }
    }

    private fun initSubscriptions() {
        with(view) {

            subscribe<OptionsEvent.OptionsList> {
                when(it.from.signature) {
                    OptionsRequest.GetAvailableOptionsList::class.java.name -> {
                        availableOptionsList.items = it.options.map { arg -> arg.name to arg.isActive}.observable()
                    }
                    OptionsRequest.GetActiveOptionsList::class.java.name -> {
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
                    OptionsRequest.GetOption::class.java.name -> {

                        fire(OptionsEvent.Option(SetOptionValue(), it.option))

                        optionDescriptionArea.text = it.option.description

                        lastClickedOption = it.option
                    }
                    SetOptionValue::class.java.name -> {

                        if (it.option.hasValue) {
                            fire(OptionValuesRequest.GetOptionValues(it.option.name))
                        }

                        if(it.option.hasValue && it.option.isActive) {
                            optionPaneHeader.show()
                            optionValueField.text = it.option.value
                        } else {
                            fire(HideOptionValuePane())
                        }
                    }
                    else -> {
                        Logger.instance().error("Unknown request signature!")
                    }
                }
            }

            subscribe<OptionValuesEvent.OptionValues> {
                view.optionValueChoiceBox.items = it.values.sortedBy {
                    it.second
                }.map {
                    "${it.first}@${it.second}ms"
                }.observable()
            }

            subscribe<HideOptionValuePane> {
                optionValueChoiceBox.items = listOf<String>().observable()
                optionPaneHeader.hide()
                optionValueField.clear()
            }

        }
    }

    private fun selectAndFocus(list: ListView<Pair<String, Boolean>>, item: String) : Int{
        val idx = list.items.indexOfFirst { it.first == item }
        list.selectionModel.select(idx)
        list.focusModel.focus(idx)
        return idx
    }

    private fun selectFocusAndScroll(list: ListView<Pair<String, Boolean>>, item: String) : Int {
        val idx = selectAndFocus(list, item)
        list.scrollTo(if (idx > 1) idx - 1 else idx)
        return idx
    }

    private fun isValueFieldUpdateable(field: TextField): Boolean {
        return field.text.isNotEmpty() &&
                field.text.length < view.textFieldsMaxChars
                && lastClickedOption.hasValue && lastClickedOption.isActive
    }

    private fun checkFieldStatusAndMaybeFire(field: TextField, last: GW2Argument) : Boolean {
        return if (!isTextFieldStatusCorrect(field, last)) {
            view.fire(OptionsRequest.UpdateOptionStatus(last.name, isActive = false))
            true
        } else {
            false
        }
    }

    private fun isTextFieldStatusCorrect(field: TextField, last: GW2Argument) : Boolean {
        return !last.hasValue || last.hasValue && (field.text.isEmpty() && !last.isActive || !field.text.isEmpty() && last.isActive)
    }

}