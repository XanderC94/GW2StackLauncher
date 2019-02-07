package view.logics

import events.AddOnsEvent
import events.AddOnsRequest
import javafx.event.EventHandler
import javafx.scene.control.ListView
import model.objects.GW2AddOn
import tornadofx.*
import view.GW2StackLauncherView
import view.components.ListViewItem


class AddOnsViewLogic(val view: GW2StackLauncherView) {

    private var lastClickedAddOn : GW2AddOn = GW2AddOn()

    init {
        handlers()
        subscriptions()
    }

    private fun handlers() {

        with(view) {
            availableAddOnsList.onMouseClicked = EventHandler {
                val item = availableAddOnsList.selectedItem
                if (item != null) {
                    fire(AddOnsRequest.GetAddOn(item.first))
                }
            }

            activeAddOnsList.onMouseClicked = EventHandler {
                val item = activeAddOnsList.selectedItem
                if (item != null) {
                    addOnsTab.select()
                    selectFocusAndScroll(availableAddOnsList, item)
                    fire(AddOnsRequest.GetAddOn(item))
                }
            }

            availableAddOnsList.setCellFactory {
                ListViewItem(toggle = { id, status ->
                    selectAndFocus(availableArgsList, id)
                    fire(AddOnsRequest.UpdateAddOnStatus(id, status))
                    fire(AddOnsRequest.GetAddOn(id))
                })
            }

            addOnsTab.onSelectionChanged = EventHandler {
                if (!addOnsTab.isSelected) {
                    fire(AddOnsRequest.ClearWebView())
                } else if (argumentsTab.isSelected) {
                    selectFocusAndScroll(availableAddOnsList, lastClickedAddOn.name)
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

    private fun subscriptions() {
        with(view) {
            subscribe<AddOnsEvent.AddOnsList> {
                when(it.from) {
                    is AddOnsRequest.GetAvailableAddOns -> {
                        availableAddOnsList.items = it.addOns.map { it.name to it.isActive }
                                .sortedBy { it.first }.observable()
                    }
                    is AddOnsRequest.GetActiveAddOns -> {
                        activeAddOnsList.items = it.addOns.map { it.name }.sorted().observable()
                    }
                }
            }

            subscribe<AddOnsEvent.AddOn> {
                lastClickedAddOn = it.addOn
            }
        }
    }
}