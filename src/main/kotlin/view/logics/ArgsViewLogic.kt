package view.logics

import events.ArgumentsEvent
import events.ArgumentsRequest
import events.ViewEvent
import events.ViewRequest
import javafx.event.EventHandler
import javafx.scene.control.ListView
import model.objects.GW2Argument
import tornadofx.*
import view.GW2SLMainView
import view.components.ListViewItem

class ArgsViewLogic(private val view: GW2SLMainView) {

    private var lastClickedAvail = GW2Argument()
    private var disableTabbing = false

    init {
        initHandlers()
        initSubscriptions()
    }

    private fun initHandlers() {
        with(view) {

            availableArgsList.onMouseClicked = EventHandler {
                val item = availableArgsList.selectedItem
                if (item!= null) {
                    fire(ViewRequest.CheckArgumentTextField(lastClickedAvail))
                    fire(ArgumentsRequest.GetArgument(item.first))
                }
            }

            availableArgsList.setCellFactory {
                ListViewItem(onToggle = { id, status ->

                    fire(ViewRequest.CheckArgumentTextField(lastClickedAvail))
                    availableArgsList.selectAndFocus(id)
                    fire(ArgumentsRequest.UpdateArgumentStatus(id, status))
                })
            }

            activeArgsList.onMouseClicked = EventHandler {
                val item = activeArgsList.selectedItem
                if (item != null) {
                    disableTabbing = true
                    availableArgsList.selectFocusAndScroll(item.split(":").first())
                    argumentsTab.select()
                }
            }

            argumentsTab.setOnSelectionChanged {

                if (!argumentsTab.isSelected) {
                    fire(ViewRequest.CheckArgumentTextField(lastClickedAvail))
                    fire(ViewRequest.HideArgumentValueHeader())
                } else if (argumentsTab.isSelected && !disableTabbing) {
                    availableArgsList.selectFocusAndScroll(lastClickedAvail.name)
                } else {
                    disableTabbing = false
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
                        lastClickedAvail = it.argument
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

    private fun ListView<Pair<String, Boolean>>.selectAndFocus(to: String) : Int{
        val idx = this.items.indexOfFirst { it.first == to }
        this.selectionModel.select(idx)
        this.focusModel.focus(idx)
        view.fire(ArgumentsRequest.GetArgument(to))
        return idx
    }

    private fun ListView<Pair<String, Boolean>>.selectFocusAndScroll(to: String) : Int {
        val idx = this.selectAndFocus(to)
        this.scrollTo(if (idx > 1) idx - 1 else idx)
        return idx
    }
}