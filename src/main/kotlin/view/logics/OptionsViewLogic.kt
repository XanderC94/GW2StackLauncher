package view.logics

import controller.Logger
import events.OptionValuesEvent
import events.OptionValuesRequest
import events.OptionsEvent
import events.OptionsRequest
import javafx.event.EventHandler
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import model.objects.GW2Argument
import model.utils.Nomenclatures.Options
import tornadofx.*
import view.GW2StackLauncherView
import view.components.ListViewItem
import view.utils.IPAndRTTConverter
import view.utils.OptionValuesConversionMapper
import view.utils.SystemPathConverter

class OptionsViewLogic(private val view: GW2StackLauncherView) {

    private var lastClickedOption = GW2Argument()
    private val emptyObsListOfPair = {listOf<Pair<String, String>>().observable()}

    init {

        with(view) {

            initHandlers()
            initSubscriptions()

            val ovcm = OptionValuesConversionMapper()
            ovcm.setMapping(Options.authserv, IPAndRTTConverter(), IPAndRTTConverter)
            ovcm.setMapping(Options.assertserv, IPAndRTTConverter(), IPAndRTTConverter)
            ovcm.setMapping(Options.dat, SystemPathConverter(), SystemPathConverter)

            optionValueChoiceBox.converter = ovcm
        }
    }

    private fun initHandlers() {
        with(view) {

            availableOptionsList.onMouseClicked = EventHandler {
                val item = availableOptionsList.selectedItem
                if (item!= null) {
                    checkFieldStatusAndMaybeFire(optionValueField, lastClickedOption)
                    optionValueField.clear()
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
                    fire(OptionsRequest.UpdateOptionValue(lastClickedOption.name, optionValueField.text))
                }
            }

            activeOptionsList.onMouseClicked = EventHandler {
                val item = activeOptionsList.selectedItem
                if (item != null) {
                    val option = item.split(":").first()
                    optionTab.select()
                    selectFocusAndScroll(availableOptionsList, option)
                    fire(OptionsRequest.GetOption(option))
                }
            }

            optionTab.setOnSelectionChanged {

                if (!optionTab.isSelected && checkFieldStatusAndMaybeFire(optionValueField, lastClickedOption)) {
                    hideOptionValueHeader()
                } else if (optionTab.isSelected) {
                    selectFocusAndScroll(availableOptionsList, lastClickedOption.name)
                }
            }

            optionValueChoiceBox.selectionModel.selectedItemProperty().onChange{
                if (it != null) {
                    optionValueField.text = it.first
                }
            }

            optionValuesRefreshButton.onMouseClicked = EventHandler {
                optionValueChoiceBox.items = emptyObsListOfPair()
                fire(OptionValuesRequest.GetOptionValues(lastClickedOption.name))
            }

        }
    }

    private fun initSubscriptions() {
        with(view) {

            subscribe<OptionsEvent.OptionsList> {
                when(it.from) {
                    is OptionsRequest.GetAvailableOptionsList -> {
                        availableOptionsList.items = it.options.map { arg -> arg.name to arg.isActive}.observable()
                    }
                    is OptionsRequest.GetActiveOptionsList -> {
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
                when(it.from) {
                    is OptionsRequest.GetOption -> {

                        setOptionValue(it.option)

                        optionDescriptionArea.text = it.option.description

                        lastClickedOption = it.option
                    }
                    else -> { }
                }
            }

            subscribe<OptionValuesEvent.OptionValues<Pair<String, Number>>> { r ->

                view.optionValueChoiceBox.items = r.values
                        .map{ it.first to it.second.toString() }
                        .sortedBy { it.second }
                        .observable()
            }

            subscribe<OptionValuesEvent.OptionValue<Pair<String, Number>>> { r ->
                val items = view.optionValueChoiceBox.items.toMutableSet()
                if (!items.map { i -> i.first }.contains(r.value.first)) {
                    items.add(r.value.first to r.value.second.toString())
                    view.optionValueChoiceBox.items = items.toList()
                            .sortedBy { it.second.toLong() }.observable()
                }
            }
        }
    }

    private fun hideOptionValueHeader() {
        view.optionPaneHeader.hide()
        view.optionValueField.clear()
        view.optionValueChoiceBox.items = emptyObsListOfPair()
    }

    private fun setOptionValue(option: GW2Argument) {
        if (option.hasValue) {
            view.optionValueChoiceBox.items = emptyObsListOfPair()
            view.fire(OptionValuesRequest.GetOptionValues(option.name))
        }

        if(option.hasValue && option.isActive) {
            view.optionPaneHeader.show()
            view.optionValueField.text = option.value
        } else {
            hideOptionValueHeader()
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