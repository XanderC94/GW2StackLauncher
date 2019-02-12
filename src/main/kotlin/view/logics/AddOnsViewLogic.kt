package view.logics

import events.AddOnsEvent
import events.AddOnsRequest
import events.BrowserRequest
import javafx.event.EventHandler
import javafx.scene.control.ListView
import model.objects.GW2AddOn
import tornadofx.*
import view.GW2SLMainView
import view.components.ListViewItem


class AddOnsViewLogic(val view: GW2SLMainView) {

    private var lastClickedAvail = GW2AddOn()
    private var disableTabbing = false

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
                    disableTabbing = true
                    availableAddOnsList.selectFocusAndScroll(to = item)
                    addOnsTab.select()
                }
            }

            availableAddOnsList.setCellFactory {
                ListViewItem(onToggle = { id, status ->
                    fire(AddOnsRequest.UpdateAddOnStatus(id, status))
                    availableAddOnsList.selectAndFocus(to = id)
                })
            }

            addOnsTab.onSelectionChanged = EventHandler {
                val item = availableAddOnsList.selectedItem
                if (addOnsTab.isSelected && !disableTabbing) {
                    availableAddOnsList.selectFocusAndScroll(to = lastClickedAvail.name)
                } else {
                    disableTabbing = false
                }
            }
        }
    }

    private fun ListView<Pair<String, Boolean>>.selectAndFocus(to: String) : Int{
        val idx = this.items.indexOfFirst { it.first == to }
        this.selectionModel.select(idx)
        this.focusModel.focus(idx)
        view.fire(AddOnsRequest.GetAddOn(to))
        return idx
    }

    private fun ListView<Pair<String, Boolean>>.selectFocusAndScroll(to: String) : Int {
        val idx = this.selectAndFocus(to)
        this.scrollTo(if (idx > 1) idx - 1 else idx)
        return idx
    }

    private fun subscriptions() {
        with(view) {
            subscribe<AddOnsEvent.AddOnsList> {
                when(it.from) {
                    is AddOnsRequest.GetAvailableAddOns -> {
                        availableAddOnsList.items = it.addOns.map { it.name to it.isActive }
                                .sortedBy { it.first.toLowerCase() }.observable()
                    }
                    is AddOnsRequest.GetActiveAddOns -> {
                        activeAddOnsList.items = it.addOns.map { it.name }
                                .sortedBy { it.toLowerCase() }.observable()
                    }
                }
            }

            subscribe<AddOnsEvent.AddOn> {
                lastClickedAvail = it.addOn
                fire(BrowserRequest.LoadURL(it.addOn.info))
            }
        }
    }
}