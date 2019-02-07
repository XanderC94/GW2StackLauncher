package view.logics

import events.ArgumentsEvent
import events.ArgumentsRequest
import events.ValuesEvent
import events.ValuesRequest
import javafx.event.EventHandler
import javafx.scene.control.TextField
import model.objects.GW2Argument
import model.utils.Nomenclatures
import tornadofx.*
import view.GW2StackLauncherView
import view.utils.IPAndRTTConverter
import view.utils.OptionValuesConversionMapper
import view.utils.SystemPathConverter

class ValueViewLogic(val view: GW2StackLauncherView) {

    private var lastClickedArg = GW2Argument()
    private val emptyObsListOfPair = {listOf<Pair<String, String>>().observable()}

    init {

        with(view) {

            argPaneHeader.hide()

            initHandlers()
            initSubscriptions()

            val ovcm = OptionValuesConversionMapper()
            ovcm.setMapping(Nomenclatures.Argument.authserv, IPAndRTTConverter(), IPAndRTTConverter)
            ovcm.setMapping(Nomenclatures.Argument.assetserv, IPAndRTTConverter(), IPAndRTTConverter)
            ovcm.setMapping(Nomenclatures.Argument.dat, SystemPathConverter(), SystemPathConverter)

            argValueChoice.converter = ovcm
        }
    }

    private fun initHandlers() {
        with(view) {

            argValueField.textProperty().onChange {
                if (isValueFieldUpdateable(argValueField)) {
                    fire(ArgumentsRequest.UpdateArgumentValue(lastClickedArg.name, argValueField.text))
                }
            }

            argValueChoice.selectionModel.selectedItemProperty().onChange{
                if (it != null) {
                    argValueField.text = it.first
                }
            }

            valuesRefreshButton.onMouseClicked = EventHandler {
                argValueChoice.items = emptyObsListOfPair()
                fire(ValuesRequest.GetArgValues(lastClickedArg.name))
            }

        }
    }

    private fun initSubscriptions() {
        with(view) {

            subscribe<ArgumentsEvent.Argument> {
                when(it.from) {
                    is ArgumentsRequest.GetArgument -> {

                        setOptionValue(it.argument)

                        argDescriptionArea.text = it.argument.description

                        lastClickedArg = it.argument
                    }
                }
            }

            subscribe<ValuesEvent.ArgValues<Pair<String, Number>>> { r ->

                view.argValueChoice.items = r.values
                        .map{ it.first to it.second.toString() }
                        .sortedBy { it.second }
                        .observable()
            }

            subscribe<ValuesEvent.ArgValue<Pair<String, Number>>> { r ->
                val items = view.argValueChoice.items.toMutableSet()
                if (r.arg == lastClickedArg.name &&
                        !items.map { i -> i.first }.contains(r.value.first)) {

                    items.add(r.value.first to r.value.second.toString())
                    view.argValueChoice.items = items.toList()
                            .sortedBy { it.second.toLong() }.observable()
                }
            }
        }
    }

    private fun hideOptionValueHeader() {
        view.argPaneHeader.hide()
        view.argValueField.clear()
        view.argValueChoice.items = emptyObsListOfPair()
    }

    private fun setOptionValue(option: GW2Argument) {
        if (option.hasValue) {
            view.argValueChoice.items = emptyObsListOfPair()
            view.fire(ValuesRequest.GetArgValues(option.name))
        }

        if(option.hasValue && option.isActive) {
            view.argPaneHeader.show()
            view.argValueField.text = option.value
        } else {
            hideOptionValueHeader()
        }
    }

    private fun isValueFieldUpdateable(field: TextField): Boolean {
        return field.text.isNotEmpty() &&
                field.text.length < view.textFieldsMaxChars
                && lastClickedArg.hasValue && lastClickedArg.isActive
    }
}