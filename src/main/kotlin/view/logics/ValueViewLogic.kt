package view.logics

import events.*
import javafx.event.EventHandler
import model.objects.GW2Argument
import model.utils.Nomenclatures
import model.utils.isStatusCorrect
import model.utils.isValueSettable
import tornadofx.*
import view.GW2SLMainView
import view.utils.IPAndRTTConverter
import view.utils.OptionValuesConversionMapper
import view.utils.SystemPathConverter

class ValueViewLogic(val view: GW2SLMainView) {

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
                if (argValueField.isValueSettable(lastClickedArg, textFieldsMaxChars)) {
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
                    is ArgumentsRequest.GetArgument -> with(it) {

                        if (argument.hasValue) {
                            view.argValueChoice.items = emptyObsListOfPair()
                            view.fire(ValuesRequest.GetArgValues(argument.name))
                        }

                        if(argument.hasValue && argument.isActive) {
                            view.argPaneHeader.show()
                            view.argValueField.text = argument.value
                        } else {
                            view.fire(ViewRequest.HideArgumentValueHeader())
                        }

                        argDescriptionArea.text = argument.description

                        lastClickedArg = argument
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

            subscribe<ViewRequest.CheckArgumentTextField> {
                fire(ViewEvent.TextFieldStatus(
                        it,
                        argValueField.isStatusCorrect(it.arg)
                ))
            }

            subscribe<ViewRequest.HideArgumentValueHeader> {
                view.argPaneHeader.hide()
                view.argValueField.clear()
                view.argValueChoice.items = emptyObsListOfPair()
            }
        }
    }
}