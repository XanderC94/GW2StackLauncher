package view.logics

import events.ArgumentsEvent
import events.ArgumentsRequest
import events.ViewEvent
import events.ViewRequest
import javafx.event.EventHandler
import javafx.scene.control.ListView
import model.objects.GW2Argument
import tornadofx.*
import view.GW2StackLauncherView
import view.components.ListViewItem

class ArgsViewLogic(private val view: GW2StackLauncherView) {

    private var lastClickedArg = GW2Argument()
    private val emptyObsListOfPair = {listOf<Pair<String, String>>().observable()}

    init {
        initHandlers()
        initSubscriptions()
    }

    private fun initHandlers() {
        with(view) {

            availableArgsList.onMouseClicked = EventHandler {
                val item = availableArgsList.selectedItem
                if (item!= null) {
//                    checkFieldStatusAndMaybeFire(argValueField, lastClickedArg)
                    fire(ViewRequest.CheckArgumentTextField(lastClickedArg))
//                    argValueField.clear()
                    fire(ArgumentsRequest.GetArgument(item.first))
                }
            }

            availableArgsList.setCellFactory {
                ListViewItem(onToggle = { id, status ->

//                    checkFieldStatusAndMaybeFire(argValueField, lastClickedArg)
                    fire(ViewRequest.CheckArgumentTextField(lastClickedArg))
                    selectAndFocus(availableArgsList, id)
                    fire(ArgumentsRequest.UpdateArgumentStatus(id, status))
                    fire(ArgumentsRequest.GetArgument(id))
                })
            }

            activeArgsList.onMouseClicked = EventHandler {
                val item = activeArgsList.selectedItem
                if (item != null) {
                    val option = item.split(":").first()
                    argumentsTab.select()
                    selectFocusAndScroll(availableArgsList, option)
                    fire(ArgumentsRequest.GetArgument(option))
                }
            }

            argumentsTab.setOnSelectionChanged {

                if (!argumentsTab.isSelected
//                        && checkFieldStatusAndMaybeFire(argValueField, lastClickedArg)
                ) {
//                    hideOptionValueHeader()
                    fire(ViewRequest.CheckArgumentTextField(lastClickedArg))
                    fire(ViewRequest.HideArgumentValueHeader())
                } else if (argumentsTab.isSelected) {
                    selectFocusAndScroll(availableArgsList, lastClickedArg.name)
                }
            }
        }
    }

    private fun initSubscriptions() {
        with(view) {

            subscribe<ArgumentsEvent.ArgumentsList> {
                when(it.from) {
                    is ArgumentsRequest.GetAvailableArguments -> {
                        availableArgsList.items = it.options.map { arg -> arg.name to arg.isActive}
                                .sortedBy { it.first }.observable()
                    }
                    is ArgumentsRequest.GetActiveArguments -> {
                        activeArgsList.setItems(it.options.map {
                            arg -> if (arg.hasValue) "${arg.name}:${arg.value}" else arg.name
                        }.sorted().observable())
                    }
                }
            }

            subscribe<ArgumentsEvent.Argument> {
                when(it.from) {
                    is ArgumentsRequest.GetArgument -> {

                        lastClickedArg = it.argument
                    }
                }
            }

            subscribe<ViewEvent.TextFieldStatus> {
                if (it.from is ViewRequest.CheckArgumentTextField && !it.ok) {
                    view.fire(ArgumentsRequest.UpdateArgumentStatus(it.from.arg.name, isActive = false))
                }
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

}